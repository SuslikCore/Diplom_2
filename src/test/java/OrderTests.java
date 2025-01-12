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
import random.generators.Generator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;


public class OrderTests {

    private final UserApi userApi = new UserApi();
    private final OrderApi orderApi = new OrderApi();
    private final Generator generator = new Generator();
    private UserData user;
    private ValidatableResponse ingredientListResponse;
    private ValidatableResponse createOrderResponse;
    private String accessToken;

    @Before
    public void setUp() {

        user = new UserData(generator.generateEmail(5), generator.generatePassword(5), generator.generateUserName(6));
        accessToken = userApi.createUser(user).extract().jsonPath().getString("accessToken");
    }

    @After
    public void cleanUp() {

        if (accessToken == null || accessToken.isEmpty()) {
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
    @DisplayName("Создание заказа с авторизацей и ингредиентом")
    @Description("Генерируем длину листа ингредиентов, получаем id и ждем 200 ok + true")
    public void createOrderWithAuthorizationAndIngredientTest() {

        //Сохраняем респонс от листа с ингредиентами
        ingredientListResponse = orderApi.getListOfIngredients().log().all();

        //Тянем 2 рандомных ингредиента id из листа с ответом
        String ingredientId = generator.getRandomId(ingredientListResponse);

        //Создаем заказ
        OrderData orderData = new OrderData(ingredientId);
        createOrderResponse = orderApi.createOrder(orderData, accessToken);

        //Проверка
        createOrderResponse.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("order.ingredients._id", hasItem(ingredientId))
                .body("order.owner.name", equalTo(user.getName()))
                .body("order.owner.email", equalTo(user.getEmail()));
    }

    @Test
    @DisplayName("Создание заказа с авторизацей и без ингредиента")
    @Description("400 bad request и сообщение о необходимости передачи ids' ингредиентов")
    public void createOrderWithAuthorizationAndNoIngredientTest() {

        //Сохраняем респонс от листа с ингредиентами
        ingredientListResponse = orderApi.getListOfIngredients().log().all();

        //Тянем 2 рандомных ингредиента id из листа с ответом
        String ingredientId = null;

        //Создаем заказ
        OrderData orderData = new OrderData(ingredientId);
        createOrderResponse = orderApi.createOrder(orderData, accessToken);

        //Проверка
        createOrderResponse.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с авторизацей и невалидным id ингредиента")
    @Description("Ожидается 500  Internal Server Error")
    public void createOrderWithAuthorizationAndInvalidIngredientIdTest() {

        //Сохраняем респонс от листа с ингредиентами
        ingredientListResponse = orderApi.getListOfIngredients().log().all();

        //Тянем 2 рандомных ингредиента id из листа с ответом
        String ingredientId = generator.getRandomId(ingredientListResponse) + "п4рив2ет";

        //Создаем заказ
        OrderData orderData = new OrderData(ingredientId);
        createOrderResponse = orderApi.createOrder(orderData, accessToken);

        //Проверка
        createOrderResponse.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }


    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Генерируем длину листа ингредиентов, получаем id и ждем 200 ok + true")
    public void createOrderWithoutAuthorization() {

        //Сохраняем респонс от листа с ингредиентами
        ingredientListResponse = orderApi.getListOfIngredients().log().all();

        //Тянем 2 рандомных ингредиента id из листа с ответом
        String ingredientId = generator.getRandomId(ingredientListResponse);

        //Создаем заказ
        OrderData orderData = new OrderData(ingredientId);
        createOrderResponse = orderApi.createOrder(orderData);

        //Проверка
        createOrderResponse.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true));
    }
}
