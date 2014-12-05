package com.alimate.controller;

import com.alimate.dto.QueryForm;
import com.alimate.dto.QueryResult;
import com.alimate.dto.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

@Controller
@RequestMapping("/console")
public class ConsoleController {
    @Autowired private JdbcTemplate template;

    @RequestMapping
    public String showQueryConsole(Model model) {
        model.addAttribute("queryForm", new QueryForm());
        model.addAttribute("result", new QueryResult());
        model.addAttribute("tables", getAllTables());
        return "console/console";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processQuery(@ModelAttribute("queryForm") @Valid QueryForm form,
                               BindingResult result,
                               Model model) {
        boolean isSucceed = false;
        if (!result.hasErrors()) {
            try {
                final QueryResult queryResult = new QueryResult();
                if (isSelect(form.getQuery())) {
                    queryResult.setSelect(true);
                    extractSelectResult(form, queryResult);
                } else {
                    queryResult.setSelect(false);
                    template.execute(form.getQuery());
                    queryResult.setMessage("Query executed");
                }
                model.addAttribute("result", queryResult);
                isSucceed = true;
            } catch (DataAccessException e) {
                result.rejectValue("query", "errors.query.sql", new Object[]{e.getRootCause().getMessage()},
                        "SQL Error");
            }
        }

        if (isSucceed) model.addAttribute("queryForm", new QueryForm());
        else model.addAttribute("result", new QueryResult());
        model.addAttribute("tables", getAllTables());

        return "console/console";
    }

    private boolean isSelect(String query) {
        return Pattern.compile("^select", Pattern.CASE_INSENSITIVE).matcher(query.trim()).find();
    }

    private void extractSelectResult(QueryForm form, final QueryResult queryResult) {
        template.query(form.getQuery(), new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                ResultSetMetaData metaData = rs.getMetaData();
                if (queryResult.getColumns().isEmpty()) {
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        queryResult.getColumns().add(metaData.getColumnName(i));
                    }
                }
                List<Object> row = new ArrayList<>();
                queryResult.getColumns().forEach(name -> {
                    try {
                        row.add(rs.getObject(name));
                    } catch (SQLException e) {
                        row.add("null");
                    }
                });
                queryResult.getResults().add(row);
            }
        });
    }

    private Map<String, List<Table>> getAllTables() {
        final String all_tables = "SELECT schemaname, tablename FROM pg_tables";
        final String table_details = "SELECT a.attname AS col, pg_catalog.format_type(a.atttypid, a.atttypmod) AS type " +
                "FROM pg_catalog.pg_attribute a WHERE a.attnum > 0 AND NOT a.attisdropped " +
                "AND a.attrelid = ( SELECT c.oid FROM pg_catalog.pg_class c " +
                "LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = ? " +
                "AND pg_catalog.pg_table_is_visible(c.oid));";

        Map<String, List<Table>> mapping = new HashMap<>();
        template.query(all_tables, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                String schema = rs.getString("schemaname");
                String tableName = rs.getString("tablename");

                mapping.compute(schema, (key, list) -> {
                    if (isNull(list))
                        list = new ArrayList<>();

                    Table table = new Table();
                    table.setNamespace(schema);
                    table.setName(tableName);
                    list.add(table);

                    return list;
                });
            }
        });

        mapping.values().forEach(list -> {
            list.forEach(t -> {
                template.query(table_details, new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException {
                        t.getCols().add(rs.getString("col"));
                        t.getTypes().add(rs.getString("type"));
                    }
                }, t.getName());
            });
        });

        return mapping;
    }
}