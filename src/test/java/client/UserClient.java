package client;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.User;

import static io.restassured.RestAssured.given;

public class UserClient {

    @Step("Зарегистрировать пользователя: {user.email}")
    public Response register(User user) {
        return given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(ApiConfig.BASE_URL + "/auth/register");
    }

    @Step("Авторизовать пользователя: {user.email}")
    public Response login(User user) {
        return given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(ApiConfig.BASE_URL + "/auth/login");
    }

    @Step("Удалить пользователя по токену")
    public Response delete(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .delete(ApiConfig.BASE_URL + "/auth/user");
    }
}