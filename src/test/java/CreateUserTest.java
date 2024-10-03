import api.*;
import generators.UserGenerator;
import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class CreateUserTest {
    private AuthApi authApi;
    private User user;
    private UserLogin login;
    private String token;
    private String bearerToken;
    private final static String ERROR_MESSAGE_NOT_UNIQUE = "User already exists";
    private final static String ERROR_MESSAGE_REQUIRED_FIELD = "Email, password and name are required fields";

    @Before
    public void createVariables(){

        authApi = new AuthApi();
        user = UserGenerator.getSuccessCreateUser();
        login = new UserLogin();

    }

    @After
    public void deleteUser() {

        if(token != null){
            authApi.deleteUserRequest(token);
        }

    }

    @Test
    @Description("создать уникального пользователя")
    public void createUniqueUserTest(){

        ValidatableResponse responseCreate = authApi.createUserRequest(user);

        int actualStatusCode = responseCreate.extract()
                                            .statusCode();

        Boolean isUserCreated = responseCreate.extract()
                                                .path("success");

        bearerToken = responseCreate.extract()
                                    .path("accessToken");

        token = bearerToken.substring(7);

        assertEquals("StatusCode is not 200", SC_OK, actualStatusCode);
        assertTrue("User is not created", isUserCreated);

        ValidatableResponse responseLogin = authApi.loginUserRequest(login.from(user));

        Boolean isUserlogged = responseLogin.extract().path("success");
        assertTrue("User is not login", isUserlogged);

    }

    @Test
    @Description("создать пользователя, который уже зарегистрирован")
    public void createNotUniqueUserTest(){

        ValidatableResponse responseCreate = authApi.createUserRequest(user);

        Boolean isUserCreated = responseCreate.extract()
                                                .path("success");

        bearerToken = responseCreate.extract()
                                    .path("accessToken");

        token = bearerToken.substring(7);

        assertTrue("User is not created", isUserCreated);

        ValidatableResponse responseLogin = authApi.loginUserRequest(login.from(user));

        Boolean isUserlogged = responseLogin.extract()
                                            .path("success");

        assertTrue("User is not login", isUserlogged);

        responseCreate = authApi.createUserRequest(user);
        int actualStatusCode = responseCreate.extract().statusCode();
        String actualMessage = responseCreate.extract().path("message");

        assertEquals("StatusCode is not 403", SC_FORBIDDEN, actualStatusCode);
        assertEquals("Message is not correct", ERROR_MESSAGE_NOT_UNIQUE, actualMessage);

    }

    @Test
    @Description("создать пользователя и не заполнить одно из обязательных полей (email)")
    public void createUserWithoutEmailTest(){

        user.setEmail(null);
        ValidatableResponse responseCreate = authApi.createUserRequest(user);
        int actualStatusCode = responseCreate.extract()
                                                .statusCode();
        String actualMessage = responseCreate.extract()
                                                .path("message");

        assertEquals("StatusCode is not 403", SC_FORBIDDEN, actualStatusCode);
        assertEquals("Message is not correct", ERROR_MESSAGE_REQUIRED_FIELD, actualMessage);

    }

    @Test
    @Description("создать пользователя и не заполнить одно из обязательных полей (password)")
    public void createUserWithoutPasswordTest(){

        user.setPassword(null);
        ValidatableResponse responseCreate = authApi.createUserRequest(user);

        int actualStatusCode = responseCreate.extract()
                                                .statusCode();

        String actualMessage = responseCreate.extract()
                                                .path("message");

        assertEquals("StatusCode is not 403", SC_FORBIDDEN, actualStatusCode);
        assertEquals("Message is not correct", ERROR_MESSAGE_REQUIRED_FIELD, actualMessage);

    }

    @Test
    @Description("создать пользователя и не заполнить одно из обязательных полей (name)")
    public void createUserWithoutNameTest(){

        user.setName(null);
        ValidatableResponse responseCreate = authApi.createUserRequest(user);

        int actualStatusCode = responseCreate.extract()
                                                .statusCode();

        String actualMessage = responseCreate.extract()
                                                .path("message");

        assertEquals("StatusCode is not 403", SC_FORBIDDEN, actualStatusCode);
        assertEquals("Message is not correct", ERROR_MESSAGE_REQUIRED_FIELD, actualMessage);

    }
}