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
    <link rel="stylesheet" href="<c:url value="/css/for-you.css"/>">
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <script src="<c:url value="/js/reviewfeedback.js"/>"></script>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <title><spring:message code="game.details.title" arguments="${game.name}"/></title>
</head>
<body>
<c:url value="/review/submit?gameId=${game.id}" var="sumbitReview"/>
<c:url value="/game/list/" var="gameList"/>
<c:url value="/for-you/discovery/next" var="nextInQueue"/>
<c:url value="/for-you/discovery/previous" var="previousInQueue"/>
<jsp:include page="../static-components/navbar.jsp"/>
<c:if test="${!emptyQueue}">
<div class="game-details-section row">
    <div class="game-details col s12 l7">
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
    <div class="game-card col s12 l4 push-l1">
        <c:url value="${game.imageUrl}" var="imageUrl" />
        <img class="game-img" src="${imageUrl}" alt="Game image">
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
                <span class=game-statistics-number>${gameReviewData.averageRatingString}<span class="game-statistics-number-minor">/10</span></span>
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
    <c:url value="/for-you" var="forYou"/>
    <div class="discovery-queue-row">
        <c:if test="${positionInQueue != 0}">
            <form action="${previousInQueue}" method="post">
                <button class="btn waves-light">
                    <i class="material-icons left">chevron_left</i>
                    <span><spring:message code="for-you.previousqueue"/></span>
                </button>
            </form>
        </c:if>
        <c:if test="${positionInQueue == 0}">
            <a href="${forYou}">
                <button class="btn waves-light" type="button">
                    <i class="material-icons right">chevron_left</i>
                    <span><spring:message code="for-you.return"/></span>
                </button>
            </a>
        </c:if>
        <c:if test="${!isLast}">
            <form action="${nextInQueue}" method="post">
                <button class="btn waves-light">
                    <span><spring:message code="for-you.nextqueue"/></span>
                    <i class="material-icons right">chevron_right</i>
                </button>
            </form>
        </c:if>
        <c:if test="${isLast}">
            <a href="${forYou}">
                <button class="btn waves-light" type="button">
                    <span><spring:message code="for-you.return"/></span>
                    <i class="material-icons right">chevron_right</i>
                </button>
            </a>
        </c:if>

    </div>
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
            <c:set var="review" value="${review}" scope="request" />
            <c:import url="/WEB-INF/jsp/review/review-card.jsp" />
        </c:forEach>
        <c:if test="${fn:length(gameReviewData.reviewList) % 2 == 1}">
            <div class="col m6"></div>
        </c:if>
    </div>
</div>
</c:if>
<c:if test="${emptyQueue}">
    <div class="f-row center-align">
        <div class="card lime darken-3">--%>
            <div class="card-content white-text f-row f-gap-2">
                <div class="">
                    <i class="material-icons medium">warning</i>
                </div>
                <div class="">
                    <span class="card-title"><spring:message code="for-you.norecomendedgames" /></span>
                    <a href="<c:url value="/profile/settings/preferences"/>" class="no-a-decoration btn-flat waves-effect waves-light border-button f-row f-jc-center f-ai-center">
                        <span><spring:message code="for-you.norecomendedgames.doit"/></span>
                    </a>
                </div>
            </div>
        </div>
    </div>
</c:if>
</body>
</html>
