import RandomGenerators.Generator;
import api.OrderApi;
import api.UserApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.OrderData;
import model.UserData;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;

public class GetUserOrder {
    private UserApi userApi = new UserApi();
    private OrderApi orderApi = new OrderApi();;
    private Generator generator = new Generator();

    private UserData user;
    private ValidatableResponse ingredientListResponse;
    private ValidatableResponse createOrderResponse;
    private ValidatableResponse getSpecificOrderResponse;
    private String accessToken;

    @Before
    public void setUp() {

        user = new UserData(generator.generateEmail(5), generator.generatePassword(5), generator.generateUserName(6));
        accessToken = userApi.createUser(user).extract().jsonPath().getString("accessToken");
    }

    @After
    public void cleanUp(){

        if (accessToken == null || accessToken.isEmpty()){
            System.out.println("Message: Токен пустой. Удаление токена производиться не будет");
            return;
        }
        try {
            userApi.deleteUser(accessToken).log().all();
        } catch (Exception e) {
            System.out.println("Не удалось удалить пользователя");
            throw new RuntimeException(e);
        }
    }


    @Test
    @DisplayName("Получение заказов конкретного пользователя")
    @Description("Ожидаем 200 ок, создаем заказ, вызываем ручку, сравниваем с ингредиентом")
    public void getSpecificUserOrderTest(){

        //Сохраняем респонс от листа с ингредиентами
        ingredientListResponse = orderApi.getListOfIngredients();

        //Тянем 1 рандомный ингредиент id из листа с ответом
        String ingredientId = generator.getRandomId(ingredientListResponse);

        //Создаем заказ
        OrderData orderData = new OrderData(ingredientId);
        createOrderResponse = orderApi.createOrder(orderData).log().all();

        getSpecificOrderResponse = orderApi.getOrderFromSpecificUser(orderData).log().all();

        //Проверка
        getSpecificOrderResponse.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Ошибка при получения заказа от пользователья без авторизации")
    @Description("Ожидаем 401 Unauthorized, создаем заказ, вызываем ручку")
    public void getSpecificUserOrderNoAuthorizationTest(){

        //Сохраняем респонс от листа с ингредиентами
        ingredientListResponse = orderApi.getListOfIngredients();

        //Тянем 1 рандомный ингредиент id из листа с ответом
        String ingredientId = generator.getRandomId(ingredientListResponse);

        //Создаем заказ
        OrderData orderData = new OrderData(ingredientId);
        createOrderResponse = orderApi.createOrder(orderData).log().all();

        getSpecificOrderResponse = orderApi.getOrderFromSpecificUser(orderData).log().all();

        //Проверка
        getSpecificOrderResponse.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
