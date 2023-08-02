package ar.edu.itba.paw.webapp.controller.responses;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseResponse<T, U> {
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
