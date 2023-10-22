package ar.edu.itba.paw.webapp.controller.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@NotNull(message = "error.body.empty")
public class AcceptRejectReportForm {
    @NotNull(message = "NotNull.property")
    @Pattern(regexp ="^(accepted|rejected)$", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
