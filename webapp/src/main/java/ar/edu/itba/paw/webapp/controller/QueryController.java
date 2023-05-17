package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.Pair;
import java.util.List;

public interface QueryController {

    default String toQueryString(List<Pair<String, Object>> entries) {
        StringBuilder str = new StringBuilder();
        str.append("?");
        entries.forEach((pair) -> {
            str.append(pair.getKey());
            str.append("=");
            str.append(pair.getValue());
            str.append("&");
        });
        return str.toString();
    }
}
