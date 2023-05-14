<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/for-you.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review.css" />">
    <link rel="stylesheet" href="<c:url value="/css/game.css" />"/>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <script src="<c:url value="/js/reviewfeedback.js"/> "></script>
    <title><spring:message code="navbar.foryou"/></title>
</head>
<body>
<jsp:include page="../static-components/navbar.jsp">
    <jsp:param name="selected" value="for-you"/>
</jsp:include>
<c:url var="setPreferences" value="/profile/settings/preferences"/>
<c:url var="searchUsers" value="/"/>
<c:url value="/for-you" var="sendSearch"/>
<c:url value="/for-you/discovery" var="discovery"/>
<div class="for-you-page">
    <div class="for-you-section">
        <div class="full-width row">
            <c:if test="${!userSetPreferences}">
                <div class="card lime darken-3">
                    <div class="card-content white-text f-row f-gap-2">
                        <div class="">
                            <i class="material-icons medium">warning</i>
                        </div>
                        <div class="">
                            <span class="card-title"><spring:message code="for-you.nopreferencesset" /></span>
                            <a href="<c:url value="/profile/settings/preferences"/>" class="no-a-decoration btn-flat waves-effect waves-light border-button f-row f-jc-center f-ai-center">
                                <span><spring:message code="for-you.nopreferencesset.doit"/></span>
                            </a>
                        </div>
                    </div>
                </div>
            </c:if>
            <c:if test="${userSetPreferences}">
                <div class="card blue-grey darken-4">
                    <div class="card-content white-text f-row f-gap-2">
                        <div class="">
                            <i class="material-icons medium">videogame_asset</i>
                        </div>
                        <div class="">
                            <span class="card-title"><spring:message code="for-you.discovery" /></span>
                            <a href="<c:url value="/for-you/discovery"/>" class="no-a-decoration btn-flat waves-effect waves-light border-button f-row f-jc-center f-ai-center">
                                <span><spring:message code="for-you.discovery.btn"/></span>
                            </a>
                        </div>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
    <div class="for-you-section">
        <span class="for-you-section-header"><spring:message code="for-you.search.users"/></span>
        <form action="${sendSearch}" class="entire-width">
            <div class="search-section">
                <div class="search-game-list">
                    <input name="search" class="z-depth-1-half search-field" type="search" value="${search}"
                           placeholder="<spring:message code="for-you.search-placeholder"/>">
                    <button class="btn-flat button-color white-text" type="submit"><i class="material-icons">search</i>
                    </button>
                </div>
            </div>
        </form>
        <c:if test="${not empty search}">
            <div class="container row">
                <c:forEach var="user" items="${users}">
                    <div class="col s5 card user-card-small">
                        <a href="<c:url value="/profile/${user.id}"/> ">
                            <div class="card-content">
                                <div class="card-title">
                                    <div class="align-icon-text">
                                        <i class="material-icons">person</i>
                                        <span><c:out value="${user.username}" /></span>
                                    </div>
                                </div>
                            </div>
                        </a>
                    </div>
                </c:forEach>
            </div>
        </c:if>
    </div>
    <div class="for-you-section">
        <span class="for-you-section-header"><spring:message code="for-you.reviews.header"/></span>
        <div class="review-card-list row">
            <c:if test="${empty reviewsFollowing}">
                <div class="s12 col center">
                    <span> <spring:message code="for-you.reviews.notfound"/></span>
                </div>
            </c:if>
            <c:forEach var="review" items="${reviewsFollowing}">
                <c:set var="review" value="${review}" scope="request" />
                <c:import url="/WEB-INF/jsp/review/review-card.jsp" />
            </c:forEach>
        </div>
        <div class="center-align">
            <c:if test="${fn:length(reviewsFollowing) == size}">
                <button class="btn-flat btn-floating-color no-a-decoration"> <a href="?size=${fn:length(reviewsFollowing) + 6}"> <spring:message code="for-you.more"/> </a></button>
            </c:if>
        </div>
    </div>
</div>

</body>
</html>
