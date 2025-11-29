package use_case.signup;

/**
 * Input data for sign-up operations.
 * Contains the username and password provided by the user.
 */
public final class SignUpInputData {
  /**
   * The username for the new account.
   */
  private final String username;
  /**
   * The password for the new account.
   */
  private final String password;

  /**
   * Constructs a SignUpInputData object with the given credentials.
   *
   * @param usernameText the username
   * @param passwordText the password
   */
  public SignUpInputData(
      final String usernameText,
      final String passwordText) {
    this.username = usernameText;
    this.password = passwordText;
  }

  /**
   * Returns the username.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Returns the password.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }
}
