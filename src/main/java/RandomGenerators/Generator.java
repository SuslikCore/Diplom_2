package RandomGenerators;

import io.restassured.response.ValidatableResponse;

import java.util.List;
import java.util.Random;

public class Test {

    public int getRandomId(ValidatableResponse response){

        List<Object> dataArray = response.extract().jsonPath().getList("data");
        int length = dataArray.size();

        // Generate a random index
        Random random = new Random();
        int randomIndex = random.nextInt(length);
    }
}
