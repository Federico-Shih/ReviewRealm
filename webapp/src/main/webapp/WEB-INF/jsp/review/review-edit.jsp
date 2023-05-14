<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
    <title><spring:message code="review.edit.page" arguments="PAGE.ARGUMENTS" /></title> <!-- Compiled and minified CSS -->
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review/review-page.css" />">
    <link rel="stylesheet" href="<c:url value="/css/game.css" />">
    <!-- Compiled and minified JavaScript -->
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            <c:if test="${reviewForm.platform != null}">
            document.querySelector("#platform").value = "${reviewForm.platform}";
            </c:if>
            <c:if test="${reviewForm.completed}">
            document.querySelector("#completed").setAttribute("checked", "checked");
            </c:if>
            <c:if test="${reviewForm.replayability}">
            document.querySelector("#replayable").setAttribute("checked", "checked");
            </c:if>

            const elems = document.querySelectorAll('select');
            var instances = M.FormSelect.init(elems, {});
        });
    </script>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
</head>
<!-- general variables
<%--<spring:message code="reviewForm.title.placeholder" var="titlePlaceholder"/>--%>
<%--<spring:message code="reviewForm.content.placeholder" var="contentPlaceholder"/>--%>
<%--<spring:message code="review.author.generic" var="authorPlaceholder"/>--%>
<%--<c:url value="/game/${game.id}" var="gameUrl" />--%>
-->
<body>
<jsp:include page="/WEB-INF/jsp/static-components/navbar.jsp"/>
<spring:message code="reviewForm.title.placeholder" var="titlePlaceholder"/>
<spring:message code="reviewForm.content.placeholder" var="contentPlaceholder"/>
<spring:message code="review.author.generic" var="authorPlaceholder"/>

<c:url value="/game/${game.id}" var="gameUrl" />
<c:url value="/review/${id}/edit" var="reviewEditEndpoint" />
<body>
<div class="container">
    <div class="row">
        <form:form modelAttribute="reviewForm" action="${reviewEditEndpoint}" method="post" id="submitForm">
            <div class="col s12 m8">
                <div class="card card-background">
                    <div class="rating-input inline valign-wrapper">
                        <form:input
                                path="reviewRating"
                                id="review-rating"
                                type="number"
                                cssClass="white-text number-input"
                                placeholder="10"
                        />
                        <div class="total-rating number-input">/10</div>
                    </div>
                    <div class="card-content card-content-container">
                        <div class="card-title review-card-title row valign-wrapper">
                            <div class="col s12 flow-text">
                                <spring:message code="review.title" arguments="${game.name}"/>
                            </div>
                        </div>
                        <form:errors path="reviewRating" cssClass="error" element="p"/>
                        <div class="divider"></div>
                        <div class="input-field">
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

                        <div class="input-field">
                            <form:label path="reviewContent"><spring:message code="review.ContentInput"/></form:label>
                            <form:textarea
                                    placeholder='${contentPlaceholder}'
                                    path="reviewContent"
                                    id="review-content"
                                    class="materialize-textarea review-content-input input-general"/>
                            <form:errors path="reviewContent" cssClass="error" element="p"/>
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
                                <span> <spring:message code="reviewForm.completed" /></span>
                            </label>
                        </div>
                        <div>
                            <label for="replayable">
                                <input type="checkbox" name="replayability" id="replayable"/>
                                <span><spring:message code="reviewForm.replayability" /></span>
                            </label>
                        </div>
                        <div class="f-row f-jc-end">
                            <button id="save-button" class="waves-effect waves-light btn submit-btn" type="submit">
                                <spring:message code="edit.form.save"/>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col s12 m4">
                <c:if test="${game.id != 0}">
                    <c:set var="game" value="${game}" scope="request" />
                    <c:set var="gameUrl" value="${gameUrl}" scope="request" />
                    <c:import url="/WEB-INF/jsp/games/short-game-details.jsp" />
                </c:if>
            </div>
        </form:form>
    </div>
</div>
</body>
</html>