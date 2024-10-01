import api.*;
import generators.UserGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class UpdateUserTest {
    private AuthApi authApi;
    private User user;
    private UserLogin login;
    private String token;
    private String bearerToken;
    private String newEmail;
    private String newPassword;
    private String newName;
    private final static String ERROR_MESSAGE_NOT_AUTHORISED = "You should be authorised";

    @Before
    public void CreateVariables(){

        authApi = new AuthApi();
        user = UserGenerator.getSuccessCreateUser();
        login = new UserLogin();
        newEmail = UserGenerator.getNewEmail();
        newPassword = UserGenerator.getNewPassword();
        newName = UserGenerator.getNewName();

        ValidatableResponse responseCreate = authApi.createUserRequest(user);
        bearerToken = responseCreate.extract()
                                        .path("accessToken");

        token = bearerToken.substring(7);

        authApi.loginUserRequest(login.from(user));
    }

    @After
    public void deleteUser() {
        if(token != null){
            authApi.deleteUserRequest(token);
        }
    }

    @Test
    @Description("Изменение данных пользователя с авторизацией (Email)")
    public void updateUserEmailWithLoginTest(){

        user.setEmail(newEmail);
        ValidatableResponse responseUpdate = authApi.updateUserRequest(user, token);
        int actualStatusCode = responseUpdate.extract()
                                                .statusCode();

        Boolean isUserUpdated = responseUpdate.extract()
                                                .path("success");

        String actualResponce = (responseUpdate.extract().path("user")).toString();

        boolean isEmailUpdated = actualResponce.contains(newEmail);

        assertEquals("StatusCode is not 200", SC_OK, actualStatusCode);
        assertTrue("User is not login", isUserUpdated);
        assertTrue("Email is not new", isEmailUpdated);

    }

    @Test
    @Description("Изменение данных пользователя с авторизацией (Name)")
    public void updateUserNameWithLoginTest(){

        user.setName(newName);
        ValidatableResponse responseUpdate = authApi.updateUserRequest(user, token);

        int actualStatusCode = responseUpdate.extract()
                                                .statusCode();

        Boolean isUserUpdated = responseUpdate.extract()
                                                .path("success");

        String actualResponce = (responseUpdate.extract().path("user")).toString();
        boolean isNameUpdated = actualResponce.contains(newName);

        assertEquals("StatusCode is not 200", SC_OK, actualStatusCode);
        assertTrue("User is not login", isUserUpdated);
        assertTrue("Name is not new", isNameUpdated);

    }

    @Test
    @Description("Изменение данных пользователя с авторизацией (Password)")
    public void updateUserPasswordWithLoginTest(){

        user.setPassword(newPassword);
        ValidatableResponse responseUpdate = authApi.updateUserRequest(user, token);
        int actualStatusCode = responseUpdate.extract()
                                                .statusCode();
        Boolean isUserUpdated = responseUpdate.extract()
                                                .path("success");

        assertEquals("StatusCode is not 200", SC_OK, actualStatusCode);
        assertTrue("User is not login", isUserUpdated);

        ValidatableResponse responseSecondLogin = authApi.loginUserRequest(login.from(user));
        Boolean isUserSecondlogged = responseSecondLogin.extract().path("success");
        assertTrue("User is not login", isUserSecondlogged);

    }

    @Test
    @Description("Изменение данных пользователя без авторизации (Email)")
    public void updateUserEmailWithoutLoginTest(){

        user.setEmail(newEmail);
        ValidatableResponse responseUpdate = authApi.updateUserRequest(user, "");

        int actualStatusCode = responseUpdate.extract()
                                                .statusCode();

        String actualMessage = responseUpdate.extract()
                                                .path("message");

        assertEquals("StatusCode is not 403", SC_UNAUTHORIZED, actualStatusCode);
        assertEquals("Message is not correct", ERROR_MESSAGE_NOT_AUTHORISED, actualMessage);

    }

    @Test
    @Description("Изменение данных пользователя без авторизации (Name)")
    public void updateUserNameWithoutLoginTest(){

        user.setName(newName);
        ValidatableResponse responseUpdate = authApi.updateUserRequest(user, "");

        int actualStatusCode = responseUpdate.extract()
                                                .statusCode();

        String actualMessage = responseUpdate.extract()
                                                .path("message");

        assertEquals("StatusCode is not 403", SC_UNAUTHORIZED, actualStatusCode);
        assertEquals("Message is not correct", ERROR_MESSAGE_NOT_AUTHORISED, actualMessage);

    }

    @Test
    @Description("Изменение данных пользователя без авторизации (Password)")
    public void updateUserPasswordWithoutLoginTest(){

        user.setPassword(newPassword);
        ValidatableResponse responseUpdate = authApi.updateUserRequest(user, "");

        int actualStatusCode = responseUpdate.extract()
                                                .statusCode();

        String actualMessage = responseUpdate.extract()
                                                .path("message");

        assertEquals("StatusCode is not 403", SC_UNAUTHORIZED, actualStatusCode);
        assertEquals("Message is not correct", ERROR_MESSAGE_NOT_AUTHORISED, actualMessage);

    }
}