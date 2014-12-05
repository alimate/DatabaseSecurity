package com.alimate.dto;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private String namespace;
    private String name;
    private List<String> cols = new ArrayList<>();
    private List<String> types = new ArrayList<>();

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCols() {
        return cols;
    }

    public void setCols(List<String> cols) {
        this.cols = cols;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}