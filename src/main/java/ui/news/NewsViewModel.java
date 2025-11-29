package ui.news;

import java.util.List;

public class NewsViewModel {

    public List<String> titles;

    public List<String> publishTimes;

    public List<String> urls;

    public boolean hasPrevPage;

    public boolean hasNextPage;

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
}
