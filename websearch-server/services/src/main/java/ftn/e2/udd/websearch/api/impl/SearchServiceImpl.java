package ftn.e2.udd.websearch.api.impl;

import ftn.e2.udd.websearch.api.search.*;
import ftn.e2.udd.websearch.model.WebPageFieldName;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Component
public class SearchServiceImpl implements SearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);
    private static final String RANGE_QUERY_REGEX = "\\s?\\|\\s?";

    private Client client;

    @Override
    public ApiSearchResponse getResults(ApiSearchRequest request, Integer resultsPerPage, Integer pageNumber) {
        return search(request, pageNumber * resultsPerPage, resultsPerPage).withPageNumber(pageNumber);
    }

    private ApiSearchResponse search(ApiSearchRequest request, int start, int numberOfResultsPerPage) {
        LOGGER.info("Got search query: {}", request.getQuery());
        if (StringUtils.isBlank(request.getQuery()) || request.getQuery().length() < 3) {
            return new ApiSearchResponse();
        }
        final QueryBuilder qb =  getQueryBuilder(request.getQuery(), request.getQueryType(), WebPageFieldName.CONTENT.getFieldName());
        final SearchResponse searchResponse = getSearchResponse(qb, request.getQuery(), start, numberOfResultsPerPage);
        final SearchHits searchHits = searchResponse.getHits();
        final SearchHit[] hits = searchResponse.getHits().getHits();
        return convert(hits.length, hits);
    }


    private SearchResponse getSearchResponse(QueryBuilder qb,String searchText, int start,  int numberOfResultsPerPage) {
        return client.prepareSearch("nutch").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb).
                 addFields(WebPageFieldName.queryFieldNames)
                .addHighlightedField("content").setFrom(start * numberOfResultsPerPage).setSize(numberOfResultsPerPage).execute()
                .actionGet();
    }

    private static ApiSearchResponse convert(int length, SearchHit[] hits){
        List<Entry> entries = new ArrayList<>();
        for (SearchHit hit: hits){
            final Map<String, Object> source = hit.getSource();
            Entry entry = new Entry();
            entry.setLink((String) source.get("url"));
            entry.setTitle((String) source.get("title"));
            entry.setDescription(hit.getHighlightFields().get("content").getFragments().toString());
            entries.add(entry);
        }

        return new ApiSearchResponse().withTotalNumberOfResults(length).withEntries(entries);
    }


    private static QueryBuilder getQueryBuilder( final String queryString, final QueryType queryType,  final String fieldName ) {
        QueryBuilder queryBuilder = null;
        switch (queryType) {
            case DEFAULT:
                queryBuilder =  multiMatchQuery(queryString, WebPageFieldName.queryFieldNames).
                        field(WebPageFieldName.TITLE.getFieldName() , 2.0f) ;
                break;
            case TERM:
                queryBuilder = termQuery(fieldName, queryString);
                break;
            case FUZZY:
                queryBuilder = fuzzyQuery(fieldName, queryString);
                break;
            case PHRASE:
                queryBuilder = matchPhraseQuery(fieldName, queryString);
                break;
            case PREFIX:
                queryBuilder = prefixQuery(fieldName, queryString);
                break;
            case WILDCARD:
                queryBuilder = wildcardQuery(fieldName, queryString);
                break;
            default:
                break;
        }

        return queryBuilder;
    }




}
