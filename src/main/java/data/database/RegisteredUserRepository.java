package data.database;

import data.user.UserRepository;
import entity.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import javax.sql.DataSource;

/**
 * Implementation of UserRepository using JDBC and SQLite.
 * Provides database operations for user management.
 */
public final class RegisteredUserRepository implements UserRepository {

  /**
   * The data source for database connections.
   */
    private final DataSource dataSource;

  /**
   * Constructs a RegisteredUserRepository with the given data source.
   *
   * @param dataSourceParam the data source for database connections
   */
  public RegisteredUserRepository(final DataSource dataSourceParam) {
    this.dataSource = dataSourceParam;
    }

  /**
   * Finds a user by username.
   *
   * @param username the username to search for
   * @return an Optional containing the user if found, empty otherwise
   */
    @Override
  public Optional<User> findByUsername(final String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement =
            connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new User(
                            resultSet.getLong("id"),
                            resultSet.getString("username"),
              resultSet.getString("password")));
        }
        return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

  /**
   * Creates a new user with the given credentials.
   *
   * @param username the username
   * @param password the password
   * @return the created user
   * @throws IllegalStateException if the username already exists
   * @throws RuntimeException if database operation fails
   */
    @Override
  public User create(final String username, final String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement =
            connection.prepareStatement(
                query,
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();

            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) {
                    return new User(keys.getLong(1), username, password);
        }
        throw new RuntimeException("No generated key found!");
            }
        } catch (SQLException e) {
      if (e.getMessage() != null
          && e.getMessage().toLowerCase().contains("unique")) {
                throw new IllegalStateException("Username already exists!");
            }
            throw new RuntimeException(e);
        }
    }
}
