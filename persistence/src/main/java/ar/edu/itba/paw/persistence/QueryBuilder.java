package ar.edu.itba.paw.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class QueryBuilder {
    private final StringBuilder str = new StringBuilder();
    private final List<Object> params = new LinkedList<>();
    public <T>QueryBuilder withList(String queryField, List<T> querylist) {
        if (querylist != null && querylist.size() > 0) {
            if (str.length() != 0) {
                str.append(" AND ");
            }
            String gamesAmount = String.join(",", Collections.nCopies(querylist.size(), "?"));
            params.addAll(querylist);
            str.append(String.format("%s IN (", queryField));
            str.append(gamesAmount);
            str.append(")");
        }
        return this;
    }

    public QueryBuilder withSimilar(String queryField, String queryString) {
        if (queryString != null) {
            if (str.length() != 0) {
                str.append(" AND ");
            }
            str.append(String.format("%s ILIKE ?", queryField));
            params.add("%" + queryString + "%");
        }
        return this;
    }

    public <T>QueryBuilder withExact(String queryField, T queryContent) {
        if (queryContent != null) {
            if (str.length() != 0) {
                str.append(" AND ");
            }
            str.append(String.format("%s = ?", queryField));
            params.add(queryContent);
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
    // IMPLEMENT LARGER THAN LESSER THAN
}