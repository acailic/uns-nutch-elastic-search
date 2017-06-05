package ftn.e2.udd.websearch.api;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {

    private int totalNumberOfResults;

    private int pageNumber;

    private List<Entry> entries = new ArrayList<>();

    public SearchResponse withTotalNumberOfResults(int totalNumberOfResults) {
        this.totalNumberOfResults = totalNumberOfResults;
        return this;
    }

    public SearchResponse withPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public SearchResponse withEntries(List<Entry> entries) {
        this.entries.addAll(entries);
        return this;
    }

    public int getTotalNumberOfResults() {
        return totalNumberOfResults;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public List<Entry> getEntries() {
        return entries;
    }
}
