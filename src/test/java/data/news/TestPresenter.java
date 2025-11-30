package data.news;

import entity.News;
import usecase.fetch_news.FetchNewsOutputBoundary;

import java.util.List;

/**
 * 测试用 Presenter
 * 将 OutputData 打印到控制台
 */
public class TestPresenter implements FetchNewsOutputBoundary {

    @Override
    public void presentNews(List<News> newsList) {
        System.out.println("=== Fetch Success ===");
        for (News news : newsList) {
            System.out.println(news.getTimePublished() + " | " + news.getTitle() + " | " + news.getUrl());
        }
    }

    @Override
    public void presentError(String errorMessage) {
        System.out.println("=== Fetch Error ===");
        System.out.println(errorMessage);
    }
}
