package ar.edu.itba.paw.persistence.helpers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class QueryBuilder {
    private final StringBuilder str = new StringBuilder();
    private final List<Object> params = new LinkedList<>();
    private String operator = "";

    private Boolean not = false;

    public <T>QueryBuilder withList(String queryField, List<T> querylist) {
        if (querylist != null && querylist.size() > 0) {
            str.append(" ");
            str.append(operator);
            str.append(" ");
            String gamesAmount = String.join(",", Collections.nCopies(querylist.size(), "?"));
            params.addAll(querylist);
            str.append(String.format(" %s %s IN (", queryField,(not)? "NOT":""));
            str.append(gamesAmount);
            str.append(") ");
            operator = "AND";
        }
        if(not){
            this.not = false;
        }
        return this;
    }

    public QueryBuilder withSimilar(String queryField, String queryString) {
        if (queryString != null) {
            queryString = queryString.replace("%", "\\%").replace("_", "\\_");
            str.append(" ");
            str.append(operator);
            str.append(String.format(" %s %s ILIKE ? ", queryField,(not)? "NOT":""));

            params.add("%" + queryString + "%");
            operator = "AND";
        }
        if(not){
            this.not = false;
        }
        return this;
    }

    public <T>QueryBuilder withExact(String queryField, T queryContent) {
        if (queryContent != null) {
            str.append(" ");
            str.append(operator);
            str.append(String.format(" %s %s ? ", queryField,(not)? "<>":"="));
            params.add(queryContent);
            operator = "AND";
        }
        if(not){
            this.not = false;
        }
        return this;
    }

    public <T>QueryBuilder withGreaterOrEqual(String queryField, T queryContent) {
        if (queryContent != null) {
            str.append(" ");
            str.append(operator);
            str.append(String.format(" %s >= ? ", queryField));
            params.add(queryContent);
            operator = "AND";
        }
        return this;
    }

    public <T>QueryBuilder withLessOrEqual(String queryField, T queryContent) {
        if (queryContent != null) {
            str.append(" ");
            str.append(operator);
            str.append(String.format(" %s <= ? ", queryField));
            params.add(queryContent);
            operator = "AND";
        }
        return this;
    }

    public <T>QueryBuilder withGreater(String queryField, T queryContent) {
        if (queryContent != null) {
            str.append(" ");
            str.append(operator);
            str.append(String.format(" %s > ? ", queryField));
            params.add(queryContent);
            operator = "AND";
        }
        return this;
    }

    public <T>QueryBuilder withLess(String queryField, T queryContent) {
        if (queryContent != null) {
            str.append(" ");
            str.append(operator);
            str.append(String.format(" %s < ? ", queryField));
            params.add(queryContent);
            operator = "AND";
        }
        return this;
    }

    public QueryBuilder OR() {
        if (!this.operator.equals("")) {
            this.operator = "OR";
        }
        return this;
    }
    public QueryBuilder NOT(){
        this.not = !this.not;
        return this;
    }

    public QueryBuilder AND() {
        if (!this.operator.equals("")) {
            this.operator = "AND";
        }
        return this;
    }

    public String toQuery() {
        if (str.length() > 0) {
            return " WHERE " + str;
        }
        return str.toString();
    }

    public List<Object> toArguments() {
        return params;
    }
}