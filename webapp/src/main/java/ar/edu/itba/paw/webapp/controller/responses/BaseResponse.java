package ar.edu.itba.paw.webapp.controller.responses;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BaseResponse {
    private final Map<String, URI> _links = new HashMap<>();

    public void add(String key, URI uriInfo) {
        _links.put(key, uriInfo);
    }

    public Map<String, URI> getLinks() {
        return _links;
    }

    public void setLinks(Map<String, URI> links) {
        _links.putAll(links);
    }
}
