package usecase.login;

import data.UserRepository;
import entity.User;
import java.util.Optional;

public class LoginInteractor {

    private final UserRepository userRepository;

    public LoginInteractor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginOutputData execute(LoginInputData input) {
        String username = input.getUsername();
        String password = input.getPassword();

        if (username == null || username.isBlank()) {
            return new LoginOutputData(false, "Username is empty!");
        } else if (password == null || password.isBlank()) {
            return new LoginOutputData(false, "Password is empty!");
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return new LoginOutputData(false, "Account doesn't exist!");
        }

        User user = userOpt.get();
        if (!password.equals(user.getPassword())) {
            return new LoginOutputData(false, "Incorrect password!");
        } return  new LoginOutputData(true, user.getUsername() + " logged in successfully!");
    }
}
