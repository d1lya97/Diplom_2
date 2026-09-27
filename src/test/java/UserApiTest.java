import io.qameta.allure.Description;
import io.restassured.http.ContentType;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserApiTest {

    private static final String BASE = "https://stellarburgers.education-services.ru/api";

    @Test
    @Description("Создание уникального пользователя")
    public void createUniqueUser() {
        String email = "test" + System.currentTimeMillis() + "@yandex.ru";
        String body = "{\"email\":\"" + email + "\",\"password\":\"pass123\",\"name\":\"Tester\"}";

        given().contentType(ContentType.JSON).body(body)
                .when().post(BASE + "/auth/register")
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @Description("Создание уже зарегистрированного пользователя")
    public void createExistingUser() {
        String email = "dup" + System.currentTimeMillis() + "@yandex.ru";
        String body = "{\"email\":\"" + email + "\",\"password\":\"pass123\",\"name\":\"Tester\"}";

        given().contentType(ContentType.JSON).body(body)
                .when().post(BASE + "/auth/register");

        given().contentType(ContentType.JSON).body(body)
                .when().post(BASE + "/auth/register")
                .then().statusCode(403)
                .body("message", equalTo("User already exists"));
    }

    @Test
    @Description("Создание пользователя без обязательного поля")
    public void createUserWithMissingField() {
        String body = "{\"password\":\"pass123\",\"name\":\"Tester\"}";

        given().contentType(ContentType.JSON).body(body)
                .when().post(BASE + "/auth/register")
                .then().statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @Description("Логин под существующим пользователем")
    public void loginExistingUser() {
        String email = "login" + System.currentTimeMillis() + "@yandex.ru";
        String regBody = "{\"email\":\"" + email + "\",\"password\":\"pass123\",\"name\":\"Tester\"}";
        given().contentType(ContentType.JSON).body(regBody).post(BASE + "/auth/register");

        String loginBody = "{\"email\":\"" + email + "\",\"password\":\"pass123\"}";
        given().contentType(ContentType.JSON).body(loginBody)
                .when().post(BASE + "/auth/login")
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @Description("Логин с неверными данными")
    public void loginWithWrongCredentials() {
        String body = "{\"email\":\"wrong@yandex.ru\",\"password\":\"wrong\"}";

        given().contentType(ContentType.JSON).body(body)
                .when().post(BASE + "/auth/login")
                .then().statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }
}
