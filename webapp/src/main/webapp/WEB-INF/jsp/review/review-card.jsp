<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
<link rel="stylesheet" href="<c:url value="/css/main.css" />">
<link rel="stylesheet" href="<c:url value="/css/review.css" />">
<!-- Compiled and minified JavaScript -->
<script src="<c:url value="/js/materialize.min.js" />"></script>
<!-- hay que pasarle el objeto Review review -->
<c:url value="/" var="baseUrl"/>

<div class="col s12 l6">
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
            <a href="${reviewUrl}">
                <span id="review-card-title"><c:out value="${review.title}"/></span>
            </a>
            <span id="review-card-content"><c:out value="${review.content}"/></span>
            <span id="review-card-date"><c:out value="${review.createdFormatted}"/></span>
        </div>
        <div class="divider-h"></div>
        <div class="review-card-footer">
            <c:url value="/profile/${review.author.id}" var="profileUrl" />
            <a href="${profileUrl}" id="review-card-bottom-text">
                <span id="review-card-author">
                    <spring:message code="review.by" arguments="@${review.author.username}"/>
                </span>
            </a>
            <div class="review-card-feedback-footer">
                <c:if test="${review.likeCounter != 0}">
                    <span><c:out value="${review.likeCounter}"/></span>
                </c:if>
                <c:url value="/review/feedback/${review.id}" var="updateFeedback" />
                <form name="likeFeedbackForm" class="feedback-form no-margin" method="post" action="${updateFeedback}">
                    <button name="feedback" class="no-padding btn-flat waves-effect waves-light ${ (review.feedback == "LIKE")? "white-text":""}" value="LIKE">
                        <i class="material-icons like-dislike-buttons">thumb_up</i>
                    </button>
                    <input type="hidden" name="url" value="${baseUrl}"/>
                </form>
                <form name="dislikeFeedbackForm" class="feedback-form no-margin" method="post" action="${updateFeedback}" >
                    <button name="feedback" class="no-padding btn-flat waves-effect waves-light ${ (review.feedback == "DISLIKE")? "white-text":""}" value="DISLIKE">
                        <i class="material-icons like-dislike-buttons">thumb_down</i>
                    </button>
                    <input type="hidden" name="url" value="${baseUrl}"/>
                </form>
            </div>
            <%--                    TODO: PREFERENCIAS DE USUARIO--%>
            <%--                    <c:forEach var="genre" items="${review.author.preferences}">--%>
            <%--                        <span class="chip-small-inverted"><c:out value="${genre.name}"/></span>--%>
            <%--                    </c:forEach>--%>
        </div>
    </div>
</div>

