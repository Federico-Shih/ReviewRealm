package ar.edu.itba.paw.webapp.controller.responses;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseResponse {
    private final Map<String, URI> links = new HashMap<>();

    public void add(String key, URI uriInfo) {
        links.put(key, uriInfo);
    }

    public Map<String, URI> getLinks() {
        return links;
    }

    public void setLinks(Map<String, URI> links) {
        this.links.putAll(links);
    }
}
