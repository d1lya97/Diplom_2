import client.UserClient;
import io.qameta.allure.Description;
import model.User;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class LoginApiTest {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = new User(
                "login" + System.currentTimeMillis() + "@yandex.ru",
                "pass123",
                "Tester"
        );
        // Создаём пользователя через @Before
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
    @Description("Логин под существующим пользователем")
    public void loginExistingUser() {
        userClient.login(user)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @Description("Логин с неверным email")
    public void loginWithWrongEmail() {
        User wrongEmail = new User("wrong@yandex.ru", "pass123", "Tester");
        userClient.login(wrongEmail)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @Description("Логин с неверным паролем")
    public void loginWithWrongPassword() {
        User wrongPass = new User(user.getEmail(), "wrongpass", "Tester");
        userClient.login(wrongPass)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", equalTo("email or password are incorrect"));
    }
}