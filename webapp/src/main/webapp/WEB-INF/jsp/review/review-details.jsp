<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title><spring:message code="review.title" arguments="${game.name}"/></title> <!-- Compiled and minified CSS -->
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
        document.addEventListener('DOMContentLoaded', function() {
            var elems = document.querySelectorAll('.modal');
            var instances = M.Modal.init(elems);
        });
        <c:if test="${created}">
            document.addEventListener('DOMContentLoaded', function () {
                M.toast({html: '<spring:message code="review.created" />', classes: 'created-toast'});
            });
        </c:if>
        <c:if test="${reported}">
            document.addEventListener('DOMContentLoaded', function () {
                M.toast({html: '<spring:message code="review.report.submitted" />', classes: 'created-toast'});
            });
        </c:if>
        var elemsTooltip = document.querySelectorAll('.tooltipped');
        var instancesTooltip = M.Tooltip.init(elemsTooltip, {});
        document.addEventListener('DOMContentLoaded', function() {
            var button = document.querySelector('#submit-report-button');
            var elems = document.querySelectorAll('.reason-radio');
            elems.forEach(function (elem) {
                elem.addEventListener('click', function () {
                    button.classList.remove('disabled');
                });
            });
        });
    </script>
    <script src="<c:url value="/js/reviewfeedback.js" />"></script>

</head>

<spring:message code="reviewForm.title.placeholder" var="titlePlaceholder"/>
<spring:message code="reviewForm.content.placeholder" var="contentPlaceholder"/>
<spring:message code="review.author.generic" var="authorPlaceholder"/>
<c:url value="/game/${game.id}" var="gameUrl"/>
<c:url value="/" var="baseUrl"/>
<body>
<jsp:include page="/WEB-INF/jsp/static-components/navbar.jsp"/>
<div class="row margin-2">
    <c:url value="/" var="reviewList"/>
    <div class="col s12">
        <a href="${reviewList}" class="breadcrumb"><spring:message code="review.search"/></a>
        <a href="#" class="breadcrumb"><c:out value="${game.name}"/> </a>
    </div>
</div>
<div class="container">
    <div class="row">
        <div class="col s12 l8">
            <div class="card card-background">
                <div class="card-content">
                    <div class="card-title row">
                        <div class="col s6">
                            <c:out value="${review.title}"/>
                        </div>
                        <div class="col s6 right-align f-row f-jc-end">
                            <div class="f-row f-ai-baseline">
                                <span id="review-card-score"><c:out value="${review.rating}" /></span>/10
                            </div>
                            <div>
                                <c:if test="${isOwner}">
                                    <c:url value="/review/${review.id}/edit" var="reviewEditUrl"/>
                                    <form action="${reviewEditUrl}" method="get" >
                                        <button data-position="bottom" data-tooltip="<spring:message code="review.edit" />" class="tooltipped waves-effect btn-flat valign-wrapper highlight light-gray-text" type="submit">
                                            <i class="material-icons">edit</i>
                                        </button>
                                    </form>
                                </c:if>
                            </div>
                            <div>
                                <c:if test="${isModerated}">
                                    <c:url value="/review/delete/${review.id}" var="moderateUrl" />
                                    <button data-position="bottom" data-tooltip="<spring:message code="review.delete" />" class="tooltipped waves-effect btn-flat valign-wrapper highlight light-gray-text modal-trigger" data-target="delete-confirmation-modal">
                                        <i class="material-icons">delete</i>
                                    </button>
                                </c:if>
                            </div>
                            <div>
                                <c:if test="${not isOwner }">
                                    <c:url value="/report/review/${review.id}" var="reportSubmitUrl"/>
                                    <button data-position="bottom" data-tooltip="<spring:message code="review.report" />" class="tooltipped waves-effect btn-flat valign-wrapper highlight light-gray-text modal-trigger ${isReported? 'disabled':''} " data-target="report-form-modal">
                                        <i class="material-icons ${isReported? 'blue-grey-text':'red-text'}">flag</i>
                                    </button>
                                </c:if>
                            </div>
                        </div>
                    </div>
                    <div class="divider-h"></div>
                    <div class="card-content-container-detail">
                        <c:out value="${review.content}"/>
                    </div>
                    <c:if test="${review.gameLength != null}">
                        <div class="divider-h"></div>
                        <div class="f-row review-game-length f-jc-sbetween">
                            <div>
                                <spring:message code="review.gameLength"/>:
                                <fmt:formatNumber value="${review.gameLengthInUnits.value}" type="number" maxFractionDigits="2" minFractionDigits="2" var="gameLengthFormatted"/>
                                <c:out value="${gameLengthFormatted}"/>
                                <spring:message code="${review.gameLengthInUnits.key.code}"/>
                            </div>
                            <div>
                                <c:out value="${review.createdFormatted}"/>
                            </div>
                        </div>
                    </c:if>
                    <div class="divider-h"></div>
                    <div class="row review-tags-panel">
                        <div class="col s12">
                            <spring:message code="review.tags"/>
                            <c:if test="${review.platform != null}">
                                <a href="<c:url value="/?f-plt=${review.platform.id}"/>">
                                    <span class="chip-small-inverted">
                                        <spring:message code="${review.platform.code}"/>
                                    </span>
                                </a>
                            </c:if>
                            <c:if test="${review.difficulty != null}">
                                <a href="<c:url value="/?f-dif=${review.difficulty.id}"/>">
                                    <span class="chip-small-inverted">
                                        <spring:message code="${review.difficulty.code}"/>
                                    </span>
                                </a>
                            </c:if>
                            <c:if test="${review.completed != null && review.completed}">
                                <a href="<c:url value="/?f-cpt=on"/>">
                                    <span class="chip-small-inverted">
                                        <spring:message code="reviewForm.completed"/>
                                    </span>
                                </a>
                            </c:if>
                            <c:if test="${review.replayability != null && review.replayability}">
                                <a href="<c:url value="/?f-rpl=on"/>">
                                    <span class="chip-small-inverted">
                                        <spring:message code="review.replayable"/>
                                    </span>
                                </a>
                            </c:if>
                        </div>
                        <div class="col s12 right right-align">
                            <a href="<c:url value="/profile/${review.author.id}" />">
                                <spring:message code="review.by" arguments="@${review.author.username}"/>
                            </a>
                        </div>
                        <div class="col s12 right right-align">
                            <c:if test="${!(empty review.author.preferences)}">
                                <span id="review-card-preferences">
                                    <spring:message code="review.author.preferences"/>
                                </span>
                            </c:if>
                            <c:forEach var="genre" items="${review.author.preferences}">
                                <span class="chip-small-inverted">
                                    <a href="<c:url value="/game/list?f-gen=${genre.id}"/>" class="rr-blue-text">
                                        <spring:message code="${genre.name}"/>
                                    </a>
                                </span>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>
            <div class="card card-background">
                <div class="review-card-feedback-footer-big">
                    <div class="f-row f-ai-baseline">
                        <span id="review-popularity-tag" class="no-wrap"><spring:message code="review.popularity"/>:</span>
                        <span id="review-popularity-value" class="no-wrap"><c:out value="${review.likeCounter}"/></span>
                    </div>
                    <div class="f-row f-jc-end f-ai-baseline">
                        <span><spring:message code="review.didyoulikereview"/></span>
                        <c:url value="/review/feedback/${review.id}" var="updateFeedback"/>
                        <form name="likeFeedbackForm" class="feedback-form" method="post" action="${updateFeedback}">
                            <button name="feedback"
                                    class="btn-flat waves-effect waves-light ${(loggedUser == null || isOwner)? "dark-disabled": ((review.feedback == "LIKE")? "white-text":"light-gray-text")}"
                                    value="LIKE">
                                <i class="material-icons like-dislike-buttons">thumb_up</i>
                            </button>
                            <input type="hidden" name="url" value="${baseUrl}"/>
                        </form>
                        <form name="dislikeFeedbackForm" class="feedback-form" method="post" action="${updateFeedback}">
                            <button name="feedback"
                                    class="btn-flat waves-effect waves-light ${(loggedUser == null || isOwner)? "dark-disabled": ((review.feedback == "DISLIKE")? "white-text":"light-gray-text")}"
                                    value="DISLIKE">
                                <i class="material-icons like-dislike-buttons">thumb_down</i>
                            </button>
                            <input type="hidden" name="url" value="${baseUrl}"/>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <div class="col s12 l4">
            <c:set var="game" value="${game}" scope="request"/>
            <c:set var="gameUrl" value="${gameUrl}" scope="request"/>
            <c:import url="/WEB-INF/jsp/games/short-game-details.jsp"/>
        </div>
    </div>
</div>
<div id="delete-confirmation-modal" class="modal">
    <div class="modal-content">
        <h5><spring:message code="review.delete.confirmation"/></h5>
    </div>
    <div class="modal-footer f-row f-jc-end f-gap-2">
        <a href="#" class="modal-close waves-effect btn-flat white-text"><spring:message code="cancel.button"/></a>
        <div>
            <form action="${moderateUrl}" method="post">
                <button class="waves-effect btn" type="submit">
                    <spring:message code="review.delete"/>
                </button>
            </form>
        </div>
    </div>
</div>
<div id="report-form-modal" class="modal">
    <form action="${reportSubmitUrl}" method="post">
        <div class="modal-content">
            <h5><spring:message code="review.report.header"/></h5>
            <c:forEach var="reason" items="${reportValues}">
                <p>
                    <label>
                        <input name="reason" value="${reason}" type="radio" class="with-gap reason-radio" />
                        <span><spring:message code="${reason.code}"/></span>
                    </label>
                </p>
            </c:forEach>
        </div>
        <div class="modal-footer f-row f-jc-end f-gap-2">
            <a href="#" class="modal-close waves-effect btn-flat white-text"><spring:message code="cancel.button"/></a>
            <div>
                <button class="waves-effect btn disabled" id="submit-report-button" type="submit">
                    <spring:message code="review.report"/>
                </button>
            </div>
        </div>
    </form>
</div>
</body>
</html>