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
<c:url value="/review/${review.id}" var="reviewUrl" />
<c:url value="/profile/${review.author.id}" var="profileUrl" />
<c:set var="avatar" value="/static/avatars/${review.author.avatarId}.png"/>
<c:url value="${review.reviewedGame.imageUrl}" var="imageUrl" />

<div class="col s12 l6">
    <div class="${hideFooter != true ? "review-card-container" : "review-card-container-small"}">
        <div class="card review-card">
            <div class="review-card-header">
                <div class="review-card-header-start">
                    <a href="${reviewUrl}" class="no-wrap overflow-ellipsis full-width">
                        <span id="review-card-title" class=""><c:out value="${review.title}"/></span>
                    </a>
                    <div class="review-card-author-header">
                        <a href="${profileUrl}" id="review-card-bottom-text" class="f-row f-ai-center">
                            <span id="review-card-author" class="no-wrap">
                                <spring:message code="review.by" arguments="@${review.author.username}"/>
                            </span>
                            <img id="author-profile-image"
                                 src="<c:url value="${avatar}"/>"
                                 alt="profilePic"/>
                        </a>
                    </div>
                    <div class="review-card-preferences-header">
                        <c:if test="${!(empty review.author.preferences)}">
                            <i id="review-card-preferences" class="material-icons">
                                favorite
                            </i>
                        </c:if>
                        <c:forEach var="genre" items="${review.author.preferences}" end="1">
                            <span class="chip-small-inverted">
                                <a href="<c:url value="/game/list?f-gen=${genre.id}"/>" class="rr-blue-text">
                                            <spring:message code="${genre.name}"/>
                                    </a>
                            </span>
                        </c:forEach>
                        <c:if test="${fn:length(review.author.preferences) > 2}">
                            <span class="chip-small-inverted">
                                <c:out value="+${fn:length(review.author.preferences) - 2}"/>
                            </span>
                        </c:if>
                    </div>

                </div>
                <div class="review-card-header-end f-column f-ai-end">
                    <div class="f-row f-ai-baseline f-jc-end">
                        <span id="review-card-score"><c:out value="${review.rating}"/></span>
                        <span id="review-card-score-outof">/10</span>
                        <i id="review-card-star" class="material-icons">star</i>
                    </div>
                    <span id="review-card-date"><c:out value="${review.createdFormatted}"/></span>
                </div>
            </div>
            <div class="divider-h"></div>
            <div class="review-card-body">
                <div class="f-column">
                    <span class="block-with-text" id="review-card-content"><c:out value="${review.content}"/></span>
                </div>
                <div class="review-card-feedback-footer">
                    <i class="material-icons" id="popularity-icon">thumbs_up_down</i>
                    <span><c:out value="${review.likeCounter}"/></span>
                </div>
            </div>

            <c:if test="${hideFooter != true}">
                <div class="divider-h"></div>
                <div class="review-card-footer">
                    <img id="game-image" src="${imageUrl}" alt="Game image">
                    <div class="review-card-footer-info">
                        <a id="review-card-game-title" class="no-wrap overflow-ellipsis full-width" href="<c:url value="/game/${review.reviewedGame.id}"/>">
                    <span>
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
                </div>
            </c:if>

        </div>
    </div>
</div>

