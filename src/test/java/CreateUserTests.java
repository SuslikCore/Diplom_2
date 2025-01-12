import api.UserApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.UserData;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import random.generators.Generator;

import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTests {

    private UserData user;
    private UserApi userApi;
    private ValidatableResponse createResponse;
    private ValidatableResponse response;
    private final Generator generator = new Generator();

    @Before
    public void setUp() {
        userApi = new UserApi();
    }

    @After
    public void cleanUp() {

        //Получаем и сохраняем accessToken
        String accessToken = createResponse.extract().jsonPath().getString("accessToken");

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
    @DisplayName("Создание валидного пользователя")
    @Description("Заполняем все поля")
    public void userCanBeCreatedTest() {

        user = new UserData(generator.generateEmail(5), generator.generatePassword(5), generator.generateUserName(6));

        // Создаем пользователя и сохраняем ответ в createResponse
        createResponse = userApi.createUser(user);

        //Проверка
        createResponse.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));

    }

    @Test
    @DisplayName("Создание пользователя, который уже был создан")
    @Description("Вызываем создание пользователя два раза с одинаковыми данными -> Возвращает ошибку 403 forbidden")
    public void CantCreateExistedUserTest() {

        user = new UserData(generator.generateEmail(5), generator.generatePassword(5), generator.generateUserName(6));

        // Создаем пользователя и сохраняем ответ в createResponse
        createResponse = userApi.createUser(user);
        response = userApi.createUser(user);

        //Проверка
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без почты")
    @Description("Вернет ошибку 403 Forbidden")
    public void userCantBeCreatedWithoutEmailTest() {

        user = new UserData("", generator.generatePassword(5), generator.generateUserName(6));

        // Создаем пользователя и сохраняем ответ в createResponse
        createResponse = userApi.createUser(user);

        //Проверка
        createResponse.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));

    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Вернет ошибку 403 Forbidden")
    public void userCantBeCreatedWithoutPasswordTest() {
        user = new UserData(generator.generateEmail(5), "", generator.generateUserName(6));

        // Создаем пользователя и сохраняем ответ в createResponse
        createResponse = userApi.createUser(user);

        //Проверка
        createResponse.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));

    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Вернет ошибку 403 Forbidden")
    public void userCantBeCreatedWithoutNameTest() {
        user = new UserData(generator.generateEmail(5), generator.generatePassword(5), "");

        // Создаем пользователя и сохраняем ответ в createResponse
        createResponse = userApi.createUser(user);

        //Проверка
        createResponse.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
