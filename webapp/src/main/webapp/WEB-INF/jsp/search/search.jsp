<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../../css/search.css"/>
    <title><spring:message code="search.page" arguments="PAGE.ARGUMENTS" /></title> <!-- Compiled and minified CSS -->
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/search.css" />">
    <link rel="stylesheet" href="<c:url value="/css/profile.css" />"/>
    <!-- Compiled and minified JavaScript -->
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <script src="<c:url value="/js/reviewfeedback.js" />"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/static-components/navbar.jsp"/>
<div class="row">
    <div class="col m10 push-m1 s12 xl8 push-xl2">
        <c:if test="${fn:length(search) == 0}">
            <h4>
                <spring:message code="search.noSearch" />
            </h4>
        </c:if>
        <c:if test="${fn:length(games) == 0 && fn:length(reviews) == 0 && fn:length(users) == 0 && fn:length(search) != 0}">
            <h4>
                <spring:message code="search.noResults" />
            </h4>
        </c:if>
        <c:if test="${fn:length(reviews) != 0}">
            <div class="card card-background">
                <div class="card-content">
                    <div class="search-results-header valign-wrapper">
                        <h5 class="card-title">
                            <spring:message code="search.reviews.title" />
                        </h5>
                        <div class="top-right">
                            <a href="<c:url value="/?search=${search}" /> ">
                                <spring:message code="search.viewmore" />
                            </a>
                        </div>
                    </div>

                    <div class="row">
                        <c:forEach var="review" items="${reviews}">
                            <c:set var="review" value="${review}" scope="request" />
                            <c:import url="/WEB-INF/jsp/review/review-card.jsp" />
                        </c:forEach>
                    </div>
                </div>
            </div>
        </c:if>
        <c:if test="${fn:length(users) != 0}">
            <div class="card card-background">
                <div class="card-content">
                    <div class="search-results-header valign-wrapper">
                        <h5 class="card-title">
                            <spring:message code="search.users.title" />
                        </h5>
                        <div class="top-right">
                            <a href="<c:url value="/community?search=${search}" /> ">
                                <spring:message code="search.viewmore" />
                            </a>
                        </div>
                    </div>
                    <div class="row">
                        <c:forEach var="user" items="${users}">
                            <div class="col s6 margin-bottom-2">
                                <c:set var="user" value="${user}" scope="request"/>
                                <c:import url="/WEB-INF/jsp/profile/profile-card.jsp"/>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </c:if>
        <c:if test="${fn:length(games) != 0}">
            <div class="card card-background">
                <div class="card-content">
                    <div class="search-results-header valign-wrapper">
                        <h5 class="card-title">
                            <spring:message code="search.games.title" />
                        </h5>
                        <div class="top-right">
                            <a href="<c:url value="/game/list?search=${search}" /> ">
                                <spring:message code="search.viewmore" />
                            </a>
                        </div>
                    </div>
                    <div class="row">
                        <c:forEach items="${games}" var="game">
                            <div class="search-result-background col m6 l6 s12">
                                <c:set var="game" value="${game}" scope="request" />
                                <c:set var="gameUrl" value="/game/${game.id}" />

                                <c:import url="/WEB-INF/jsp/games/short-game-details.jsp" />
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </c:if>
    </div>
</div>
</body>
</html>