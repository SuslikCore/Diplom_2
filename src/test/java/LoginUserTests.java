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

public class LoginUserTests {

    private UserData userCreateData;
    private UserData userLoginData;
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

        //Получаем и сохраняем accessToken
        String accessToken = createResponse.extract().jsonPath().getString("accessToken");

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
    @DisplayName("Логин с валидными данными")
    @Description("Ожидаем 200 ок и используем 2е данных userCreateData и userLoginData")
    public void userCanLoginWithAllParametersTest(){
        userCreateData = new UserData(generator.generateEmail(5), generator.generatePassword(5), generator.generateUserName(6));

        userLoginData = new UserData(userCreateData.getEmail(), userCreateData.getPassword());

        // Создаем пользователя и сохраняем ответ в createResponse
        createResponse = userApi.createUser(userCreateData).log().all();
        response = userApi.loginUser(userLoginData);

        //Проверка
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(userLoginData.getEmail()))
                .body("user.name", equalTo(userCreateData.getName()));
    }

    @Test
    @DisplayName("Логин с неверным логином")
    @Description("Создаем пользователя и используем другую почту при логине -> Возвращает 401")
    public void invalidEmailUserLoginTest(){
        userCreateData = new UserData(generator.generateEmail(5), generator.generatePassword(5), generator.generateUserName(6));
        userLoginData = new UserData(generator.generateEmail(5), userCreateData.getPassword());

        // Создаем пользователя и сохраняем ответ в createResponse
        createResponse = userApi.createUser(userCreateData).log().all();
        response = userApi.loginUser(userLoginData);

        //Проверка
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));

    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Создаем пользователя и используем другой пароль при логине -> Возвращает 401")
    public void invalidPasswordUserLoginTest(){
        userCreateData = new UserData(generator.generateEmail(5), generator.generatePassword(5), generator.generateUserName(6));
        userLoginData = new UserData(userCreateData.getEmail(), generator.generatePassword(5));

        // Создаем пользователя и сохраняем ответ в createResponse
        createResponse = userApi.createUser(userCreateData).log().all();
        response = userApi.loginUser(userLoginData);

        //Проверка
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));

    }

}
