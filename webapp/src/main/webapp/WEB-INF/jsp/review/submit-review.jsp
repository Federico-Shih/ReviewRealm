<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<c:url value="/review/submit/${game.id}" var="submitEndpoint"/>
<c:url value="/review/${id}/edit" var="reviewEditEndpoint" />
<head>
    <title><spring:message code="review.page.title"/></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review/review-page.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review.css" />">
    <link rel="stylesheet" href="<c:url value="/css/game.css" />">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">

    <!-- Compiled and minified JavaScript -->
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            var elems = document.querySelectorAll('.collapsible');
            var instances = M.Collapsible.init(elems, {accordion: false});

            let newLength = document.getElementById('review-content').value.length;
            document.getElementById('review-content-length').innerHTML = newLength;
            <c:if test="${reviewForm.platform != null}">
            document.querySelector("#platform").value = "${reviewForm.platform}";
            </c:if>
            <c:if test="${reviewForm.completed}">
            document.querySelector("#completed").setAttribute("checked", "checked");
            </c:if>
            <c:if test="${reviewForm.replayability}">
            document.querySelector("#replayable").setAttribute("checked", "checked");
            </c:if>
            <c:if test="${reviewForm.unit != null}">
            document.querySelector("#unit").value = "${reviewForm.unit}";
            </c:if>
            document.getElementById('review-content').onkeyup = function (){
                newLength = document.getElementById('review-content').value.length;
                document.getElementById('review-content-length').innerHTML = newLength;
            };
        });
        document.addEventListener('DOMContentLoaded', function () {
            var elems = document.querySelectorAll('.modal');
            var instances = M.Modal.init(elems);
        });

    </script>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
</head>

<spring:message code="reviewForm.title.placeholder" var="titlePlaceholder"/>
<spring:message code="reviewForm.content.placeholder" var="contentPlaceholder"/>
<spring:message code="review.author.generic" var="authorPlaceholder"/>

<c:url value="/game/${game.id}" var="gameUrl" />

<body>
<jsp:include page="/WEB-INF/jsp/static-components/navbar.jsp"/>
<div class="container">
    <div class="row">
        <form:form modelAttribute="reviewForm" action="${edit? reviewEditEndpoint : submitEndpoint}" method="post" id="submitForm">
        <div class="col s12 m8">
            <div class="card card-background">
                    <div class="card-content card-content-container">
                        <div class="card-title review-card-title row valign-wrapper">
                            <div class="col s12 flow-text">
                                <spring:message code="review.title" arguments="${game.name}"/>
                            </div>
                        </div>
                        <div class="divider"></div>
                        <div class="f-row f-gap-2">
                            <div class="input-field title-input">
                                <form:label path="reviewTitle"><spring:message code="review.titleInput"/></form:label>
                                <form:input
                                        path="reviewTitle"
                                        id="review-title"
                                        placeholder='${titlePlaceholder}'
                                        type="text"
                                        class="input-general"
                                />
                                <form:errors path="reviewTitle" cssClass="error" element="p"/>
                            </div>
                            <div class="input-field rating-input inline valign-wrapper">
                                <form:label path="reviewRating"><spring:message code="review.ratingInput"/></form:label>
                                <form:input
                                        path="reviewRating"
                                        id="review-rating"
                                        type="number"
                                        cssClass="white-text number-input"
                                        placeholder="10"
                                />
                                <div class="total-rating number-input">/10</div>
                                <form:errors path="reviewRating" cssClass="error" element="p"/>
                            </div>
                        </div>


                        <div class="input-field">
                            <form:label path="reviewContent"><spring:message code="review.ContentInput"/></form:label>
                            <form:textarea
                                    placeholder='${contentPlaceholder}'
                                    path="reviewContent"
                                    id="review-content"
                                    class="materialize-textarea review-content-input input-general"
                            />
                            <form:errors path="reviewContent" cssClass="error" element="p"/>
                            <div class="right-align">
                                <span id="review-content-length">0</span>
                                <span><spring:message code="review.characters"/></span>
                            </div>
                        </div>
                        <div>
                            <form:label path="platform"><spring:message code="review.platform" /></form:label>
                            <form:select name="platform" path="platform" id="platform" cssClass="browser-default">
                                <option value="">
                                    <spring:message code="reviewForm.platform.placeholder" />
                                </option>
                                <c:forEach items="${platforms}" var="platform">
                                    <option value="${platform}">
                                        <spring:message code="${platform.code}" />
                                    </option>
                                </c:forEach>
                            </form:select>
                            <form:errors path="platform" cssClass="error" element="p" />
                        </div>
                        <div>
                            <form:label path="difficulty"><spring:message code="review.difficulty" /></form:label>
                            <form:select name="difficulty" path="difficulty" id="difficulty" cssClass="browser-default">
                                <option value="">
                                    <spring:message code="reviewForm.difficulty.placeholder" />
                                </option>
                                <c:forEach items="${difficulties}" var="platform">
                                    <form:option value="${platform}">
                                        <spring:message code="${platform.code}" />
                                    </form:option>
                                </c:forEach>
                            </form:select>
                            <form:errors path="difficulty" cssClass="error" element="p" />
                        </div>
                        <div class="row">
                            <div class="col s12">
                                <form:label for="gamelength" path="gameLength"><spring:message code="review.gameLength" /></form:label>
                            </div>
                            <div class="col s8">
                                <form:input
                                        path="gameLength"
                                        id="gamelength"
                                        type="number"
                                        class="white-text"
                                        step="0.01"
                                />
                                <form:errors path="gameLength" cssClass="error" element="p" />
                            </div>
                            <div class="col s4">
                                <form:select name="unit" path="unit" id="unit" cssClass="browser-default">
                                    <c:forEach items="${units}" var="unit">
                                        <option value="${unit}">
                                            <spring:message code="${unit.code}" />
                                        </option>
                                    </c:forEach>
                                </form:select>
                            </div>
                        </div>
                        <div>
                            <label for="completed">
                                <input type="checkbox" name="completed" id="completed" />
                                <span> <spring:message code="reviewForm.completed"/></span>
                            </label>
                        </div>
                        <div>
                            <label for="replayable">
                                <input type="checkbox" name="replayability" id="replayable"/>
                                <span><spring:message code="reviewForm.replayability"/></span>
                            </label>
                        </div>
                        <div class="f-row f-jc-end f-gap-2">
                            <c:if test="${edit}">
                                <a href="<c:url value="/review/${id}" />"
                                   class="waves-effect btn-flat cancel-button white-text">
                                    <spring:message code="cancel.button"/>
                                </a>
                            </c:if>
                            <button class="${(selectedGameId==0)? " disabled ":" "}waves-effect btn modal-trigger"
                                    data-target="submit-confirmation-modal" type="button">
                                <c:if test="${edit}">
                                    <spring:message code="edit.form.save"/>
                                </c:if>
                                <c:if test="${!edit}">
                                    <spring:message code="reviewForm.create"/>
                                </c:if>
                            </button>
                            <div id="submit-confirmation-modal" class="modal">
                                <div class="modal-content">
                                    <h5><spring:message code="review.submit.confirmation"/></h5>
                                </div>
                                <div class="modal-footer f-row f-jc-end f-gap-2">
                                    <a href="#" class="modal-close waves-effect btn-flat white-text"><spring:message code="cancel.button"/></a>
                                    <div>
                                        <button class="waves-effect btn submit-btn" type="submit">
                                            <c:if test="${edit}">
                                                <spring:message code="edit.form.save"/>
                                            </c:if>
                                            <c:if test="${!edit}">
                                                <spring:message code="reviewForm.create"/>
                                            </c:if>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
            </div>
        </div>
        </form:form>
        <div class="col s12 m4 ">
            <c:set var="game" value="${game}" scope="request" />
            <c:set var="gameUrl" value="${gameUrl}" scope="request" />
            <c:import url="/WEB-INF/jsp/games/short-game-details.jsp" />
            <ul class="collapsible" id="guidelines-collapsible">
                <li>
                    <div class="collapsible-header filters-header center">
                        <i class="material-icons left">gavel</i>
                        <span class="review-filters-panel-subtitle"><spring:message code="community.rules.title"/></span>
                        <i class="material-icons right dropdown-arrow">arrow_drop_down</i>
                    </div>
                    <ol class="collapsible-body">
                        <li><span class="community-guideline"><spring:message code="community.rules.respect"/></span></li>
                        <li><span class="community-guideline"><spring:message code="community.rules.spam"/></span></li>
                        <li><span class="community-guideline"><spring:message code="community.rules.relevancy"/></span></li>
                        <li><span class="community-guideline"><spring:message code="community.rules.spoilers"/></span></li>
                        <li><span class="community-guideline"><spring:message code="community.rules.piracy"/></span></li>
                        <li><span class="community-guideline"><spring:message code="community.rules.privacy"/></span></li>
                    </ol>
                </li>
            </ul>
        </div>
    </div>
</div>
</body>
</html>