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
    <!-- Compiled and minified JavaScript -->
    <script src="<c:url value="/js/materialize.min.js" />"></script>
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
        <div class="col s12 m8">
            <div class="card card-background">
                <div class="card-content">
                    <div class="card-title row">
                        <div class="col s10">
                            <c:out value="${review.title}" />
                        </div>
                        <div class="col s2" style="float: right;">
                            <span style="font-weight: bold"><c:out value="${review.rating}" /></span>/10
                        </div>
                    </div>
                    <div class="divider"></div>
                    <div class="card-content-container-detail">
                        <c:out value="${review.content}" />
                    </div>
                    <div class="row" style="padding: 5px">
                        <spring:message code="review.gameLength" />:
                        <c:out value="${reviewExtra.gametime}" />
                        <spring:message code="${reviewExtra.unit.code}" />
                    </div>
                    <div class="divider"></div>
                    <div class="row" style="margin-top: 5px">
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
                            <spring:message code="review.by" arguments="${review.author.email}" />
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col s12 m4">
            <c:set var="game" value="${game}" scope="request" />
            <c:set var="gameUrl" value="${gameUrl}" scope="request" />
            <c:import url="/WEB-INF/jsp/games/short-game-details.jsp" />
        </div>
    </div>
</div>
</body>
</html>