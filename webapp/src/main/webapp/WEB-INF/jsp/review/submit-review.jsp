<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
    <title>Crear reseña</title> <!-- Compiled and minified CSS -->
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review/review-page.css" />">
    <!-- Compiled and minified JavaScript -->
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            const elems = document.querySelectorAll('select');
            var instances = M.FormSelect.init(elems, {});
        });
    </script>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
</head>

<spring:message code="reviewForm.title.placeholder" var="titlePlaceholder"/>
<spring:message code="reviewForm.content.placeholder" var="contentPlaceholder"/>
<spring:message code="review.author.generic" var="authorPlaceholder"/>
<c:url value="/review/submit/${game.id}" var="submitEndpoint"/>
<c:url value="/game/${game.id}" var="gameUrl" />

<body>
<jsp:include page="/WEB-INF/jsp/static-components/navbar.jsp"/>
<div class="container">
    <div class="row">
        <div class="col s12 m8">
            <div class="card card-background">
                <form:form modelAttribute="reviewForm" action="${submitEndpoint}" method="post">
                    <div class="rating-input valign-wrapper">
                        <div class="number-input">
                            <form:input
                                    path="reviewRating"
                                    id="review-rating"
                                    type="number"
                                    class="white-text"
                                    placeholder="10"
                            />
                        </div>
                        <div>/10</div>
                    </div>
                    <div class="card-content card-content-container">
                        <div class="card-title review-card-title row valign-wrapper">
                            <div class="col s12 flow-text">
                                <spring:message code="review.title" arguments="${game.name}"/>
                            </div>
                        </div>
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
                        </div>
                        <div class="input-field">
                            <form:label path="reviewContent"><spring:message code="review.ContentInput"/></form:label>
                            <form:textarea
                                    placeholder='${contentPlaceholder}'
                                    path="reviewContent"
                                    id="review-content"
                                    class="materialize-textarea review-content-input input-general"
                                    style="color: black;"/>
                        </div>
                        <div class="input-field">
                            <form:label path="reviewAuthor"><spring:message code="review.author"/></form:label>
                            <form:input id="review-author" path="reviewAuthor" placeholder='${authorPlaceholder}' type="text"
                                   class="input-general" style="color: black;"/>
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
                            <div class="col s12">
                                <form:errors path="reviewTitle" cssClass="error" element="p"/>
                                <form:errors path="reviewContent" cssClass="error" element="p"/>
                                <form:errors path="reviewAuthor" cssClass="error" element="p"/>
                                <form:errors path="reviewRating" cssClass="error" element="p"/>
                                <form:errors path="difficulty" cssClass="error" element="p" />
                                <form:errors path="platform" cssClass="error" element="p" />
                                <form:errors path="gameLength" cssClass="error" element="p" />
                            </div>
                        </div>
                        <div class="row">
                            <button class="waves-effect waves-light btn submit-btn s2 offset-s10 col" type="submit">
                                <spring:message code="reviewForm.create"/>
                            </button>
                        </div>
                    </div>
                </form:form>
            </div>
        </div>
        <div class="col s12 m4">
            <div class="card card-background">
                <div class="card-content">
                    <div>
                        <img src="${game.imageUrl}" alt="game-image" class="game-image"/>
                    </div>
                    <a href="${gameUrl}">
                        <h5><c:out value="${game.name}"/></h5>
                    </a>
                    <div>
                        <span><spring:message code="genres"/> </span>
                        <c:forEach var="genre" items="${game.genres}">
                            <span class="chip-small"><spring:message code="${genre.name}"/> </span>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>