import client.UserClient;
import io.qameta.allure.Description;
import model.User;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class RegisterApiTest {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = new User(
                "reg" + System.currentTimeMillis() + "@yandex.ru",
                "pass123",
                "Tester"
        );
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @Description("Создание уникального пользователя")
    public void createUniqueUser() {
        accessToken = userClient.register(user)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .extract().path("accessToken");
    }

    @Test
    @Description("Создание уже зарегистрированного пользователя")
    public void createExistingUser() {
        userClient.register(user);
        userClient.register(user)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", equalTo("User already exists"));
    }

    @Test
    @Description("Создание пользователя без email")
    public void createUserWithoutEmail() {
        User noEmail = new User(null, "pass123", "Tester");
        userClient.register(noEmail)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @Description("Создание пользователя без пароля")
    public void createUserWithoutPassword() {
        User noPass = new User("x" + System.currentTimeMillis() + "@yandex.ru", null, "Tester");
        userClient.register(noPass)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @Description("Создание пользователя без имени")
    public void createUserWithoutName() {
        User noName = new User("y" + System.currentTimeMillis() + "@yandex.ru", "pass123", null);
        userClient.register(noName)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }
}