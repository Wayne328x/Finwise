package usecase.signup;

import data.user.UserRepository;
import entity.User;

/**
 * Interactor for user sign-up operations.
 * Handles the business logic for creating new user accounts.
 */
public final class SignUpInteractor {
  /**
   * The repository for user data operations.
   */
  private final UserRepository userRepository;

  /**
   * Constructs a SignUpInteractor with the given user repository.
   *
   * @param userRepo the user repository
   */
  public SignUpInteractor(final UserRepository userRepo) {
    this.userRepository = userRepo;
  }

  /**
   * Executes the sign-up operation based on the provided input.
   *
   * @param input the sign-up input containing username and password
   * @return output data containing the result status and message
   */
  public SignUpOutputData execute(final SignUpInputData input) {
    String username = input.getUsername();
    String password = input.getPassword();

    if (username == null || username.isBlank()) {
      return new SignUpOutputData(false, "Username is empty!");
    } else if (password == null || password.isBlank()) {
      return new SignUpOutputData(false, "Password is empty!");
    }

    try {
      User newUser = userRepository.create(username, password);
      return new SignUpOutputData(
          true,
          "Account created successfully: "
              + newUser.getUsername()
              + "!");
    } catch (IllegalStateException e) {
      return new SignUpOutputData(false, "Username already exists!");
    } catch (Exception e) {
      return new SignUpOutputData(false, "Something went wrong!");
    }
  }
}
