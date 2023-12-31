package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Mission;
import ar.edu.itba.paw.models.Pair;
import ar.edu.itba.paw.servicesinterfaces.MissionService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import ar.edu.itba.paw.webapp.controller.cache.CacheHelper;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.mediatypes.VndType;
import ar.edu.itba.paw.webapp.controller.responses.MissionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;

@Path("missions")
@Component
public class MissionController {
    private final MessageSource messageSource;
    private final MissionService missionService;

    private final UserService us;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public MissionController(MessageSource messageSource, MissionService missionService, UserService us) {
        this.messageSource = messageSource;
        this.missionService = missionService;
        this.us = us;
    }

    @GET
    @Path("{id}")
    @Produces(VndType.APPLICATION_MISSION)
    public Response getMissionById(@PathParam("id") String missionName, @Context Request request) {
        return missionService.getMissionById(missionName)
                .map(mission ->
                        CacheHelper.conditionalCache(
                            Response.ok(MissionResponse.fromEntity(uriInfo, mission, getLocalizedMission(mission), getLocalizedFrequency(mission.getFrequency()))),
                            request,
                            mission,
                            CacheHelper.buildCacheControl(86400)
                        )
                ).orElse(Response.status(Response.Status.NOT_FOUND)).build();
    }

    @GET
    @Produces(VndType.APPLICATION_MISSION_LIST)
    public Response getMission(@Context Request request) {
        List<Mission> missions = missionService.getMissions(AuthenticationHelper.getLoggedUser(us));
        return CacheHelper.conditionalCache(
            Response.ok(
                new GenericEntity<List<MissionResponse>>(
                    missions.stream()
                        .map((mission ->
                            MissionResponse
                                .fromEntity(
                                    uriInfo,
                                    mission,
                                    getLocalizedMission(mission),
                                    getLocalizedFrequency(mission.getFrequency())
                                )
                        )).collect(Collectors.toList())) {}
            ),
            request,
            missions,
            CacheHelper.buildCacheControl(60)
        ).build();
    }

    private Pair<String, String> getLocalizedMission(Mission mission) {
        return Pair.of(
                messageSource.getMessage(mission.getTitle(), null, LocaleHelper.getLocale()),
                messageSource.getMessage(mission.getDescription(), new Object[]{mission.getTarget()}, LocaleHelper.getLocale()));
    }

    private String getLocalizedFrequency(Mission.MissionFrequency frequency) {
        return messageSource.getMessage(frequency.getName(), null, LocaleHelper.getLocale());
    }
}
