package ftn.e2.udd.websearch.api.search;

public class Entry {
    private String title;
    private String link;
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Entry withTitle(String title) {
        this.title = title;
        return this;
    }

    public Entry withLink(String link) {
        this.link = link;
        return this;
    }

    public Entry withDescription(String description) {
        this.description = description;
        return this;
    }
}
