package com.alimate.dto;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class QueryForm {
    private String query;

    @NotNull @NotEmpty(message = "{field.required}")
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}