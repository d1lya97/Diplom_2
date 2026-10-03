package client;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {

    @Step("Создать заказ с авторизацией")
    public Response create(Order order, String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ApiConfig.BASE_URL + "/orders");
    }

    @Step("Создать заказ без авторизации")
    public Response createWithoutAuth(Order order) {
        return given()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post(ApiConfig.BASE_URL + "/orders");
    }
}