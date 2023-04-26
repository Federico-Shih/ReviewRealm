<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/game.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review.css" />">
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <title><spring:message code="game.details.title" arguments="${game.name}"/></title>
</head>
<body>
<c:url value="/review/submit?gameId=${game.id}" var="sumbitReview"/>
<c:url value="/game/list/" var="gameList"/>
<jsp:include page="../static-components/navbar.jsp"/>
<div class="game-details-section">
    <div class="game-details">
        <span class="game-title"><c:out value="${game.name}"/></span>
        <div class="game-genres">
            <span class="game-genre"><spring:message code="genres"/></span>
            <c:forEach items="${game.genres}" var="genre">
                <div class="chip white-text"><a href="${gameList}?f-gen=${genre.id}" class="no-a-decoration"><spring:message code="${genre.name}"/></a></div>
            </c:forEach>
        </div>
        <div class="divider"></div>
        <p class="game-description"><c:out value="${game.description}"/></p>
        <div class="divider"></div>
    </div>
    <div class="game-card">
        <img class="game-img" src="${game.imageUrl}" alt="Game image">
        <span class="game-card-text">
            <spring:message code="publishing.date" arguments='${game.publishDate}'/>
        </span>
        <span class="game-card-text">
            <spring:message code="developer" arguments='${game.developer}'/>
        </span>
        <span class="game-card-text">
            <spring:message code="editor" arguments='${game.publisher}'/>
        </span>
    </div>
</div>
<c:if test="${!empty gameReviewData.reviewList}">
    <div class="statistics-section">
        <span class="game-review-section-header"> <spring:message code="game.details.review.statistics"/></span>
        <div class="statistics-list">
            <div class="game-statistics">
                <span class="game-statistics-header"><spring:message code="game.details.review.statistics.rating"/></span>
                <span class=game-statistics-number>${gameReviewData.averageRating}<span class="game-statistics-number-minor">/10</span></span>
            </div>
            <c:if test="${gameReviewData.averagePlatform != null}">
                <div class="game-statistics">
                    <span class="game-statistics-header"><spring:message code="game.details.review.statistics.platform" /></span>
                    <span class="game-statistics-text"><spring:message code="${gameReviewData.averagePlatform.code}"/> </span>
                </div>
            </c:if>
            <c:if test="${gameReviewData.averageDifficulty != null}">
                <div class="game-statistics">
                    <span class="game-statistics-header"><spring:message code="game.details.review.statistics.difficulty" /></span>
                    <span class="game-statistics-text"><spring:message code="${gameReviewData.averageDifficulty.code}"/> </span>
                </div>
            </c:if>
            <c:if test="${gameReviewData.averageGameTime != 0}">
                <div class="game-statistics">
                    <span class="game-statistics-header"><spring:message code="game.details.review.statistics.gametime" /></span>
                    <span class=game-statistics-number>${gameReviewData.averageGameTime/3600}hs</span>
                </div>
            </c:if>
            <c:if test="${gameReviewData.replayability != 0}">
                <div class="game-statistics">
                    <span class="game-statistics-header"><spring:message code="game.details.review.statistics.replayability" /></span>
                    <span class=game-statistics-number>${gameReviewData.replayability}<span class="game-statistics-number-minor">%</span></span>
                </div>
            </c:if>
            <c:if test="${gameReviewData.completability != 0}">
                <div class="game-statistics">
                    <span class="game-statistics-header"><spring:message code="game.details.review.statistics.completability" /></span>
                    <span class=game-statistics-number>${gameReviewData.completability}<span class="game-statistics-number-minor">%</span></span>
                </div>
            </c:if>
        </div>
    </div>
</c:if>

<div class="game-review-section">
    <div class="game-review-header">
        <span class="game-review-section-header"><spring:message code="game.details.other.reviews"/></span>
        <a class="btn waves-effect-light" href="${sumbitReview}"><spring:message code="game.details.new.review"/></a>
    </div>
    <div class="game-review-card-list row">
        <c:if test="${empty gameReviewData.reviewList}">
            <div class="no-reviews">
                <div class="s"></div>
                <p id="no-reviews-text"><spring:message code="game.details.first.review"/></p>
            </div>
        </c:if>
        <c:forEach var="review" items="${gameReviewData.reviewList}">
            <div class="col s12 m6">
                <div class="card">
                    <div class="review-card-header">
                        <div class="review-card-header-start">
                            <a href="/review/${review.id}">
                                <span id="review-card-title"><c:out value="${review.title}"/></span>
                            </a>
                        </div>
                        <div class="review-card-header-end">
                            <span id="review-card-score"><c:out value="${review.rating}"/></span>
                            <span id="review-card-score-outof">/10</span>
                            <i class="material-icons small">star</i>
                        </div>
                    </div>
                    <div class="review-card-body">
                        <span id="review-card-content"><c:out value="${review.content}"/></span>
                        <span id="review-card-date"><c:out value="${review.createdFormatted}"/></span>
                    </div>
                    <div class="divider"></div>
                    <div class="review-card-footer">
                    <span id="review-card-bottom-text review-card-author">
                        <spring:message code="review.by" arguments="@${review.author.username}"/>
                    </span>
                            <%--                            <c:forEach var="genre" items="${review.author.}">--%>
                            <%--                                <span class="chip-small-inverted"><c:out value="${genre.name}"/></span>--%>
                            <%--                            </c:forEach>--%>
                    </div>
                </div>
            </div>

        </c:forEach>
        <c:if test="${fn:length(gameReviewData.reviewList) % 2 == 1}">
            <div class="col m6"></div>
        </c:if>
    </div>
</div>
</body>
</html>
