package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.models.Paginated;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

public class PaginatedResponseHelper extends BaseResponse {
    private static final String TOTAL_PAGES_HEADER_NAME = "X-Reviewrealm-TotalPages";
    private static final String TOTAL_ELEMENTS_HEADER_NAME = "X-Reviewrealm-TotalElements";

    private PaginatedResponseHelper() {
    }

    public static Response.ResponseBuilder fromPaginated(UriInfo info, List<?> values, Paginated<?> pagination) {
        Response.ResponseBuilder response = Response.ok(new GenericEntity<List<?>>(values) {});
        response.link(info.getRequestUri(), "self");
        if (pagination.getPage() > 1) {
            response.link(info.getRequestUriBuilder().replaceQueryParam("page", pagination.getPage() - 1).build(), "prev");
        }
        if (pagination.getPage() < pagination.getTotalPages()) {
            response.link(info.getRequestUriBuilder().replaceQueryParam("page", pagination.getPage() + 1).build(), "next");
        }
        response.header(TOTAL_PAGES_HEADER_NAME, pagination.getTotalPages());
        response.header(TOTAL_ELEMENTS_HEADER_NAME, pagination.getTotalElements());
        return response;
    }

}
