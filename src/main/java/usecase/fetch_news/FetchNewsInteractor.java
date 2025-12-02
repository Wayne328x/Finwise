package usecase.fetch_news;

import entity.News;
import java.util.List;

public class FetchNewsInteractor implements FetchNewsInputBoundary {

    private final NewsDataAccessInterface newsDao;
    private final FetchNewsOutputBoundary presenter;

    public FetchNewsInteractor(NewsDataAccessInterface newsDao, FetchNewsOutputBoundary presenter) {
        this.newsDao = newsDao;
        this.presenter = presenter;
    }

    @Override
    public void execute(FetchNewsInputData inputData) {
        try {
            List<News> newsList = newsDao.fetchNews(null);

            // package the output data
            FetchNewsOutputData outputData = new FetchNewsOutputData(newsList);

            presenter.presentNews(outputData.getNewsList());

        } catch (NewsDataAccessInterface.DataFetchException e) {
            // DAO reached the access limit
            FetchNewsOutputData outputData = new FetchNewsOutputData(e.getMessage());
            presenter.presentError(outputData.getErrorMessage());
        } catch (Exception e) {
            // other exceptions
            FetchNewsOutputData outputData = new FetchNewsOutputData("Failed to fetch news: " + e.getMessage());
            presenter.presentError(outputData.getErrorMessage());
        }
    }
}
