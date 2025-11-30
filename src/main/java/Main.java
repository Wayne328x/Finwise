import data.news.*;
import data.usecase5.*;
import data.*;
import data.usecase4.JsonTradingDataAccess;

import interface_adapters.controllers.*;
import interface_adapters.presenters.*;


import ui.dashboard.DashboardView;
import ui.login.LoginView;
import ui.signup.SignUpView;
import ui.news.NewsView;
import ui.portfolio.PortfolioView;
import ui.portfolio.PortfolioViewModel;
import ui.stock_search.StockSearchView;
import ui.tracker.TrackerView;
import ui.trends.TrendsView;
import ui.trends.TrendsViewModel;
import usecase.stocksearch.StockSearchInputBoundary;
import usecase.stocksearch.StockSearchInteractor;
import usecase.add_expense.AddExpenseInteractor;
import usecase.list_expenses.ListExpensesInteractor;
import usecase.login.*;
import usecase.portfolio.*;
import usecase.signup.*;
import usecase.fetch_news.*;
import usecase.trading.*;


import usecase.trends.TrendsDataAccess;
import usecase.trends.TrendsInteractor;

import javax.sql.DataSource;
import javax.swing.*;
import java.nio.file.Paths;

public class Main {

    private static DataSource dataSource;
    private static RegisteredUserRepository userRepository;
    private static RegisteredExpenseRepository expenseRepository;
    private static TradingDataAccessInterface tradingData;
    private static PortfolioRepository portfolioRepo;
    private static PriceHistoryRepository priceHistoryRepo;

    private static SignUpController signUpController;
    private static LoginController loginController;
    private static DashboardController dashboardController;
    private static TrackerController trackerController;
    private static StockSearchController stockSearchController;
    private static TradingController tradingController;
    private static TradingViewModel tradingViewModel;
    private static PortfolioController portfolioController;
    private static TrendsController trendsController;
    private static TrendsPresenter trendsPresenter;
    private static TrendsViewModel trendsViewModel;
    private static TrendsDataAccess trendsDataAccess;

    private static JFrame currentFrame;
    private static String currentUsername;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Setup database
            dataSource = DataSourceFactory.sqlite("app.db");
            TableInitializer.ensureSchema(dataSource);
            userRepository = new RegisteredUserRepository(dataSource);
            expenseRepository = new RegisteredExpenseRepository(dataSource);

            // Trends (slightly messy)
            trendsViewModel = new TrendsViewModel();
            trendsPresenter = new TrendsPresenter(trendsViewModel);
            trendsDataAccess = new TrendsAdapter(expenseRepository);
            TrendsInteractor trendsInteractor = new TrendsInteractor(trendsDataAccess, trendsPresenter);
            trendsController = new TrendsController(trendsInteractor);

            // Create interactors
            SignUpInteractor signUpInteractor = new SignUpInteractor(userRepository);
            LoginInteractor loginInteractor = new LoginInteractor(userRepository);
            ListExpensesInteractor listExpensesInteractor = new ListExpensesInteractor(expenseRepository);
            AddExpenseInteractor addExpenseInteractor     = new AddExpenseInteractor(expenseRepository);

            // Stocks API call
            AlphaVantageAPI api = new AlphaVantageAPI();
            StockSearchPresenter stockSearchPresenter = new StockSearchPresenter();
            StockSearchInputBoundary stockSearchInteractor =
                    new StockSearchInteractor(api, stockSearchPresenter);

            WatchlistRepository watchlistRepository =
                    new JdbcWatchlistRepository(dataSource);

            stockSearchController =
                    new StockSearchController(stockSearchInteractor, watchlistRepository);

            //Trading setup
            tradingData = new JsonTradingDataAccess(Paths.get("orders.json"));
            

            // Portfolio repo relies on tradingData
            portfolioRepo = new TradingDataPortfolioRepository(tradingData);
            priceHistoryRepo = new AlphaVantagePriceHistoryRepository();

            PortfolioPresenter portfolioPresenter = new PortfolioPresenter(new PortfolioViewModel());

            PortfolioInteractor portfolioInteractor = new PortfolioInteractor(
                    portfolioRepo,
                    priceHistoryRepo,
                    portfolioPresenter
            );

            // Create controllers
            signUpController = new SignUpController(signUpInteractor);
            loginController = new LoginController(loginInteractor);
            dashboardController = new DashboardController();
            trackerController = new TrackerController(listExpensesInteractor, addExpenseInteractor);
            portfolioController = new PortfolioController(portfolioInteractor, portfolioPresenter.getViewModel());


            tradingViewModel = new TradingViewModel();
            TradingPresenter tradingPresenter = new TradingPresenter(tradingViewModel);
            TradingInteractor tradingInteractor = new TradingInteractor(tradingData, tradingPresenter);
            tradingController = new TradingController(tradingInteractor, tradingViewModel);

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
        if (tradingData.getCash(username) == 0.0) {
            tradingData.updateCash(username, 10000.0);
        }
        tradingViewModel.setCashAfterTrade(tradingData.getCash(username));
        
        currentUsername = username; // Store the username for use in other views

        DashboardView dashboardView = new DashboardView(
                dashboardController,
                stockSearchController,
                tradingController,
                trendsController,
                trendsViewModel,
                portfolioController,
                Main::showLoginView,     // callback to login screen
                username,                // show welcome message
                expenseRepository,
                Main::showTrackerView
        );

        currentFrame = dashboardView;
        dashboardView.setVisible(true);
    }

    private static void showNewsView(){
        NewsApiDAO newsApiDAO = new NewsApiDAO();   // Get DAO
        try {
            newsApiDAO.fetchNews("");
        } catch (NewsApiDAO.RateLimitExceededException e) {
            System.out.println("Rate Limit Exceeded");
        }

        // Presenter
        NewsView view = new NewsView(null);
        FetchNewsPresenter presenter = new FetchNewsPresenter(view);

        // Interactor
        FetchNewsInteractor interactor = new FetchNewsInteractor(newsApiDAO, presenter);

        // Controller
        NewsController controller = new NewsController(interactor, presenter);

        // View
        view.setController(controller);

        // Initialize the news
        controller.fetchNews();
    }

    private static void showTrackerView(String username) {
        TrackerView trackerView = new TrackerView(username, trackerController);
        trackerView.setVisible(true);
    }

    private static void showPortfolioView() {
        // Use Case 5: Portfolio performance diagnostics
        if (currentFrame != null) currentFrame.dispose();

        // create ViewModel
        PortfolioViewModel viewModel = new PortfolioViewModel();

        // create Presenter（implement PortfolioOutputBoundary）
        PortfolioPresenter presenter = new PortfolioPresenter(viewModel);

        TradingDataAccessInterface tradingDataAccess = tradingData;

        // create Interactor（implement PortfolioInputBoundary）
        PortfolioInputBoundary interactor = new PortfolioInteractor(
                new TradingDataPortfolioRepository(tradingDataAccess),
                new AlphaVantagePriceHistoryRepository(),
                presenter
        );

        // create Controller（dependent on InputBoundary + ViewModel）
        PortfolioController controller = new PortfolioController(interactor, viewModel);

        // create View（dependent on Controller + username）
        PortfolioView view = new PortfolioView(controller, currentUsername);

        currentFrame = view;
        view.setVisible(true);
    }

    private static void showInvestmentView() {
        // ToDo
    }

    private static void showStockPricesView() {
        if (currentFrame != null) currentFrame.dispose();

        AlphaVantageAPI api = new AlphaVantageAPI();
        StockSearchPresenter stockSearchPresenter = new StockSearchPresenter();
        StockSearchInputBoundary interactor =
                new StockSearchInteractor(api, stockSearchPresenter);

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
        if (currentFrame != null) currentFrame.dispose();

        // Use the already-initialized fields
        TrendsView trendsView = new TrendsView(trendsController, trendsViewModel, currentUsername);
        currentFrame = trendsView;
        trendsView.setVisible(true);
    }

    private static void showExpensesView() {
        // ToDo
    }
}
