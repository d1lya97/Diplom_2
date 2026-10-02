import client.OrderClient;
import client.UserClient;
import io.qameta.allure.Description;
import model.Order;
import model.User;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrderApiTest {

    private static final String ING1 = "61c0c5a71d1f82001bdaaa6d";
    private static final String ING2 = "61c0c5a71d1f82001bdaaa72";

    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();

        User user = new User(
                "order" + System.currentTimeMillis() + "@yandex.ru",
                "pass123",
                "Tester"
        );
        accessToken = userClient.register(user)
                .then()
                .extract().path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @Description("Создание заказа с авторизацией возвращает 200")
    public void createOrderWithAuthReturnsOk() {
        Order order = new Order(Arrays.asList(ING1, ING2));
        orderClient.create(order, accessToken)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    @Description("Создание заказа с авторизацией возвращает success=true")
    public void createOrderWithAuthReturnsSuccess() {
        Order order = new Order(Arrays.asList(ING1, ING2));
        orderClient.create(order, accessToken)
                .then()
                .body("success", equalTo(true));
    }

    @Test
    @Description("Создание заказа с авторизацией возвращает номер заказа")
    public void createOrderWithAuthReturnsNumber() {
        Order order = new Order(Arrays.asList(ING1, ING2));
        orderClient.create(order, accessToken)
                .then()
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Создание заказа без ингредиентов возвращает 400")
    public void createOrderWithoutIngredientsReturnsBadRequest() {
        Order empty = new Order(Collections.emptyList());
        orderClient.create(empty, accessToken)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @Description("Создание заказа без ингредиентов возвращает сообщение об ошибке")
    public void createOrderWithoutIngredientsReturnsMessage() {
        Order empty = new Order(Collections.emptyList());
        orderClient.create(empty, accessToken)
                .then()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Description("Создание заказа с неверным хешем возвращает 500")
    public void createOrderWithInvalidHashReturnsServerError() {
        Order invalid = new Order(Collections.singletonList("invalid_hash_123"));
        orderClient.create(invalid, accessToken)
                .then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @Description("Создание заказа без авторизации возвращает 200 (поведение стенда)")
    public void createOrderWithoutAuthReturnsOk() {
        Order order = new Order(Collections.singletonList(ING1));
        orderClient.createWithoutAuth(order)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }
}