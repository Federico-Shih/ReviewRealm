package ar.edu.itba.paw.webapp.controller.cache;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.function.Function;

public class CacheHelper {
    private CacheHelper() {
    }

    public static CacheControl buildCacheControl(int maxAge) {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(maxAge);
        return cacheControl;
    }

    public static <T> Response.ResponseBuilder buildEtagCache(Request request, T entity, CacheControl cacheControl, Function<T, Response.ResponseBuilder> defaultFunction) {
        EntityTag tag = new EntityTag(Integer.toString(entity.hashCode()));
        Response.ResponseBuilder builder = request.evaluatePreconditions(tag);
        if (builder != null) return builder.cacheControl(cacheControl).tag(tag);
        return defaultFunction.apply(entity).cacheControl(cacheControl).tag(tag);
    }
}
