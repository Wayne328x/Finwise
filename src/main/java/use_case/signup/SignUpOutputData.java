package use_case.signup;

/**
 * Output data for sign-up operations.
 * Contains the result status and message for user registration.
 */
public final class SignUpOutputData {
  /**
   * Indicates whether the sign-up operation was successful.
   */
  private final boolean success;
  /**
   * Message describing the sign-up result or error.
   */
  private final String message;

  /**
   * Constructs a SignUpOutputData object.
   *
   * @param successValue whether the sign-up was successful
   * @param messageText status message
   */
  public SignUpOutputData(
      final boolean successValue,
      final String messageText) {
    this.success = successValue;
    this.message = messageText;
  }

  /**
   * Returns whether the sign-up operation was successful.
   *
   * @return true if successful, false otherwise
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * Returns the status message.
   *
   * @return the message
   */
  public String getMessage() {
    return message;
  }
}
