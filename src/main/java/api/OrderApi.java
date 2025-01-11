package api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.OrderData;

import static io.restassured.RestAssured.given;

public class OrderApi extends RestApi{

    public static final String CREATE_ORDER_URI = "api/orders";
    public static final String GET_ORDER_FROM_SPECIFIC_USER_URI = "api/orders";
    public static final String GET_LIST_OF_INGREDIENTS = "api/ingredients";

    @Step("Создаем заказ c авторизацией")
    public ValidatableResponse createOrder(OrderData orderData, String accessToken){
        return given()
                .spec(requestSpecification())
                .header("Authorization",accessToken)
                .and()
                .body(orderData)
                .when()
                .post(CREATE_ORDER_URI)
                .then();
    }

    @Step("Создаем заказ без авторизации")
    public ValidatableResponse createOrder(OrderData orderData){
        return given()
                .spec(requestSpecification())
                .and()
                .body(orderData)
                .when()
                .post(CREATE_ORDER_URI)
                .then();
    }

    @Step("Получить данные об ингредиентах")
    public ValidatableResponse getListOfIngredients(){
        return given()
                .spec(requestSpecification())
                .and()
                .when()
                .get(GET_LIST_OF_INGREDIENTS)
                .then();
    }

    @Step("Получаем заказ от конкретного пользователя")
    public ValidatableResponse getOrderFromSpecificUser(OrderData orderData ,String accessToken){
        return given()
                .spec(requestSpecification())
                .header("Authorization",accessToken)
                .and()
                .body(orderData)
                .when()
                .get(GET_ORDER_FROM_SPECIFIC_USER_URI)
                .then();
    }

    @Step("Получаем заказ от конкретного пользователя без авторизации")
    public ValidatableResponse getOrderFromSpecificUser(OrderData orderData){
        return given()
                .spec(requestSpecification())
                .and()
                .body(orderData)
                .when()
                .get(GET_ORDER_FROM_SPECIFIC_USER_URI)
                .then();
    }

}
