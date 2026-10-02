package client;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.User;

import static io.restassured.RestAssured.given;

public class UserClient {

    public static final String BASE = "https://stellarburgers.education-services.ru/api";

    public Response register(User user) {
        return given()
                .contentType(ContentType.JSON)
                .body(user)              // ← Gson сериализует автоматически
                .when()
                .post(BASE + "/auth/register");
    }

    public Response login(User user) {
        return given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(BASE + "/auth/login");
    }

    public Response delete(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .delete(BASE + "/auth/user");
    }
}
