package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.models.Paginated;

import javax.ws.rs.core.UriInfo;
import java.util.List;

public class ListResponse<V> extends BaseResponse {
    private List<V> data;

    public static <V> ListResponse<V> fromEntity(UriInfo info, List<V> values) {
        ListResponse<V> response = new ListResponse<>();
        response.data = values;
        response.link("self", info.getRequestUri());
        return response;
    }

    public void setData(List<V> data) {
        this.data = data;
    }

    public List<V> getData() {
        return data;
    }

}
