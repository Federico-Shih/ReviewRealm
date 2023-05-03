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
    <title><spring:message code="navbar.foryou"/></title>
</head>
<body>
<jsp:include page="../static-components/navbar.jsp">
    <jsp:param name="selected" value="for-you"/>
</jsp:include>
<c:url var="setPreferences" value="/profile/edit"/>
<c:url var="searchUsers" value="/"/> <%--TODO Busqueda de usuarios para following--%>
<c:url value="/for-you" var="sendSearch"/>

<div class="for-you-page">
    <div class="for-you-section">
        <span class="for-you-section-header"><spring:message code="for-you.games.header"/> </span>
        <div class="row">
            <c:if test="${empty recommendedGames}">
                <div class="col s12 center-align">
                    <span> <spring:message code="for-you.games.notfound"/></span>
                    <a href="${setPreferences}"><span class><spring:message code="for-you.doit"/></span></a>
                </div>
            </c:if>
            <c:forEach items="${recommendedGames}" var="game">
                <div class="col s4">
                    <div class="game-card-for-list z-depth-2">
                        <a href="<c:url value="/game/${game.id}"/>">
                            <c:url value="${game.imageUrl}" var="imgUrl" />
                            <img class="game-img" src="${imgUrl}"
                                 alt="<c:out value="${game.name}"/>">
                        </a>
                        <div class="game-card-details">
                            <div class="game-card-details-text">
                                <a class="game-card-title white-text" href="<c:url value="/game/${game.id}"/>">
                                    <span ><c:out value="${game.name}"/></span>
                                </a>
                                <span class="game-card-text"><spring:message code="publishing.date"
                                                                             arguments="${game.publishDate}"/></span>
                                <span class="game-card-text"><spring:message code="developer"
                                                                             arguments="${game.developer}"/></span>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
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
        <div class="review-card-list">
            <c:if test="${empty reviewsFollowing}">
                <div class="s12 col center">
                    <span> <spring:message code="for-you.reviews.notfound"/></span>
                    <a href="${searchUsers}"><span class><spring:message code="for-you.doit"/></span></a>
                </div>
            </c:if>
            <c:forEach var="review" items="${reviewsFollowing}">
                <div class="card review-card">
                    <div class="review-card-header">
                        <div class="review-card-header-start">
                            <a id="review-card-game-title" href="./game/<c:out value="${review.reviewedGame.id}"/>">
                            <span >
                                <c:out value="${review.reviewedGame.name}"/>
                            </span>
                            </a>
                            <div>
                                <c:forEach var="genre" items="${review.reviewedGame.genres}">
                                    <span class="chip-small">
                                        <a href="<c:url value="/game/list?f-gen=${genre.id}"/>" class="white-text">
                                                <spring:message code="${genre.name}"/>
                                        </a>
                                    </span>
                                </c:forEach>
                            </div>
                        </div>
                        <div class="review-card-header-end">
                            <span id="review-card-score"><c:out value="${review.rating}"/></span>
                            <span id="review-card-score-outof">/10</span>
                            <i class="material-icons small">star</i>
                        </div>
                    </div>
                    <div class="divider-h"></div>
                    <div class="review-card-body">
                        <c:url value="/review/${review.id}" var="reviewUrl" />
                        <a href="${reviewUrl}">
                            <span id="review-card-title"><c:out value="${review.title}"/></span>
                        </a>
                        <span id="review-card-content"><c:out value="${review.content}"/></span>
                        <span id="review-card-date"><c:out value="${review.createdFormatted}"/></span>
                    </div>
                    <div class="divider-h"></div>
                    <div class="review-card-footer">
                        <c:url value="/profile/${review.author.id}" var="profileUrl" />
                        <a href="${profileUrl}" id="review-card-bottom-text review-card-author">
                            <spring:message code="review.by" arguments="@${review.author.username}"/>
                        </a>
                            <%--                    TODO: PREFERENCIAS DE USUARIO--%>
                            <%--                    <c:forEach var="genre" items="${review.author.preferences}">--%>
                            <%--                        <span class="chip-small-inverted"><c:out value="${genre.name}"/></span>--%>
                            <%--                    </c:forEach>--%>
                    </div>
                </div>
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
