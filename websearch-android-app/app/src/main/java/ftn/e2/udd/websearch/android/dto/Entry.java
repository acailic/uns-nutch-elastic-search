package ftn.e2.udd.websearch.android.dto;

public class Entry {
    private String url;
    private String title;
    private String description;

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Entry withUrl(String url) {
        this.url = url;
        return this;
    }

    public Entry withTitle(String title) {
        this.title = title;
        return this;
    }

    public Entry withDescription(String description) {
        this.description = description;
        return this;
    }
}
