package model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserData {
    private String email;
    private String password;
    private String name;

    public UserData(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
