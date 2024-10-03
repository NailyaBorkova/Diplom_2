package api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static config.Environment.HOST;
import static io.restassured.RestAssured.given;

public class IngredientsApi {
    private static final String ENDPOINT_INGREDIENTS = "/api/ingredients";

    @Step("Get ingredients")
    public ValidatableResponse getIngredientsRequest() {
        return given()
                .when()
                .get(HOST + ENDPOINT_INGREDIENTS)
                .then();
    }
}