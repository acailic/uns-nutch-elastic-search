package ftn.e2.udd.websearch.model;

/**
 *   Aleksandar
 */
public enum WebPageFieldName {
    TITLE("title"),
    CONTENT("content") ;

    public static final String[] queryFieldNames = {
            TITLE.getFieldName(),
            CONTENT.getFieldName(),
    };

    public static final String[] simpleSearchFieldNames = {
            TITLE.getFieldName(),
            CONTENT.getFieldName(),
    };

    public static final String[] highlightFieldNames = {
            TITLE.getFieldName(),
            CONTENT.getFieldName(),
    };

    private String fieldName;

    private WebPageFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

}
