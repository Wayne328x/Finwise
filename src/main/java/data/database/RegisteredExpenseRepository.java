package data.database;

import data.expense.ExpenseRepository;
import entity.Expense;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 * Implementation of ExpenseRepository using JDBC and SQLite.
 * Provides database operations for expense management.
 */
public final class RegisteredExpenseRepository implements ExpenseRepository {

  /**
   * The data source for database connections.
   */
    private final DataSource dataSource;

  /**
   * Index for username parameter in prepared statements.
   */
  private static final int PARAM_INDEX_USERNAME = 1;

  /**
   * Index for datetime parameter in prepared statements.
   */
  private static final int PARAM_INDEX_DATETIME = 2;

  /**
   * Index for type parameter in prepared statements.
   */
  private static final int PARAM_INDEX_TYPE = 3;

  /**
   * Index for amount parameter in prepared statements.
   */
  private static final int PARAM_INDEX_AMOUNT = 4;

  /**
   * Constructs a RegisteredExpenseRepository with the given data source.
   *
   * @param dataSourceParam the data source for database connections
   */
  public RegisteredExpenseRepository(final DataSource dataSourceParam) {
    this.dataSource = dataSourceParam;
    }

  /**
   * Finds all expenses for a given username.
   *
   * @param username the username to search for
   * @return a list of expenses for the user
   */
    @Override
  public List<Expense> findByUsername(final String username) {
        String query = "SELECT * FROM expenses WHERE username = ?";
        List<Expense> expenses = new ArrayList<>();

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement =
            connection.prepareStatement(query)) {
      preparedStatement.setString(PARAM_INDEX_USERNAME, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    String u = resultSet.getString("username");
                    String dt = resultSet.getString("datetime");
                    String t = resultSet.getString("type");
                    double a = resultSet.getDouble("amount");
                    expenses.add(new Expense(id, u, dt, t, a));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByUsername expense failed!", e);
    }
    return expenses;
    }

  /**
   * Adds a new expense to the database.
   *
   * @param username the username
   * @param datetime the expense datetime
   * @param type the expense type
   * @param amount the expense amount
   */
    @Override
  public void add(
      final String username,
      final String datetime,
      final String type,
      final double amount) {
    String query =
        "INSERT INTO expenses (username, datetime, type, amount) "
            + "VALUES (?, ?, ?, ?)";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement =
            connection.prepareStatement(query)) {
      preparedStatement.setString(PARAM_INDEX_USERNAME, username);
      preparedStatement.setString(PARAM_INDEX_DATETIME, datetime);
      preparedStatement.setString(PARAM_INDEX_TYPE, type);
      preparedStatement.setDouble(PARAM_INDEX_AMOUNT, amount);
            preparedStatement.executeUpdate();
    } catch (SQLException e) {
            throw new RuntimeException("add expense failed!", e);
        }
    }

  /**
   * Gets the total expense amount for a user.
   *
   * @param username the username
   * @return the total expense amount, or 0.0 if no expenses found
   */
    @Override
  public double getTotalForUser(final String username) {
        String query = "SELECT SUM(amount) FROM expenses WHERE username = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement =
            connection.prepareStatement(query)) {
      preparedStatement.setString(PARAM_INDEX_USERNAME, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble(1);
        }
        return 0.0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("get totalForUser expense failed!", e);
        }
    }
}
