import controllers.*;
import data.*;
import data.usecase5.InMemoryPortfolioRepository;
import data.usecase5.InMemoryPriceHistoryRepository;
import ui.LoginView;
import ui.SignUpView;
import ui.DashboardView;
import use_case.login.LoginInteractor;
import use_case.portfolio.PortfolioInteractor;
import use_case.signup.SignUpInteractor;
import use_case.stocksearch.StockSearchInteractor;

import javax.sql.DataSource;
import javax.swing.*;

public class Main {

    private static DataSource dataSource;
    private static RegisteredUserRepository userRepository;
    private static RegisteredExpenseRepository expenseRepository;

    private static SignUpController signUpController;
    private static LoginController loginController;
    private static DashboardController dashboardController;

    private static JFrame currentFrame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Setup database
            dataSource = DataSourceFactory.sqlite("sqllite.db");
            TableInitializer.ensureSchema(dataSource);
            userRepository = new RegisteredUserRepository(dataSource);
            expenseRepository = new RegisteredExpenseRepository(dataSource);

            // Create interactors
            SignUpInteractor signUpInteractor = new SignUpInteractor(userRepository);
            LoginInteractor loginInteractor = new LoginInteractor(userRepository);

            // Stocks API call
            AlphaVantageAPI api = new AlphaVantageAPI();
            StockSearchInteractor stockSearchInteractor = new StockSearchInteractor(api);

            portfolioRepo = new InMemoryPortfolioRepository();
            priceHistoryRepo = new InMemoryPriceHistoryRepository();
            //portfolioInteractor = new PortfolioInteractor(portfolioRepo, priceHistoryRepo, )

            // Create controllers
            signUpController = new SignUpController(signUpInteractor);
            loginController = new LoginController(loginInteractor);
            dashboardController = new DashboardController();

            // Start application on the login screen
            showLoginView();
        });
    }

    /** Displays the login window */
    private static void showLoginView() {
        if (currentFrame != null) currentFrame.dispose();

        LoginView loginView = new LoginView(
                loginController,
                Main::showSignUpView,         // Runnable
                Main::showDashboardView       // Consumer<String>
        );

        currentFrame = loginView;
        loginView.setVisible(true);
    }

    /** Displays the sign-up window */
    private static void showSignUpView() {
        if (currentFrame != null) currentFrame.dispose();

        SignUpView signUpView = new SignUpView(
                signUpController,
                Main::showLoginView // callback to switch back
        );

        currentFrame = signUpView;
        signUpView.setVisible(true);
    }

    private static void showDashboardView(String username) {
        if (currentFrame != null) currentFrame.dispose();

        DashboardView dashboardView = new DashboardView(
                dashboardController,
                stockSearchController,
                Main::showLoginView,   // callback to login screen
                username,              // show welcome message
                expenseRepository
        );

        currentFrame = dashboardView;
        dashboardView.setVisible(true);
    }
}
