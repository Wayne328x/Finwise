package usecase.fetch_news;

import java.util.List;

import entity.News;

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
            final List<News> newsList = newsDao.fetchNews(null);

            // package the output data
            final FetchNewsOutputData outputData = new FetchNewsOutputData(newsList);

            presenter.presentNews(outputData.getNewsList());

        }
        catch (NewsDataAccessInterface.DataFetchException exception) {
            // DAO reached the access limit
            final FetchNewsOutputData outputData = new FetchNewsOutputData(exception.getMessage());
            presenter.presentError(outputData.getErrorMessage());
        }
    }
}
