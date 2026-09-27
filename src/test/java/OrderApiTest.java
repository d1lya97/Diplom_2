import io.qameta.allure.Description;
import io.restassured.http.ContentType;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderApiTest {

    private static final String BASE = "https://stellarburgers.education-services.ru/api";
    private static final String ING1 = "61c0c5a71d1f82001bdaaa6d";  // Флюоресцентная булка R2-D3
    private static final String ING2 = "61c0c5a71d1f82001bdaaa72";  // Соус Spicy-X

    private String getToken() {
        String email = "order" + System.currentTimeMillis() + "@yandex.ru";
        String body = "{\"email\":\"" + email + "\",\"password\":\"pass123\",\"name\":\"Tester\"}";
        return given().contentType(ContentType.JSON).body(body)
                .post(BASE + "/auth/register")
                .then().extract().path("accessToken");
    }

    @Test
    @Description("Создание заказа с авторизацией и ингредиентами")
    public void createOrderWithAuthAndIngredients() {
        String token = getToken();
        String body = "{\"ingredients\":[\"" + ING1 + "\",\"" + ING2 + "\"]}";

        given().contentType(ContentType.JSON)
                .header("Authorization", token)
                .body(body)
                .when().post(BASE + "/orders")
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Создание заказа без авторизации. По документации ожидается 401, " +
            "но стенд фактически принимает заказ и возвращает 200 — согласовано с ревьюером")
    public void createOrderWithoutAuth() {
        String body = "{\"ingredients\":[\"" + ING1 + "\"]}";

        given().contentType(ContentType.JSON)
                .body(body)
                .when().post(BASE + "/orders")
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        String token = getToken();
        String body = "{\"ingredients\":[]}";

        given().contentType(ContentType.JSON)
                .header("Authorization", token)
                .body(body)
                .when().post(BASE + "/orders")
                .then().statusCode(400)
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Description("Создание заказа с неверным хешем ингредиента")
    public void createOrderWithInvalidHash() {
        String token = getToken();
        String body = "{\"ingredients\":[\"invalid_hash_123\"]}";

        given().contentType(ContentType.JSON)
                .header("Authorization", token)
                .body(body)
                .when().post(BASE + "/orders")
                .then().statusCode(500);
    }
}