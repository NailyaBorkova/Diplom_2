import api.*;
import data.Ingredient;
import data.ListIngredient;
import generators.UserGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class GetOrdersUserTest {
    private AuthApi authApi;
    private User user;
    private UserLogin login;
    private IngredientsApi getIngredients;
    private OrdersApi ordersApi;
    private String token;
    private String bearerToken;
    private final static String ERROR_MESSAGE_NOT_AUTHORISED = "You should be authorised";

    @Before
    public void CreateVariables(){

        authApi = new AuthApi();
        user = UserGenerator.getSuccessCreateUser();
        login = new UserLogin();

        getIngredients = new IngredientsApi();
        ordersApi = new OrdersApi();

        ValidatableResponse responseCreate = authApi.createUserRequest(user);

        bearerToken = responseCreate.extract()
                                    .path("accessToken");

        token = bearerToken.substring(7);

        authApi.loginUserRequest(login.from(user));

        ValidatableResponse responseIngredients = getIngredients.getIngredientsRequest();

        Ingredient ingredient = responseIngredients.extract()
                                                    .body()
                                                    .as(Ingredient.class);

        ArrayList<Ingredient> list = ingredient.getData();

        ArrayList<String> listIngredients = new ArrayList<>();

        int max = list.size();
        for (int i = 0; i < max; i++){
            listIngredients.add(list.get(i).get_id());
            }

        ListIngredient listIngredient = new ListIngredient(listIngredients);

        ordersApi.createOrderRequest(listIngredient,token);

    }

    @After
    public void deleteUser() {
        if(token != null){
            authApi.deleteUserRequest(token);
        }
    }

    @Test
    //Получение заказов конкретного пользователя с авторизацией
    public void getOrdersUserWithLoginTest() {

        ValidatableResponse responseGetOrdersByUser = ordersApi.getOrdersByUserRequest(token);

        int actualStatusCode = responseGetOrdersByUser.extract()
                                                        .statusCode();

        Boolean isOrdersGet  = responseGetOrdersByUser.extract()
                                                        .path("success");

        assertEquals("StatusCode is not 200", SC_OK, actualStatusCode);
        assertTrue("Order is not created", isOrdersGet);

    }

    @Test
    //Получение заказов конкретного пользователя без авторизации
    public void getOrdersUserWithoutLoginTest() {

        ValidatableResponse responseGetOrdersByUser = ordersApi.getOrdersByUserRequest("");

        int actualStatusCode = responseGetOrdersByUser.extract()
                                                        .statusCode();

        String actualMessage = responseGetOrdersByUser.extract()
                                                        .path("message");

        assertEquals("StatusCode is not 401", SC_UNAUTHORIZED, actualStatusCode);
        assertEquals("Message is not correct", ERROR_MESSAGE_NOT_AUTHORISED, actualMessage);

    }
}