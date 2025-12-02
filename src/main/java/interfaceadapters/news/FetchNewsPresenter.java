package interfaceadapters.news;

import entity.News;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import ui.news.NewsView;
import ui.news.NewsViewModel;
import usecase.fetch_news.FetchNewsOutputBoundary;

public class FetchNewsPresenter implements FetchNewsOutputBoundary {

    private final NewsView view;

    /** Save all the news for turning pages. */
    private List<News> allNews = new ArrayList<>();

    private final int newsShownSize = 3;

    /** Index for current page. */
    private int currentPage;

    /** Formatting the time. */
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
        this.currentPage = 0;
        // new data start from index 1
        updateViewModelAndRender();
    }

    @Override
    public void presentError(String errorMessage) {
        view.showError(errorMessage);
    }

    /** Turn to the next page of news. */
    public void nextPage() {
        if ((currentPage + 1) * newsShownSize < allNews.size()) {
            currentPage++;
            updateViewModelAndRender();
        }
    }

    /** Turn to the previous page of news. */
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
        final int start = currentPage * newsShownSize;
        final int end = Math.min(start + newsShownSize, allNews.size());

        final List<String> titles = new ArrayList<>();
        final List<String> publishTimes = new ArrayList<>();
        final List<String> urls = new ArrayList<>();

        // set up 3 news for each page
        for (int i = start; i < end; i++) {
            final News n = allNews.get(i);
            titles.add(n.getTitle());
            publishTimes.add(n.getTimePublished().format(formatter));
            urls.add(n.getUrl());
        }

        // when the news left is less than 3, fill it up with blank
        while (titles.size() < newsShownSize) {
            titles.add("");
            publishTimes.add("");
            urls.add("");
        }

        final boolean hasPrev = currentPage > 0;
        final boolean hasNext = end < allNews.size();

        final NewsViewModel vm = new NewsViewModel(
                titles, publishTimes, urls, hasPrev, hasNext
        );

        view.updateView(vm);
    }
}
