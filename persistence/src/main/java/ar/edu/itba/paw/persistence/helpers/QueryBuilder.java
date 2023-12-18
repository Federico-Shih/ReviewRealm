package ar.edu.itba.paw.persistence.helpers;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QueryBuilder {
    private final StringBuilder str = new StringBuilder();

    private final List<Object> params = new LinkedList<>();

    private QueryOperator operator = QueryOperator.EMPTY;

    private Boolean not = false;

    public <T>QueryBuilder withList(String queryField, List<T> querylist) {
        if (querylist != null && !querylist.isEmpty()) {
            str.append(" ");
            str.append(operator.getOperator());
            str.append(" ");
            String gamesAmount = IntStream.range(0, querylist.size()).mapToObj((i) -> "?" + (i + 1 + params.size())).collect(Collectors.joining(","));
            params.addAll(querylist);
            str.append(String.format(" %s %s IN (", queryField, (not) ? QueryOperator.NOT.getOperator() : QueryOperator.EMPTY.getOperator()));
            str.append(gamesAmount);
            str.append(") ");
            operator = QueryOperator.AND;
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
            str.append(operator.getOperator());
            str.append(String.format(" lower( %s ) %s LIKE lower( ?%d ) ", queryField, (not) ? QueryOperator.NOT.getOperator() : QueryOperator.EMPTY.getOperator(), params.size() + 1));

            params.add("%" + queryString + "%");
            operator = QueryOperator.AND;
        }
        if(not){
            this.not = false;
        }
        return this;
    }

    public <T>QueryBuilder withExact(String queryField, T queryContent) {
        if (queryContent != null) {
            str.append(" ");
            str.append(operator.getOperator());
            str.append(String.format(" %s (%s %s ?%d) ", (not)? "NOT" : "" ,queryField, "=", params.size() + 1));
            params.add(queryContent);
            operator = QueryOperator.AND;
        }
        if(not){
            this.not = false;
        }
        return this;
    }

    public <T>QueryBuilder withGreaterOrEqual(String queryField, T queryContent) {
        if (queryContent != null) {
            str.append(" ");
            str.append(operator.getOperator());
            str.append(String.format(" %s >= ?%d ", queryField, params.size() + 1));
            params.add(queryContent);
            operator = QueryOperator.AND;
        }
        return this;
    }

    public <T>QueryBuilder withLessOrEqual(String queryField, T queryContent) {
        if (queryContent != null) {
            str.append(" ");
            str.append(operator.getOperator());
            str.append(String.format(" %s <= ?%d ", queryField, params.size() + 1));
            params.add(queryContent);
            operator = QueryOperator.AND;
        }
        return this;
    }

    public <T>QueryBuilder withGreater(String queryField, T queryContent) {
        if (queryContent != null) {
            str.append(" ");
            str.append(operator.getOperator());
            str.append(String.format(" %s > ?%d", queryField, params.size() + 1));
            params.add(queryContent);
            operator = QueryOperator.AND;
        }
        return this;
    }

    public <T>QueryBuilder withLess(String queryField, T queryContent) {
        if (queryContent != null) {
            str.append(" ");
            str.append(operator.getOperator());
            str.append(String.format(" %s < ?%d ", queryField, params.size() + 1));
            params.add(queryContent);
            operator = QueryOperator.AND;
        }
        return this;
    }

    public QueryBuilder OR() {
        if (!this.operator.equals(QueryOperator.EMPTY)) {
            this.operator = QueryOperator.OR;
        }
        return this;
    }
    public QueryBuilder NOT(){
        this.not = !this.not;
        return this;
    }

    public QueryBuilder AND() {
        if (!this.operator.equals(QueryOperator.EMPTY)) {
            this.operator = QueryOperator.AND;
        }
        return this;
    }

    public QueryBuilder PARENTHESIS_OPEN() {
        str.append(" ");
        str.append(operator.getOperator());
        str.append(" ( ");
        str.append("true");
        operator = QueryOperator.AND;
        return this;
    }

    public QueryBuilder PARENTHESIS_CLOSE() {
        str.append(" ) ");
        operator = QueryOperator.AND;
        return this;
    }
    public QueryBuilder isNull(String queryField){
        if (queryField != null) {
            str.append(" ");
            str.append(operator.getOperator());
            str.append(String.format(" %s IS %s NULL ",queryField,(not)? "NOT" : ""));
            operator = QueryOperator.AND;
        }
        if(not){
            this.not = false;
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