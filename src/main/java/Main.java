import data.news.*;
import data.usecase5.*;
import data.*;

import interface_adapters.controllers.*;
import interface_adapters.presenters.*;

import ui.*;


import use_case.login.*;
import use_case.portfolio.*;
import use_case.signup.*;
import use_case.stocksearch.*;
import use_case.fetch_news.*;
import use_case.case5.*;

import javax.sql.DataSource;
import javax.swing.*;

public class Main {

    private static DataSource dataSource;
    private static RegisteredUserRepository userRepository;
    private static RegisteredExpenseRepository expenseRepository;
    private static InMemoryPortfolioRepository portfolioRepo;
    private static InMemoryPriceHistoryRepository priceHistoryRepo;

    private static SignUpController signUpController;
    private static LoginController loginController;
    private static DashboardController dashboardController;
    private static StockSearchController stockSearchController;
    private static PortfolioController portfolioController;

    private static JFrame currentFrame;
    private static String currentUsername;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Setup database
            dataSource = DataSourceFactory.sqlite("app.db");
            TableInitializer.ensureSchema(dataSource);
            userRepository = new RegisteredUserRepository(dataSource);
            expenseRepository = new RegisteredExpenseRepository(dataSource);

            // Create interactors
            SignUpInteractor signUpInteractor = new SignUpInteractor(userRepository);
            LoginInteractor loginInteractor = new LoginInteractor(userRepository);

            // Stocks API call
            AlphaVantageAPI api = new AlphaVantageAPI();
            StockSearchInteractor stockSearchInteractor = new StockSearchInteractor(api);

            WatchlistRepository watchlistRepository =
                    new JdbcWatchlistRepository(dataSource);

            stockSearchController =
                    new StockSearchController(stockSearchInteractor, watchlistRepository);

            portfolioRepo = new InMemoryPortfolioRepository();
            priceHistoryRepo = new InMemoryPriceHistoryRepository();
            //portfolioInteractor = new PortfolioInteractor(portfolioRepo, priceHistoryRepo, )

            // Create controllers
            signUpController = new SignUpController(signUpInteractor);
            loginController = new LoginController(loginInteractor);
            dashboardController = new DashboardController();
           // portfolioController = new PortfolioController()

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
        
        currentUsername = username; // Store the username for use in other views

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

    private static void showNewsView(){
        // 1.Get DAO
        NewsApiDAO newsApiDAO = new NewsApiDAO();
        try {
            newsApiDAO.fetchNews("");
        } catch (NewsApiDAO.RateLimitExceededException e) {
            System.out.println("Rate Limit Exceeded");
        }

        // 2. Presenter
        NewsView view = new NewsView(null); 
        FetchNewsPresenter presenter = new FetchNewsPresenter(view);

        // 3. Interactor
        FetchNewsInteractor interactor = new FetchNewsInteractor(newsApiDAO, presenter);

        // 4. Controller
        NewsController controller = new NewsController(interactor, presenter);

        // 5. View
        view.setController(controller);

        // 6. Initialize the news
        controller.fetchNews();
    }

    private static void showPortfolioView() {
        // Use Case 5: Portfolio performance diagnostics
        if (currentFrame != null) currentFrame.dispose();

        // 1. create ViewModel
        PortfolioViewModel viewModel = new PortfolioViewModel();

        // 2. create Presenter（implement PortfolioOutputBoundary）
        Presenter presenter = new Presenter(viewModel);

        // 3. create Interactor（implement PortfolioInputBoundary）
        PortfolioInputBoundary interactor = new PortfolioInteractor(
                new InMemoryPortfolioRepository(),
                new InMemoryPriceHistoryRepository(),
                presenter
        );

        // 4. create Controller（dependent on InputBoundary + ViewModel）
        PortfolioController controller = new PortfolioController(interactor, viewModel);

        // 5. create View（dependent on Controller + username）
        PortfolioView view = new PortfolioView(
                controller,
                currentUsername
        );

        currentFrame = view;
        view.setVisible(true);
    }

    private static void showInvestmentView() {
        // ToDo
    }

    private static void showStockPricesView() {
        if (currentFrame != null) currentFrame.dispose();

        AlphaVantageAPI api = new AlphaVantageAPI();
        StockSearchInteractor interactor = new StockSearchInteractor(api);

        // Use a watchlist repository so the stock search controller
        // has access to persisted watched stocks.
        WatchlistRepository watchlistRepository =
                new JdbcWatchlistRepository(dataSource);

        StockSearchController controller =
                new StockSearchController(interactor, watchlistRepository);

        StockSearchView view = new StockSearchView(
                controller,
                currentUsername
        );

        currentFrame = view;
        view.setVisible(true);
    }

    private static void showTrendsView() {
        // ToDo
    }

    private static void showExpensesView() {
        // ToDo
    }
}

