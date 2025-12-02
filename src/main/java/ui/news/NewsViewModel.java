package ui.news;

import java.util.List;

public final class NewsViewModel {

    private List<String> titles;

    private List<String> publishTimes;

    private List<String> urls;

    private boolean hasPrevPage;

    private boolean hasNextPage;

    public NewsViewModel(
            List<String> titles,
            List<String> publishTimes,
            List<String> urls,
            boolean hasPrevPage,
            boolean hasNextPage
    ) {
        this.titles = titles;
        this.publishTimes = publishTimes;
        this.urls = urls;
        this.hasPrevPage = hasPrevPage;
        this.hasNextPage = hasNextPage;
    }

    public List<String> getTitles() {
        return titles;
    }

    public List<String> getPublishTimes() {
        return publishTimes;
    }

    public List<String> getUrls() {
        return urls;
    }

    public boolean isHasPrevPage() {
        return hasPrevPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }
}
