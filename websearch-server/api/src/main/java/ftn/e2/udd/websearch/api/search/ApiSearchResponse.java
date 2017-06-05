package ftn.e2.udd.websearch.api.search;

import java.util.ArrayList;
import java.util.List;

public class ApiSearchResponse {

    private int totalNumberOfResults;

    private int pageNumber;

    private List<Entry> entries = new ArrayList<>();

    public ApiSearchResponse withTotalNumberOfResults(int totalNumberOfResults) {
        this.totalNumberOfResults = totalNumberOfResults;
        return this;
    }

    public ApiSearchResponse withPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public ApiSearchResponse withEntries(List<Entry> entries) {
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
