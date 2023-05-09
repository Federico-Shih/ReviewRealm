<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<c:url value="/review/submit/${game.id}" var="submitEndpoint"/>
<c:url value="/review/submit" var="searchEndpoint"/>
<head>
    <title><spring:message code="review.page.title"/></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review/review-page.css" />">
    <link rel="stylesheet" href="<c:url value="/css/game.css" />">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">

    <!-- Compiled and minified JavaScript -->
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            const elems = document.querySelectorAll('select');
            var instances = M.FormSelect.init(elems, {});
            document.querySelector("#searchInput").addEventListener("keypress", function (e) {
                if (e.keyCode === 13) {
                    document.querySelector("#searchButton").click();
                    e.preventDefault();
                }
            });
            document.querySelector("#submitForm").addEventListener("submit", function (e) {
                var form = document.querySelector("#submitForm");
                console.log(e.submitter.id);
                if (e.submitter.id === "searchButton" || e.submitter.id === "searchInput") {
                    form.action = "${searchEndpoint}";
                    form.method = "get";
                } else if (e.submitter.id === "createButton") {
                    form.action = "${submitEndpoint}";
                } else {
                    form.method = "get";
                    form.action = "${searchEndpoint}";
                    form.querySelector("#gameId").value = e.submitter.id;
                }
            });
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
        <form:form modelAttribute="reviewForm" action="${submitEndpoint}" method="post" id="submitForm">
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
                                <c:if test="${selectedGameId != 0}">
                                    <spring:message code="review.title" arguments="${game.name}"/>
                                </c:if>
                                <c:if test="${selectedGameId == 0}">
                                    <spring:message code="review.new"/>
                                </c:if>
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
                                    style="color: black;"
                            />
                            <form:errors path="reviewTitle" cssClass="error" element="p"/>
                        </div>

                        <div class="input-field">
                            <form:label path="reviewContent"><spring:message code="review.ContentInput"/></form:label>
                            <form:textarea
                                    placeholder='${contentPlaceholder}'
                                    path="reviewContent"
                                    id="review-content"
                                    class="materialize-textarea review-content-input input-general"
                                    style="color: black;"/>
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
                            <div class="col s10">
                                <form:input
                                        path="gameLength"
                                        id="gamelength"
                                        type="number"
                                        class="white-text"
                                        step="0.01"
                                />
                                <form:errors path="gameLength" cssClass="error" element="p" />
                            </div>
                            <div class="col s2">
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
                        <div class="row">
                            <button id="createButton" class="${(selectedGameId==0)? " disabled ":" "}waves-effect waves-light btn submit-btn s2 offset-s10 col" type="submit">
                                <spring:message code="reviewForm.create"/>
                            </button>
                        </div>
                    </div>
            </div>
        </div>
        <div class="col s12 m4">
            <c:if test="${selectedGameId!=0}">
                <c:set var="game" value="${game}" scope="request" />
                <c:set var="gameUrl" value="${gameUrl}" scope="request" />
                <c:import url="/WEB-INF/jsp/games/short-game-details.jsp" />
            </c:if>
                <div class="search-game-list">
                    <input id="searchInput" name="search" class="z-depth-1-half search-field white-text" value="${searchField}" placeholder="<spring:message code="game.list.placeholder.search"/>">
                    <button id="searchButton" class="btn-flat button-color white-text" type="submit" ><i class="material-icons" >search</i></button>
                    <input name="gameId" value="${selectedGameId}" type="hidden"/>
                    <%-- TODO: revisar esto --%>
                    <%--porque seguramente esta rre mal pero no tenia otra forma que se me ocurra--%>
                </div>
            <c:if test="${empty searchedGames && !empty searchField}">
                <span><spring:message code="game.list.notfound"/></span>
            </c:if>
            <c:if test="${!(empty searchedGames)}">
                <div class="search-results-list">
                    <c:forEach items="${searchedGames}" var="gameIterator">
                        <c:url var="gameUrl" value="?reviewRating=${reviewForm.reviewRating}&reviewTitle=${reviewForm.reviewTitle}&reviewContent=${reviewForm.reviewContent}&platform=${reviewForm.platform}&difficulty=${reviewForm.difficulty}&gameLength=${reviewForm.gameLength}&unit=${reviewForm.unit}&gameId=${gameIterator.id}" />
                        <div class="card-background">
                            <a href="${gameUrl}" id="${gameIterator.id}" class="no-a-decoration searchResult">
                                <div class="search-result-container">
                                    <div>
                                        <c:url value="${gameIterator.imageUrl}" var="imgUrl" />
                                        <img src="${imgUrl}" alt="game-image" class="search-result-image"/>
                                    </div>
                                    <div class="search-result-container-text">
                                        <h6><c:out value="${gameIterator.name}"/></h6>
                                        <div>
                                            <c:forEach var="genre" items="${gameIterator.genres}" end="1">
                                                <span class="chip-small"><spring:message code="${genre.name}"/> </span>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                            </a>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
        </div>
        </form:form>
    </div>
</div>
</body>
</html>