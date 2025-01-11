import RandomGenerators.Generator;
import api.UserApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.UserData;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class ChangeDataUserTests {
    private UserData userCreateData;
    private UserData userChangedData;
    private String accessToken;
    private UserApi userApi;
    private ValidatableResponse createResponse;
    private ValidatableResponse response;
    private Generator generator = new Generator();

    @Before
    public void setUp() {
        userApi = new UserApi();
    }

    @After
    public void cleanUp(){
        if (accessToken == null || accessToken.isEmpty()){
            System.out.println("Сообщение: Токен пустой. Удаление токена производиться не будет");
            return;
        }
        try {
            userApi.deleteUser(accessToken).log().all();
        } catch (Exception e) {
            System.out.println("Сообщение: Не удалось удалить пользователя");
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Изменение email пользователя c авторизацией")
    @Description("Изменяем поле email и получаем ответ 200 ОК")
    public void changeUserEmailDataTest(){

        userCreateData = new UserData(generator.generateEmail(5), generator.generatePassword(5), generator.generateUserName(6));
        userChangedData = new UserData(generator.generateEmail(5),"",generator.generateUserName(6));

        // Создаем пользователя и сохраняем ответ в createResponse
        createResponse = userApi.createUser(userCreateData).log().all();
        accessToken = createResponse.extract().jsonPath().getString("accessToken");
        response = userApi.changeUserData(userChangedData, accessToken);

        //Проверка
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("user.email", equalTo(userChangedData.getEmail()));
    }

    @Test
    @DisplayName("Изменение поле name пользователя c авторизацией")
    @Description("Изменяем поле name и получаем ответ 200 ОК")
    public void changeUserNameDataTest(){

        userCreateData = new UserData(generator.generateEmail(5), generator.generatePassword(5), generator.generateUserName(6));
        userChangedData = new UserData(generator.generateEmail(5), "", generator.generateUserName(6));

        // Создаем пользователя и сохраняем ответ в createResponse
        createResponse = userApi.createUser(userCreateData).log().all();
        accessToken = createResponse.extract().jsonPath().getString("accessToken");
        response = userApi.changeUserData(userChangedData, accessToken);

        //Проверка
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("user.name", equalTo(userChangedData.getName()));
    }

    @Test
    @DisplayName("Ошибка при изменении поля email пользователя без авторизации")
    @Description("При изменении поля email без авторизации получаем ответ 401 UNAUTHORIZED")
    public void cantChangeEmailDataWithNoAuthorizationTest(){

        //Тестовые данные
        userCreateData = new UserData(generator.generateEmail(5), generator.generatePassword(5), generator.generateUserName(6));
        userChangedData = new UserData(generator.generateEmail(5), "", generator.generateUserName(6));

        // Создаем пользователя и сохраняем ответ в createResponse
        createResponse = userApi.createUser(userCreateData).log().all();
        accessToken = createResponse.extract().jsonPath().getString("accessToken");
        response = userApi.changeUserData(userChangedData);

        //Проверка
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Ошибка при изменении поля name пользователя без авторизации")
    @Description("При изменении поля name без авторизации получаем ответ 401 UNAUTHORIZED")
    public void cantChangeNameDataWithNoAuthorizationTest(){

        //Тестовые данные
        userCreateData = new UserData(generator.generateEmail(5), generator.generatePassword(5), generator.generateUserName(6));
        userChangedData = new UserData(generator.generateEmail(5), "", generator.generateUserName(6));

        // Создаем пользователя и сохраняем ответ в createResponse
        createResponse = userApi.createUser(userCreateData).log().all();
        accessToken = createResponse.extract().jsonPath().getString("accessToken");
        response = userApi.changeUserData(userChangedData);

        //Проверка
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
