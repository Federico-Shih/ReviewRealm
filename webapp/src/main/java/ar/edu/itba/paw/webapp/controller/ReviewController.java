package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.FeedbackType;
import ar.edu.itba.paw.exceptions.ReviewAlreadyExistsException;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.ReportService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AccessControl;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentReviewId;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentUserId;
import ar.edu.itba.paw.webapp.controller.forms.EditReviewForm;
import ar.edu.itba.paw.webapp.controller.forms.SubmitReviewForm;
import ar.edu.itba.paw.webapp.controller.mediatypes.VndType;
import ar.edu.itba.paw.webapp.controller.querycontainers.ReviewSearchQuery;
import ar.edu.itba.paw.webapp.controller.responses.FeedbackResponse;
import ar.edu.itba.paw.webapp.controller.responses.PaginatedResponseHelper;
import ar.edu.itba.paw.webapp.controller.responses.ReviewResponse;
import ar.edu.itba.paw.webapp.validators.FeedbackTypeForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("reviews")
@Component
public class ReviewController{
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewController.class);
    @Context
    private UriInfo uriInfo;
    private final GameService gameService;
    @Autowired
    private AccessControl accessControl;
    private final ReviewService reviewService;
    private final UserService userService;
    private final ReportService reportService;
    private static final int PAGE_SIZE = 8;
    private static final int INITIAL_PAGE = 1;
    private static final int MAX_SEARCH_RESULTS = 6;

    @Autowired
    public ReviewController(GameService gameService, ReviewService reviewService, UserService userService, ReportService reportService) {
        this.gameService = gameService;
        this.reviewService = reviewService;
        this.userService = userService;
        this.reportService = reportService;

    }

    /*
      TODO:
       GET /reviews
       POST /reviews
       DELETE /reviews/{reviewid}
       GET /reviews/{reviewid}
       PATCH /reviews/{reviewid}
       GET /reviews/{id}/feedback/{userId}
       POST /reviews/{id}/feedback{?feedbackType}
       DELETE /reviews/{id}/feedback/{userId}
    */


    @GET
    @Produces(VndType.APPLICATION_REVIEW_LIST)
    public Response getReviews(@Valid @BeanParam ReviewSearchQuery reviewSearchQuery) {
        User user = AuthenticationHelper.getLoggedUser(userService);
        final Paginated<Review> reviews = reviewService.searchReviews(reviewSearchQuery.getPage(), reviewSearchQuery.getFilter(), reviewSearchQuery.getOrdering(), user!=null? user.getId() : null);
        if (reviews.getTotalPages() == 0 || reviews.getList().isEmpty()) {
            return Response.noContent().build();
        }
        List<ReviewResponse> reviewResponseList = reviews.getList().stream().map((review) -> ReviewResponse.fromEntity(uriInfo, review, user == null? null: user.getId())).collect(Collectors.toList());
        return PaginatedResponseHelper.fromPaginated(uriInfo, reviewResponseList, reviews).build();
    }

    @POST
    @Produces(VndType.APPLICATION_REVIEW)
    @Consumes(VndType.APPLICATION_REVIEW_FORM)
    public Response createReview(@Valid @NotNull(message = "error.body.empty") final SubmitReviewForm form) throws ReviewAlreadyExistsException {
        User author = AuthenticationHelper.getLoggedUser(userService);
        Review createdReview = reviewService.createReview(
                form.getReviewTitle(),
                form.getReviewContent(),
                form.getReviewRating(),
                author.getId(),
                form.getGameId(),
                form.getDifficultyEnum(),
                form.getGameLengthSeconds(),
                form.getPlatformEnum(),
                form.getCompleted(),
                form.getReplayability()
        );
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path("/reviews").path(createdReview.getId().toString()).build())
                .entity(ReviewResponse.fromEntity(uriInfo, createdReview, author.getId()))
                .build();
    }

    @DELETE
    @Path("{id:\\d+}")
    @PreAuthorize("@accessControl.checkReviewAuthorOwnerOrMod(#id)")
    public Response deleteReview(@Valid @PathParam("id") long id) { // Todo: Hace falta el valid?
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);

        if (!reviewService.deleteReviewById(id, loggedUser.getId())) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().build();
    }

    @GET
    @Path("{id:\\d+}")
    @Produces(VndType.APPLICATION_REVIEW)
    public Response getReviewById(@Valid @PathParam("id") final long id) {
        Long activeUserId = AuthenticationHelper.getLoggedUser(userService) == null ? null: AuthenticationHelper.getLoggedUser(userService).getId();
        Optional<Review> possibleReview = reviewService.getReviewById(id, activeUserId);
        if (!possibleReview.isPresent())
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(ReviewResponse.fromEntity(uriInfo, possibleReview.get(), activeUserId)).build();
    }



    @PUT
    @Path("{id:\\d+}")
    @Consumes(VndType.APPLICATION_REVIEW_UPDATE)
    @PreAuthorize("@accessControl.checkReviewAuthorOwnerOrMod(#id)")
    public Response editReview(@Valid @ExistentReviewId @PathParam("id") final long id, @Valid @NotNull(message = "error.body.empty") final EditReviewForm form) {
        reviewService.updateReview(id, form.getReviewTitle(),
                form.getReviewContent(),
                form.getReviewRating(),
                form.getDifficultyEnum(),
                form.getGameLengthSeconds(),
                form.getPlatformEnum(),
                form.getCompleted(),
                form.getReplayability()
        );
        return Response.noContent().build();
    }

    // Si no existe, devuelve null, no me parece mal. TODO: quiza considerar 404?
    @GET
    @Path("{reviewId:\\d+}/feedback/{userId:\\d+}")
    @Produces(VndType.APPLICATION_REVIEW_FEEDBACK)
    public Response getReviewFeedback(@Valid @ExistentReviewId @PathParam("reviewId") long reviewId, @Valid @ExistentUserId @PathParam("userId") long userId) {
        Optional<Review> perhapsReview = reviewService.getReviewById(reviewId, userId);
        return Response.ok(FeedbackResponse.fromEntity(uriInfo, userId, perhapsReview.get().getId(), perhapsReview.get().getFeedback())).build();
    }

    @POST
    @Path("{reviewId:\\d+}/feedback")
    @Consumes(VndType.APPLICATION_REVIEW_FEEDBACK_FORM)
    @Produces(VndType.APPLICATION_REVIEW_FEEDBACK)
    @PreAuthorize("@accessControl.checkUserIsNotAuthor(#reviewId)")
    public Response createReviewFeedback(@Valid @ExistentReviewId @PathParam("reviewId") long reviewId, @Valid @NotNull(message = "error.body.empty") FeedbackTypeForm feedbackType) {
        FeedbackType fb = feedbackType.transformToEnum();
        long userId = AuthenticationHelper.getLoggedUser(userService).getId();

        reviewService.updateOrCreateReviewFeedback(reviewId,userId,fb);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path("reviews").path(String.valueOf(reviewId)).path("feedback").path(String.valueOf(userId)).build())
                .entity(FeedbackResponse.fromEntity(uriInfo, userId, reviewId, fb))
                .build();
    }


    @DELETE
    @Path("{reviewId:\\d+}/feedback/{userId:\\d+}")
    @PreAuthorize("@accessControl.checkLoggedIsCreatorOfFeedback(#reviewId, #userId)")
    public Response deleteReviewFeedback(@Valid @ExistentReviewId @PathParam("reviewId") long reviewId, @Valid @ExistentUserId @PathParam("userId") long userId) {
        return reviewService.deleteReviewFeedback(reviewId,userId) ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
    }

}
