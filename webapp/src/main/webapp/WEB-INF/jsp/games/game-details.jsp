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
    <link rel="stylesheet" href="<c:url value="/css/for-you.css" />">
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <script src="<c:url value="/js/reviewfeedback.js"/>"></script>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <title><spring:message code="game.details.title" arguments="${game.name}"/></title>
    <script>
        <c:if test="${created}">
            document.addEventListener('DOMContentLoaded', function () {
                M.toast({html: '<spring:message code="game.created" />', classes: 'created-toast'});
            });
        </c:if>
    </script>
</head>
<body>
<c:url value="/review/submit/${game.id}" var="sumbitReview"/>
<c:url value="/game/list/" var="gameList"/>
<jsp:include page="../static-components/navbar.jsp"/>
<div class="row margin-2">
    <div class="col s12 valign-wrapper breadcrumb-align">
        <a href="${gameList}" class="breadcrumb"><spring:message code="game.list.placeholder.search" /></a>
        <a href="#" class="breadcrumb"><c:out value="${game.name}" /></a>
    </div>
</div>
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
<c:if test="${!empty reviews}">
    <div class="statistics-section">
        <span class="game-review-section-header"> <spring:message code="game.details.review.statistics"/></span>
        <div class="statistics-list f-gap-1">
            <div class="card indigo darken-4 game-statistics-width">
                <div class="card-content white-text f-column f-gap-1 f-jc-start f-ai-center">
                    <span class="game-statistics-header"><spring:message code="game.details.review.statistics.rating"/></span>
                    <span class=game-statistics-number>${gameReviewData.averageRatingString}<span class="game-statistics-number-minor">/10</span></span>
                </div>
            </div>
            <c:if test="${gameReviewData.averagePlatform != null}">
                <div class="card indigo darken-4 game-statistics-width">
                    <div class="card-content white-text f-column f-gap-1 f-jc-start f-ai-center">
                        <span class="game-statistics-header"><spring:message code="game.details.review.statistics.platform" /></span>
                        <span class="game-statistics-text"><spring:message code="${gameReviewData.averagePlatform.code}"/> </span>
                    </div>
                </div>
            </c:if>
            <c:if test="${gameReviewData.averageDifficulty != null}">
                <div class="card indigo darken-4 game-statistics-width">
                    <div class="card-content white-text f-column f-gap-1 f-jc-start f-ai-center">
                        <span class="game-statistics-header"><spring:message code="game.details.review.statistics.difficulty" /></span>
                        <span class="game-statistics-text"><spring:message code="${gameReviewData.averageDifficulty.code}"/> </span>
                    </div>
                </div>
            </c:if>
            <c:if test="${gameReviewData.averageGameTime != 0}">
                <div class="card indigo darken-4 game-statistics-width">
                    <div class="card-content white-text f-column f-gap-1 f-jc-start f-ai-center">
                        <span class="game-statistics-header"><spring:message code="game.details.review.statistics.gametime" /></span>
                        <span class=game-statistics-number>${gameReviewData.averageGameTimeStringHs}hs</span>
                    </div>
                </div>
            </c:if>
            <c:if test="${gameReviewData.replayability != 0}">
                <div class="card indigo darken-4 game-statistics-width">
                    <div class="card-content white-text f-column f-gap-1 f-jc-start f-ai-center">
                        <span class="game-statistics-header"><spring:message code="game.details.review.statistics.replayability" /></span>
                        <span class=game-statistics-number>${gameReviewData.replayabilityString}<span class="game-statistics-number-minor">%</span></span>
                    </div>
                </div>
            </c:if>
            <c:if test="${gameReviewData.completability != 0}">
                <div class="card indigo darken-4 game-statistics-width">
                    <div class="card-content white-text f-column f-gap-1 f-jc-start f-ai-center">
                        <span class="game-statistics-header"><spring:message code="game.details.review.statistics.completability" /></span>
                        <span class=game-statistics-number>${gameReviewData.completabilityString}<span class="game-statistics-number-minor">%</span></span>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
</c:if>
<c:if test="${discoveryQueue}">
    <c:url value="/for-you" var="forYou"/>
    <div class="discovery-queue-row">
        <c:if test="${positionInQueue != 0}">
            <a href="?position=${positionInQueue - 1}">
                <button class="btn waves-light">
                    <i class="material-icons left">chevron_left</i>
                    <span><spring:message code="for-you.previousqueue"/></span>
                </button>
            </a>
        </c:if>
        <c:if test="${positionInQueue == 0}">
            <a href="${forYou}">
                <button class="btn waves-light" type="button">
                    <i class="material-icons right">chevron_left</i>
                    <span><spring:message code="for-you.return"/></span>
                </button>
            </a>
        </c:if>
        <a href="?position=${positionInQueue+1}">
            <button class="btn waves-light">
                <span><spring:message code="for-you.nextqueue"/></span>
                <i class="material-icons right">chevron_right</i>
            </button>
        </a>
    </div>
</c:if>

<div class="game-review-section">
    <div class="game-review-header">
        <span class="game-review-section-header"><spring:message code="game.details.other.reviews"/></span>
        <a class="btn waves-effect-light" href="${sumbitReview}"><spring:message code="game.details.new.review"/></a>
    </div>
    <div class="game-review-card-list row">
        <c:if test="${empty reviews}">
            <div class="no-reviews">
                <div class="s"></div>
                <p id="no-reviews-text"><spring:message code="game.details.first.review"/></p>
            </div>
        </c:if>
        <c:set var="hideFooter" value="true" scope="request" />
        <c:forEach var="review" items="${reviews}">
            <c:set var="review" value="${review}" scope="request" />
            <c:import url="/WEB-INF/jsp/review/review-card.jsp" />
        </c:forEach>
        <c:if test="${fn:length(reviews) % 2 == 1}">
            <div class="col m6"></div>
        </c:if>
    </div>
    <div class="row">
        <ul class="center-align pagination">
            <c:if test="${currentPage > 1}">
                <li class="waves-effect"><a href="${queriesToKeepAtPageChange}page=${currentPage-1}"><i
                        class="material-icons">chevron_left</i></a></li>
            </c:if>
            <c:forEach var="i" begin="${initialPage}" end="${maxPages}">
                <c:if test="${i == currentPage}">
                    <li class="pagination-active"><a href="${queriesToKeepAtPageChange}page=${i}">${i}</a></li>
                </c:if>
                <c:if test="${i != currentPage}">
                    <li class="waves-effect"><a href="${queriesToKeepAtPageChange}page=${i}">${i}</a></li>
                </c:if>
            </c:forEach>
            <c:if test="${currentPage < maxPages}">
                <li class="waves-effect"><a href="${queriesToKeepAtPageChange}page=${currentPage+1}"><i
                        class="material-icons">chevron_right</i></a></li>
            </c:if>
        </ul>
    </div>
</div>
</body>
</html>
