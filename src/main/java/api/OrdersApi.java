package api;

import data.ListIngredient;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static config.Environment.HOST;
import static io.restassured.RestAssured.given;

public class OrdersApi {
    private static final String ENDPOINT_ORDERS = "/api/orders";

    @Step("Create order")
    public ValidatableResponse createOrderRequest(ListIngredient listIngredient, String token) {
        return given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(listIngredient)
                .when()
                .post(HOST + ENDPOINT_ORDERS)
                .then();
    }

    @Step("Get orders by user")
    public ValidatableResponse getOrdersByUserRequest(String token) {
        return given()
                .auth().oauth2(token)
                .when()
                .get(HOST + ENDPOINT_ORDERS)
                .then();
    }
}