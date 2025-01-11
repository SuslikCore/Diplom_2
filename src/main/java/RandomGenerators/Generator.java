package RandomGenerators;

import io.restassured.response.ValidatableResponse;
import org.apache.commons.text.RandomStringGenerator;

import java.util.List;
import java.util.Random;

public class Generator {

    private List<Object> dataArray;
    private int ingredientIndex;

    public String getRandomId(ValidatableResponse response){

        dataArray = response.extract().jsonPath().getList("data");
        int length = dataArray.size();

        // Generate a random index
        Random random = new Random();
        ingredientIndex = random.nextInt(length);
        String ingredientId = response.extract().jsonPath().getString("data[" + ingredientIndex + "]._id");

        return ingredientId;
    }

    public String generateEmail(int length) {

        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(Character::isLetterOrDigit)
                .build();

        String generatedEmail = generator.generate(length) + "@mail.ru";

        return generatedEmail.toLowerCase();
    }

    public String generatePassword(int length) {

        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(Character::isLetterOrDigit)
                .build();

        String generatedPassword = generator.generate(length);

        return generatedPassword.toLowerCase();
    }

    public String generateUserName(int length) {

        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(Character::isLetterOrDigit)
                .build();

        String generatedUserName = generator.generate(length) + "User";

        return generatedUserName.toLowerCase();
    }



}
