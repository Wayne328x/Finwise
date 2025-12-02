package entity;

import java.time.LocalDateTime;

public class News {

    private final String title;
    private final String url;
    private final LocalDateTime timePublished;

    public News(String title, String url, LocalDateTime timePublished) {
        if (title == null || url == null || timePublished == null) {
            throw new IllegalArgumentException("News fields cannot be null");
        }
        this.title = title;
        this.url = url;
        this.timePublished = timePublished;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public LocalDateTime getTimePublished() {
        return timePublished;
    }

    @Override
    public String toString() {
        return "News{"
                +
                "title='"
                +
                title + '\''
                +
                ", url='"
                +
                url
                +
                '\''
                +
                ", timePublished="
                +
                timePublished
                +
                '}';
    }
}
