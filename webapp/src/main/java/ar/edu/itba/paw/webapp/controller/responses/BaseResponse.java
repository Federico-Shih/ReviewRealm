package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.enums.Genre;
import org.apache.commons.lang3.NotImplementedException;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseResponse {
    private final Map<String, URI> links = new HashMap<>();

    public void link(String key, URI uriInfo) {
        links.put(key, uriInfo);
    }

    public Map<String, URI> getLinks() {
        return links;
    }

    public void setLinks(Map<String, URI> links) {
        this.links.putAll(links);
    }

//    public static <T>URI getLinkFromEntity(final UriInfo uri, T entity) {
//        throw new NotImplementedException();
//    }
}
