package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.controller.helpers.CurryingFunction;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.function.Function;

public class UriInfoController {
    @Context
    private UriInfo uriInfo;

    protected  <T, R>Function<T, R> currifyUriInfo(CurryingFunction<UriInfo, T, R> fun) {
        return fun.curry(fun, uriInfo);
    }
}
