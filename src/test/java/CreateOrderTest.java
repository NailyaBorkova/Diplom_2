import api.*;
import data.*;
import generators.UserGenerator;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class CreateOrderTest {
    private AuthApi authApi;
    private User user;
    private UserLogin login;
    private IngredientsApi getIngredients;
    private OrdersApi ordersApi;
    private String token;
    private String bearerToken;

    @Before
    public void CreateVariables(){
        authApi = new AuthApi();
        user = UserGenerator.getSuccessCreateUser();
        login = new UserLogin();

        getIngredients = new IngredientsApi();
        ordersApi = new OrdersApi();

        ValidatableResponse responseCreate = authApi.createUserRequest(user);
        bearerToken = responseCreate.extract().path("accessToken");
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
    //Создание заказа с ингредиентами с авторизацией
    public void createOrderWithLoginTest() {
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

        ValidatableResponse responseCreateOrder = ordersApi.createOrderRequest(listIngredient,token);
        int actualStatusCode = responseCreateOrder.extract()
                                                    .statusCode();

        Boolean isOrderCreated = responseCreateOrder.extract()
                                                    .path("success");

        assertEquals("StatusCode is not 200", SC_OK, actualStatusCode);
        assertTrue("Order is not created", isOrderCreated);

    }

    @Test
    //Создание заказа с ингредиентами без авторизации
    public void createOrderWithoutLoginTest() {

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

        ValidatableResponse responseCreateOrder = ordersApi.createOrderRequest(listIngredient,"");

        int actualStatusCode = responseCreateOrder.extract()
                                                    .statusCode();

        Boolean isOrderCreated = responseCreateOrder.extract()
                                                    .path("success");

        assertEquals("StatusCode is not 200", SC_OK, actualStatusCode);
        assertTrue("Order is not created", isOrderCreated);

    }

    @Test
    //Создание заказа без ингредиентов
    public void createOrderWithoutIngredientsTest() {
        ArrayList<String> listString = new ArrayList<>();
        ListIngredient listIngredient = new ListIngredient(listString);

        ValidatableResponse responseCreateOrder = ordersApi.createOrderRequest(listIngredient,token);
        int actualStatusCode = responseCreateOrder.extract()
                                                    .statusCode();

        Boolean isOrderCreated = responseCreateOrder.extract()
                                                    .path("success");

        assertEquals("StatusCode is not 400", SC_BAD_REQUEST, actualStatusCode);
        assertFalse("Order is created", isOrderCreated);

    }

    @Test
    //Создание заказа с неверным хешем ингредиентов
    public void createOrderWithIncorrectHashTest() {
        ValidatableResponse responseIngredients = getIngredients.getIngredientsRequest();

        Ingredient ingredient = responseIngredients.extract()
                                                    .body()
                                                    .as(Ingredient.class);

        ArrayList<Ingredient> list = ingredient.getData();

        ArrayList<String> listIngredients = new ArrayList<>();

        int max = list.size();
        Random random = new Random();

        for (int i = 0; i < max; i++){
            listIngredients.add(list.get(i).get_id()+random.nextInt(50));
            }

        ListIngredient listIngredient = new ListIngredient(listIngredients);

        ValidatableResponse responseCreateOrder = ordersApi.createOrderRequest(listIngredient,token);
        int actualStatusCode = responseCreateOrder.extract()
                                                    .statusCode();

        assertEquals("StatusCode is not 500", SC_INTERNAL_SERVER_ERROR, actualStatusCode);

    }
}