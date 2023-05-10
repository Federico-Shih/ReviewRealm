package ar.edu.itba.paw.persistence.helpers;

import java.util.LinkedList;
import java.util.List;

public class UpdateBuilder {
    private final StringBuilder str = new StringBuilder();
    private final List<Object> parameters = new LinkedList<>();

    boolean first = true;

    public UpdateBuilder set(String field, Object value) {
        if (value != null) {
            if (!first) {
                str.append(", ");
            }
            str.append(field);
            str.append(" = ?");
            parameters.add(value);
            first = false;
        }
        return this;
    }

    public String toQuery() {
        if (str.length() > 0) {
            return " SET " + str;
        }
        return str.toString();
    }

    public List<Object> getParameters() {
        return parameters;
    }
}
