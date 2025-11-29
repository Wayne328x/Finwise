package interface_adapters.presenters;

import entity.News;
import ui.NewsView;
import ui.NewsViewModel;
import use_case.fetch_news.FetchNewsOutputBoundary;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FetchNewsPresenter implements FetchNewsOutputBoundary {

    private final NewsView view;

    /** save all the news for turning pages */
    private List<News> allNews = new ArrayList<>();

    /** index for current page */
    private int currentPage = 0;

    /** formatting the time */
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public FetchNewsPresenter(NewsView view) {
        this.view = view;
    }

    // --------------------------
    //   methods must complement
    // --------------------------

    @Override
    public void presentNews(List<News> newsList) {
        this.allNews = newsList;
        this.currentPage = 0; // new data start from index 1
        updateViewModelAndRender();
    }

    @Override
    public void presentError(String errorMessage) {
        view.showError(errorMessage);
    }

    // --------------------------
    //     Presenter additional methods
    // --------------------------

    public void nextPage() {
        if ((currentPage + 1) * 3 < allNews.size()) {
            currentPage++;
            updateViewModelAndRender();
        }
    }

    public void prevPage() {
        if (currentPage > 0) {
            currentPage--;
            updateViewModelAndRender();
        }
    }

    // --------------------------
    //     set up view model and update view
    // --------------------------

    private void updateViewModelAndRender() {
        int start = currentPage * 3;
        int end = Math.min(start + 3, allNews.size());

        List<String> titles = new ArrayList<>();
        List<String> publishTimes = new ArrayList<>();
        List<String> urls = new ArrayList<>();

        // set up 3 news for each page
        for (int i = start; i < end; i++) {
            News n = allNews.get(i);
            titles.add(n.getTitle());
            publishTimes.add(n.getTimePublished().format(formatter));
            urls.add(n.getUrl());
        }

        // when the news left is less than 3, fill it up with blank
        while (titles.size() < 3) {
            titles.add("");
            publishTimes.add("");
            urls.add("");
        }

        boolean hasPrev = currentPage > 0;
        boolean hasNext = end < allNews.size();

        NewsViewModel vm = new NewsViewModel(
                titles, publishTimes, urls, hasPrev, hasNext
        );

        view.updateView(vm);
    }
}
