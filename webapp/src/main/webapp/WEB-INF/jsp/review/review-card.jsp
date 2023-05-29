<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
<link rel="stylesheet" href="<c:url value="/css/main.css" />">
<link rel="stylesheet" href="<c:url value="/css/review.css" />">
<!-- Compiled and minified JavaScript -->
<script src="<c:url value="/js/materialize.min.js" />"></script>
<!-- hay que pasarle el objeto Review review -->
<c:url value="/" var="baseUrl"/>

<div class="col s12 l6">
    <div class="review-card-container">
        <div class="card review-card">
            <div class="review-card-header">
                <div class="review-card-header-start">
                    <a id="review-card-game-title" href="<c:url value="/game/${review.reviewedGame.id}"/>">
                    <span >
                        <c:out value="${review.reviewedGame.name}"/>
                    </span>
                    </a>
                    <div>
                        <c:forEach var="genre" items="${review.reviewedGame.genres}">
                        <span class="chip-small">
                            <a href="<c:url value="/game/list?f-gen=${genre.id}"/>" class="white-text">
                                    <spring:message code="${genre.name}"/>
                            </a>
                        </span>
                        </c:forEach>
                    </div>
                </div>
                <div class="review-card-header-end">
                    <span id="review-card-score"><c:out value="${review.rating}"/></span>
                    <span id="review-card-score-outof">/10</span>
                    <i id="review-card-star" class="material-icons">star</i>
                </div>
            </div>
            <div class="divider-h"></div>
            <div class="review-card-body">
                <c:url value="/review/${review.id}" var="reviewUrl" />
                <div class="f-column">
                    <a href="${reviewUrl}">
                        <span id="review-card-title"><c:out value="${review.title}"/></span>
                    </a>
                    <span class="block-with-text" id="review-card-content"><c:out value="${review.content}"/></span>
                </div>
                <div class="f-row f-jc-sbetween full-width">
                    <div>
                    <span id="review-time-played">
                        <c:if test="${review.gameLength != null}">
                            <spring:message code="review.gameLength" />:
                            <c:out value="${review.gameLengthInUnits.value}" />
                            <spring:message code="${review.gameLengthInUnits.key.code}" />
                        </c:if>
                        <c:if test="${review.completed}">
                            (<spring:message code="reviewForm.completed"/>)
                        </c:if>
                    </span>
                    </div>
                    <span id="review-card-date"><c:out value="${review.createdFormatted}"/></span>
                </div>
            </div>
            <div class="divider-h"></div>
            <div class="review-card-footer">
                <c:url value="/profile/${review.author.id}" var="profileUrl" />
                <div class="f-row">
                    <div class="review-card-author-footer">
                        <a href="${profileUrl}" id="review-card-bottom-text">
                        <span id="review-card-author">
                            <spring:message code="review.by" arguments="@${review.author.username}"/>
                        </span>
                        </a>
                    </div>
                    <div class="review-card-feedback-footer">
                        <span><c:out value="${review.likeCounter}"/></span>
                        <c:url value="/review/feedback/${review.id}" var="updateFeedback" />
                        <form name="likeFeedbackForm" class="feedback-form no-margin" method="post" action="${updateFeedback}">
                            <button name="feedback" class="no-padding btn-flat waves-effect waves-light ${(loggedUser == null || loggedUser.id == review.author.id)? "dark-disabled": ((review.feedback == "LIKE")? "white-text":"light-gray-text")}" value="LIKE">
                                <i class="material-icons like-dislike-buttons">thumb_up</i>
                            </button>
                            <input type="hidden" name="url" value="${baseUrl}"/>
                        </form>
                        <form name="dislikeFeedbackForm" class="feedback-form no-margin" method="post" action="${updateFeedback}" >
                            <button name="feedback" class="no-padding btn-flat waves-effect waves-light ${(loggedUser == null || loggedUser.id == review.author.id)? "dark-disabled": ((review.feedback == "DISLIKE")? "white-text":"light-gray-text")}" value="DISLIKE">
                                <i class="material-icons like-dislike-buttons">thumb_down</i>
                            </button>
                            <input type="hidden" name="url" value="${baseUrl}"/>
                        </form>
                    </div>
                </div>
                <div class="review-card-preferences-footer">
                    <c:if test="${!(empty review.author.preferences)}">
                    <span id="review-card-preferences">
                        <spring:message code="review.author.preferences"/>
                    </span>
                    </c:if>
                    <c:forEach var="genre" items="${review.author.preferences}" end="2">
                    <span class="chip-small-inverted">
                        <a href="<c:url value="/game/list?f-gen=${genre.id}"/>" class="rr-blue-text">
                                    <spring:message code="${genre.name}"/>
                            </a>
                    </span>
                    </c:forEach>
                    <c:if test="${fn:length(review.author.preferences) > 3}">
                    <span class="chip-small-inverted">
                        <c:out value="+${fn:length(review.author.preferences) - 3}"/>
                    </span>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

