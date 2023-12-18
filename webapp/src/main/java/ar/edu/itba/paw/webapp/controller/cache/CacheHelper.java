package ar.edu.itba.paw.webapp.controller.cache;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

public class CacheHelper {
    private CacheHelper() {
    }

    public static CacheControl buildCacheControl(int maxAge) {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(maxAge);
        return cacheControl;
    }

    public static <T> Response.ResponseBuilder conditionalCache(Response.ResponseBuilder responseBuilder, Request request, T entity, CacheControl cacheControl) {
        EntityTag tag = new EntityTag(Integer.toString(entity.hashCode()));
        Response.ResponseBuilder builder = request.evaluatePreconditions(tag);
        if (builder != null) return builder.cacheControl(cacheControl).tag(tag);
        return responseBuilder.cacheControl(cacheControl).tag(tag);
    }

    public static Response.ResponseBuilder unconditionalCache(Response.ResponseBuilder responseBuilder, int maxAge) {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(maxAge);
        responseBuilder.cacheControl(cacheControl);
        return responseBuilder;
    }
}
