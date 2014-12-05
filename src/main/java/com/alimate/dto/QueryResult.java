package com.alimate.dto;

import java.util.ArrayList;
import java.util.List;

public class QueryResult {
    private boolean select;
    private String message;
    private List<String> columns = new ArrayList<>();
    private List<List<Object>> results = new ArrayList<>();

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<List<Object>> getResults() {
        return results;
    }

    public void setResults(List<List<Object>> results) {
        this.results = results;
    }
}