package ar.edu.itba.paw.webapp.controller;

import org.javatuples.Pair;

import java.util.List;

public interface QueryController {

    default String toQueryString(List<Pair<String, Object>> entries) {
        StringBuilder str = new StringBuilder();
        str.append("?");
        entries.forEach((pair) -> {
            str.append(pair.getValue0());
            str.append("=");
            str.append(pair.getValue1());
            str.append("&");
        });
        return str.toString();
    }
}
