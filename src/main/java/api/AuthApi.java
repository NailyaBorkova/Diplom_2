package api;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static config.Environment.HOST;
import static io.restassured.RestAssured.given;

public class AuthApi {
    private static final String ENDPOINT_CREATE = "/api/auth/register";
    private static final String ENDPOINT_LOGIN = "/api/auth/login";
    private static final String ENDPOINT_USER = "/api/auth/user";

    @Step("Create user")
    public ValidatableResponse createUserRequest(User user) {
        return given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(HOST + ENDPOINT_CREATE)
                .then();
    }

    @Step("Login user")
    public ValidatableResponse loginUserRequest(UserLogin userLogin) {
        return given()
                .contentType(ContentType.JSON)
                .body(userLogin)
                .when()
                .post(HOST + ENDPOINT_LOGIN)
                .then();
    }

    @Step("Update user")
    public ValidatableResponse updateUserRequest(User user, String token) {
        return given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .patch(HOST + ENDPOINT_USER)
                .then();
    }

    @Step("Delete user")
    public ValidatableResponse deleteUserRequest(String token) {
        return given()
                .auth().oauth2(token)
                .when()
                .delete(HOST + ENDPOINT_USER)
                .then();
    }
}