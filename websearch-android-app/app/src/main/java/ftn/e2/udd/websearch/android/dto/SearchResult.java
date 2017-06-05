package ftn.e2.udd.websearch.android.dto;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {
    private boolean errorOccurred;

    private int totalNumberOfResults;

    private int pageNumber;

    private List<Entry> entries = new ArrayList<>();

    public SearchResult withTotalNumberOfResults(int totalNumberOfResults) {
        this.totalNumberOfResults = totalNumberOfResults;
        return this;
    }

    public SearchResult withPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public SearchResult withEntries(List<Entry> entries) {
        this.entries.addAll(entries);
        return this;
    }

    public SearchResult withErrorOccurred(boolean errorOccurred) {
        this.errorOccurred = errorOccurred;
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

    public boolean isErrorOccurred() {
        return errorOccurred;
    }
}
