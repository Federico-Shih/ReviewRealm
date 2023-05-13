<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
    <title><spring:message code="review.title" arguments="${game.name}" /></title> <!-- Compiled and minified CSS -->
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review/review-page.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review.css" />">
    <!-- Compiled and minified JavaScript -->
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <script>
        <c:if test="${isModerated}">
            document.addEventListener('DOMContentLoaded', function() {
                var elems = document.querySelectorAll('.dropdown-trigger');
                var instances = M.Dropdown.init(elems, {
                    alignment: 'right',
                    coverTrigger: false,
                });
            });
        </c:if>
    </script>
    <script src="<c:url value="/js/reviewfeedback.js" />"></script>

</head>

<spring:message code="reviewForm.title.placeholder" var="titlePlaceholder"/>
<spring:message code="reviewForm.content.placeholder" var="contentPlaceholder"/>
<spring:message code="review.author.generic" var="authorPlaceholder"/>
<c:url value="/game/${game.id}" var="gameUrl" />
<c:url value="/" var="baseUrl"/>
<body>
<jsp:include page="/WEB-INF/jsp/static-components/navbar.jsp"/>
<div class="row margin-2">
    <c:url value="/" var="reviewList" />
    <div class="col s12">
        <a href="${reviewList}" class="breadcrumb"><spring:message code="review.search" /></a>
        <a href="#" class="breadcrumb"><c:out value="${game.name}" /> </a>
    </div>
</div>
<div class="container">
    <div class="row">
        <div class="col s12 l8">
            <div class="card card-background">
                <div class="card-content">
                    <div class="card-title row">
                        <div class="col s8">
                            <c:out value="${review.title}" />
                        </div>
                        <div class="col s2 ${isModerated ? "" : "s4 right"} right-align">
                            <span id="review-card-score"><c:out value="${review.rating}" /></span>/10
                        </div>
                        <c:if test="${isModerated}">
                        <div class="col s2 right-align">
                                <div>
                                    <a class="dropdown-trigger btn valign-wrapper" href="#" data-target="dropdown-mod">
                                        <i class="material-icons" aria-hidden="true">settings</i>
                                    </a>
                                    <ul id="dropdown-mod" class="dropdown-content dropdown-container">
                                        <li>
                                            <c:url value="/review/delete/${review.id}" var="moderateUrl" />
                                            <form action="${moderateUrl}" method="post" >
                                                <button class="waves-effect btn-flat valign-wrapper red-text f-row f-ai-center" type="submit">
                                                    <i class="material-icons">delete</i>
                                                    <span><spring:message code="review.delete" /></span>
                                                </button>
                                            </form>
                                        </li>
                                    </ul>
                                </div>
                        </div>
                        </c:if>
                    </div>
                    <div class="divider-h"></div>
                    <div class="card-content-container-detail">
                        <c:out value="${review.content}" />
                    </div>
                    <div class="divider-h"></div>
                    <div class="review-card-feedback-footer-big">
                        <c:if test="${review.likeCounter != 0}">
                            <span><c:out value="${review.likeCounter}"/></span>
                        </c:if>
                        <c:url value="/review/feedback/${review.id}" var="updateFeedback" />
                        <form name="likeFeedbackForm" class="feedback-form" method="post" action="${updateFeedback}">
                                <button name="feedback" class="btn-flat waves-effect waves-light ${(review.feedback == "LIKE")? "white-text":""}" value="LIKE">
                                    <i class="material-icons like-dislike-buttons">thumb_up</i>
                                </button>
                                <input type="hidden" name="url" value="${baseUrl}"/>
                        </form>
                        <form name="dislikeFeedbackForm" class="feedback-form" method="post" action="${updateFeedback}" >
                                <button name="feedback" class="btn-flat waves-effect waves-light ${(review.feedback == "DISLIKE")? "white-text":""}" value="DISLIKE">
                                    <i class="material-icons like-dislike-buttons">thumb_down</i>
                                </button>
                                <input type="hidden" name="url" value="${baseUrl}"/>
                        </form>
                    </div>
                    <div class="divider-h"></div>
                    <div class="row review-game-length">
                        <spring:message code="review.gameLength" />:
                        <c:out value="${reviewExtra.gametime}" />
                        <spring:message code="${reviewExtra.unit.code}" />
                    </div>
                    <div class="divider-h"></div>
                    <div class="row review-tags-panel">
                        <div class="col s12">
                            <spring:message code="review.tags" />
                            <c:if test="${review.platform != null}">
                                <div class="chip chip-color">
                                    <spring:message code="${review.platform.code}" />
                                </div>
                            </c:if>
                            <c:if test="${review.difficulty != null}">
                                <div class="chip chip-color">
                                    <spring:message code="${review.difficulty.code}" />
                                </div>
                            </c:if>
                            <c:if test="${review.completed != null && review.completed}">
                                <div class="chip chip-color">
                                    <spring:message code="reviewForm.completed" />
                                </div>
                            </c:if>
                            <c:if test="${review.replayability != null && review.replayability}">
                                <div class="chip chip-color">
                                    <spring:message code="review.replayable" />
                                </div>
                            </c:if>
                        </div>
                        <div class="col s12 right right-align">
                            <a href="<c:url value="/profile/${review.author.id}" />">
                                <spring:message code="review.by" arguments="@${review.author.username}" />
                            </a>
                            <c:forEach var="genre" items="${review.author.preferences}">
                                <span class="chip-small-inverted"><spring:message code="${genre.name}"/></span>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col s12 l4">
            <c:set var="game" value="${game}" scope="request" />
            <c:set var="gameUrl" value="${gameUrl}" scope="request" />
            <c:import url="/WEB-INF/jsp/games/short-game-details.jsp" />
        </div>
    </div>
</div>
</body>
</html>