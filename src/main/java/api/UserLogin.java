package api;

public class UserLogin {

    public String email;
    public String password;

    public UserLogin(String email, String password){
        this.email = email;
        this.password = password;
    }

    public UserLogin() {
    }

    public static UserLogin from (User user) {
        return new UserLogin(user.getEmail(), user.getPassword());
    }


}