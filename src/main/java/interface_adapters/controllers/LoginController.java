package interface_adapters.controllers;

import use_case.login.LoginInputData;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputData;

public class LoginController {

    private final LoginInteractor loginInteractor;

    public LoginController(LoginInteractor loginInteractor) {
        this.loginInteractor = loginInteractor;
    }

    public LoginOutputData login(String username, String password) {
        LoginInputData input = new LoginInputData(username, password);
        return loginInteractor.execute(input);
    }
}
