package api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.UserData;

import static io.restassured.RestAssured.given;

public class UserApi extends RestApi {

    public static final String CREATE_USER_URI = "api/auth/register";
    public static final String LOGIN_USER_URI = "api/auth/login";
    public static final String CHANGE_USER_DATA_URI = "api/auth/user";
    public static final String DELETE_USER_URI = "api/auth/user";

    @Step("Создаем пользователя")
    public ValidatableResponse createUser(UserData user) {
        return given()
                .spec(requestSpecification())
                .and()
                .body(user)
                .when()
                .post(CREATE_USER_URI)
                .then();
    }

    @Step("Логинимся пользователем")
    public ValidatableResponse loginUser(UserData user) {
        return given()
                .spec(requestSpecification())
                .and()
                .body(user)
                .when()
                .post(LOGIN_USER_URI)
                .then();
    }

    @Step("Изменяем данные пользователя")
    public ValidatableResponse changeUserData(UserData user, String accessToken) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", accessToken)
                .and()
                .body(user)
                .when()
                .patch(CHANGE_USER_DATA_URI)
                .then();
    }

    @Step("Изменяем данные без токена")
    public ValidatableResponse changeUserData(UserData user) {
        return given()
                .spec(requestSpecification())
                .and()
                .body(user)
                .when()
                .patch(CHANGE_USER_DATA_URI)
                .then();
    }

    @Step("Удаляем пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", accessToken)
                .and()
                .when()
                .delete(DELETE_USER_URI)
                .then();
    }
}
