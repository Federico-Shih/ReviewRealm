<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/game.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review.css" />">
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <script src="<c:url value="/js/reviewfeedback.js"/>"></script>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <title><spring:message code="profile.favgames.title"/></title>
</head>
<c:url value="/profile/settings/favgames" var="applyChanges"/>

<body>
<jsp:include page="../static-components/navbar.jsp"/>
    <div class="container">
        <form:form modelAttribute="favgamesForm" action="${applyChanges}" method="post">
            <div class="row">
                <c:if test="${nothingToShow}">
                    <div class="f-row f-jc-center">
                        <a href="<c:url value="/review/submit"/>">
                            <h2 class="white-text center"><spring:message code="profile.favgames.empty"/></h2>
                        </a>
                    </div>
                </c:if>
                <c:if test="${!(nothingToShow)}">
                    <div class="f-row f-jc-center">
                        <h2 class="white-text"><spring:message code="profile.favgames.selection"/></h2>
                    </div>
                    <div class="f-row f-jc-center">
                        <h5 class="white-text t-a-center"><spring:message code="profile.favgames.selection.consideration"/></h5>
                    </div>
                </c:if>
                <div class="f-row f-jc-center">
                    <form:errors path="gameIds" cssClass="error"/>
                </div>
                <c:forEach var="game" items="${favgamescandidates}">
                    <div class="col s4 m4 l4">
                        <div class="game-card-for-list">
                            <c:url value="${game.imageUrl}" var="imgUrl" />
                            <img class="game-img" src="${imgUrl}"
                                 alt="<c:out value="${game.name}"/>">

                            <div class="game-card-details">
                                <div class="game-card-whole-details-text">
                                    <div class="game-card-title white-text">
                                        <span><c:out value="${game.name}"/></span>
                                    </div>
                                    <span class="game-card-text"><spring:message code="publishing.date"
                                                                                 arguments="${game.publishDate}"/></span>
                                    <span class="game-card-text"><spring:message code="developer"
                                                                                 arguments="${game.developer}"/></span>
                                    <label>
                                        <input type="checkbox" value="${game.id}" id="${game.id}" name="gameIds" class="filled-in">
                                        <span class="height-fit-content"><spring:message code="favgames.choose"/></span>
                                    </label>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
                <c:forEach var="game" items="${favgames}">
                    <div class="col s4 m4 l4">
                        <div class="game-card-for-list">
                            <c:url value="${game.imageUrl}" var="imgUrl" />
                            <img class="game-img" src="${imgUrl}"
                                 alt="<c:out value="${game.name}"/>">

                            <div class="game-card-details">
                                <div class="game-card-whole-details-text">
                                    <div class="game-card-title white-text">
                                        <span><c:out value="${game.name}"/></span>
                                    </div>
                                    <span class="game-card-text"><spring:message code="publishing.date"
                                                                                 arguments="${game.publishDate}"/></span>
                                    <span class="game-card-text"><spring:message code="developer"
                                                                                 arguments="${game.developer}"/></span>

                                    <label>
                                        <input type="checkbox" value="${game.id}" id="${game.id}" name="gameIds" checked class="filled-in">
                                        <span class="height-fit-content"><spring:message code="favgames.choose.already"/></span>
                                    </label>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <c:if test="${!nothingToShow}">
                <div class="f-row f-jc-center">
                    <button class="btn-large" type="submit"><spring:message code="favgames.button"/></button>
                </div>
            </c:if>
        </form:form>
        <c:if test="${nothingToShow}">
            <div class="f-row f-jc-center">
                <a href="<c:url value="/review/submit"/>">
                    <button class="btn-large"><spring:message code="reviewForm.create"/></button>
                </a>
            </div>
        </c:if>
    </div>
</body>
</html>
