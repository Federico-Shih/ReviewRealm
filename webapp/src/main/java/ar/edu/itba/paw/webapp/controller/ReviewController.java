package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.dtos.filtering.ReviewFilter;
import ar.edu.itba.paw.enums.FeedbackType;
import ar.edu.itba.paw.exceptions.GenreNotFoundException;
import ar.edu.itba.paw.exceptions.ReviewAlreadyExistsException;
import ar.edu.itba.paw.exceptions.ReviewNotFoundException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;

import ar.edu.itba.paw.webapp.controller.forms.EditReviewForm;
import ar.edu.itba.paw.webapp.controller.forms.SubmitReviewForm;
import ar.edu.itba.paw.webapp.controller.mediatypes.VndType;
import ar.edu.itba.paw.webapp.controller.querycontainers.ReviewSearchQuery;
import ar.edu.itba.paw.webapp.controller.responses.FeedbackResponse;
import ar.edu.itba.paw.webapp.controller.responses.PaginatedResponseHelper;
import ar.edu.itba.paw.webapp.controller.responses.ReviewResponse;
import ar.edu.itba.paw.webapp.exceptions.CustomRuntimeException;
import ar.edu.itba.paw.webapp.controller.forms.FeedbackTypeForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("reviews")
@Component
public class ReviewController {
    @Context
    private UriInfo uriInfo;
    private final ReviewService reviewService;
    private final UserService userService;

    @Autowired
    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;

    }

    @GET
    @Produces(VndType.APPLICATION_REVIEW_LIST)
    public Response getReviews(@Valid @BeanParam ReviewSearchQuery reviewSearchQuery) {
        User user = AuthenticationHelper.getLoggedUser(userService);

        ReviewFilter reviewFilter = reviewSearchQuery.getFilter();
        if (reviewFilter.getRecommendedFor() != null || reviewFilter.getFromFollowing() != null || reviewFilter.getNewForUser() != null) {
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            if (
                    reviewFilter.getRecommendedFor() != null && !Objects.equals(reviewFilter.getRecommendedFor(), user.getId())
                            || reviewFilter.getFromFollowing() != null && !Objects.equals(reviewFilter.getFromFollowing(), user.getId())
                            || reviewFilter.getNewForUser() != null && !Objects.equals(reviewFilter.getNewForUser(), user.getId())) {
                throw new CustomRuntimeException(Response.Status.FORBIDDEN, "unauthorized");
            }
        }
        Paginated<Review> reviews;
        try {
            reviews = reviewService.searchReviews(reviewSearchQuery.getPage(), reviewSearchQuery.getFilter(), reviewSearchQuery.getOrdering(), user != null ? user.getId() : null);
        }catch (UserNotFoundException e){
            return Response.noContent().build();
        }
        if (reviews.getTotalPages() == 0 || reviews.getList().isEmpty()) {
            return Response.noContent().build();
        }
        List<ReviewResponse> reviewResponseList = reviews.getList().stream().map((review) -> ReviewResponse.fromEntity(uriInfo, review, user == null ? null : user.getId())).collect(Collectors.toList());
        return PaginatedResponseHelper.fromPaginated(uriInfo, reviewResponseList, reviews).build();
    }

    @POST
    @Produces(VndType.APPLICATION_REVIEW)
    @Consumes(VndType.APPLICATION_REVIEW_FORM)
    public Response createReview(@Valid @NotNull(message = "error.body.empty") final SubmitReviewForm form) throws ReviewAlreadyExistsException {
        long author = AuthenticationHelper.getLoggedUser(userService).getId();
        Review createdReview = reviewService.createReview(
                form.getReviewTitle(),
                form.getReviewContent(),
                form.getReviewRating(),
                author,
                form.getGameId(),
                form.getDifficultyEnum(),
                form.getGameLengthSeconds(),
                form.getPlatformEnum(),
                form.getCompleted(),
                form.getReplayability()
        );
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path("/reviews").path(createdReview.getId().toString()).build())
                .entity(ReviewResponse.fromEntity(uriInfo, createdReview, author))
                .build();
    }

    @DELETE
    @Path("{id:\\d+}")
    @PreAuthorize("@accessControl.checkReviewAuthorOwnerOrMod(#id)")
    public Response deleteReview(@PathParam("id") long id) {
        long loggedUser = AuthenticationHelper.getLoggedUser(userService).getId();

        if (!reviewService.deleteReviewById(id, loggedUser)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    @GET
    @Path("{id:\\d+}")
    @Produces(VndType.APPLICATION_REVIEW)
    public Response getReviewById(@PathParam("id") final long id) {
        User activeUser = AuthenticationHelper.getLoggedUser(userService);
        Optional<Review> possibleReview = reviewService.getReviewById(id, activeUser == null ? null : activeUser.getId());
        if (!possibleReview.isPresent())
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(ReviewResponse.fromEntity(uriInfo, possibleReview.get(), activeUser == null ? null : activeUser.getId())).build();
    }


    @PUT
    @Path("{id:\\d+}")
    @Consumes(VndType.APPLICATION_REVIEW_UPDATE)
    @PreAuthorize("@accessControl.checkReviewAuthorOwner(#id)")
    public Response editReview(@PathParam("id") final long id, @Valid @NotNull(message = "error.body.empty") final EditReviewForm form) {
        Review editedReview = reviewService.updateReview(id, form.getReviewTitle(),
                form.getReviewContent(),
                form.getReviewRating(),
                form.getDifficultyEnum(),
                form.getGameLengthSeconds(),
                form.getPlatformEnum(),
                form.getCompleted(),
                form.getReplayability()
        );
        return Response.ok(ReviewResponse.fromEntity(uriInfo, editedReview, AuthenticationHelper.getLoggedUser(userService).getId())).build();
    }

    @GET
    @Path("{reviewId:\\d+}/feedback/{userId:\\d+}")
    @Produces(VndType.APPLICATION_REVIEW_FEEDBACK)
    public Response getReviewFeedback(@PathParam("reviewId") long reviewId, @PathParam("userId") long userId) {
        if(!userService.getUserById(userId).isPresent()) throw new UserNotFoundException();
        Review review = reviewService.getReviewById(reviewId, userId).orElseThrow(ReviewNotFoundException::new);
        if (review.getFeedback() == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(FeedbackResponse.fromEntity(uriInfo, userId, review.getId(), review.getFeedback())).build();
    }

    @PUT
    @Path("{reviewId:\\d+}/feedback/{userId:\\d+}")
    @Consumes(VndType.APPLICATION_REVIEW_FEEDBACK_FORM)
    @Produces(VndType.APPLICATION_REVIEW_FEEDBACK)
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#userId)")
    public Response updateReviewFeedback(@PathParam("reviewId") long reviewId, @PathParam("userId") long userId, @Valid @NotNull(message = "error.body.empty") FeedbackTypeForm feedbackType) {
        FeedbackType fb = feedbackType.transformToEnum();
        Review review = reviewService.getReviewById(reviewId, userId).orElseThrow(ReviewNotFoundException::new);
        boolean existsOldFeedback = review.getFeedback() != null;
        reviewService.updateOrCreateReviewFeedback(reviewId, userId, fb);
        if (existsOldFeedback) {
            return Response.ok(FeedbackResponse.fromEntity(uriInfo, userId, reviewId, fb)).build();
        }
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path("reviews").path(String.valueOf(reviewId)).path("feedback").path(String.valueOf(userId)).build())
                .entity(FeedbackResponse.fromEntity(uriInfo, userId, reviewId, fb))
                .build();
    }


    @DELETE
    @Path("{reviewId:\\d+}/feedback/{userId:\\d+}")
    @PreAuthorize("@accessControl.checkLoggedIsCreatorOfFeedback(#reviewId, #userId)")
    public Response deleteReviewFeedback(@PathParam("reviewId") long reviewId, @PathParam("userId") long userId) {
        return reviewService.deleteReviewFeedback(reviewId, userId) ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
    }

}
