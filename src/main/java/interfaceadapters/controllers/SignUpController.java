package interfaceadapters.controllers;

import usecase.signup.SignUpInputData;
import usecase.signup.SignUpInteractor;
import usecase.signup.SignUpOutputData;

public class SignUpController {

    private final SignUpInteractor signUpInteractor;

    public SignUpController(SignUpInteractor signUpInteractor) {
        this.signUpInteractor = signUpInteractor;
    }

    public SignUpOutputData signUp(String username, String password) {
        SignUpInputData input = new SignUpInputData(username, password);
        return signUpInteractor.execute(input);
    }
}
