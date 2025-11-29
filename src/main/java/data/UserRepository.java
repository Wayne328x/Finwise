package data;

import entity.User;
import java.util.Optional;

/**
 * Repository interface for user data operations.
 * Provides methods to find and create users.
 */
public interface UserRepository {

  /**
   * Finds a user by username.
   *
   * @param username the username to search for
   * @return an Optional containing the user if found, empty otherwise
   */
  Optional<User> findByUsername(String username);

  /**
   * Creates a new user with the given credentials.
   *
   * @param username the username
   * @param password the password
   * @return the created user
   */
  User create(String username, String password);
}
