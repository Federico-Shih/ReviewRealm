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
    <!-- Compiled and minified JavaScript -->
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <script src="<c:url value="/js/reviewfeedback.js" />"></script>
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
<div class="row">
    <div class="col m10 push-m1 s12 l6 push-l3">
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
                            <a href="<c:url value="/" /> ">
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
                    <h5 class="card-title">
                        <spring:message code="search.users.title" />
                    </h5>
                    <c:forEach items="${users}" var="user">
                        <div>
                            <h6>
                                <a href="<c:url value="/user/${user.id}" />">
                                        ${user.username}
                                </a>
                            </h6>
                        </div>
                    </c:forEach>
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
                    <c:forEach items="${games}" var="game">
                        <c:url var="gameUrl" value="/game/${game.id}" />
                        <div class="search-result-background">
                            <a href="${gameUrl}" id="${game.id}" class="no-a-decoration searchResult">
                                <div class="search-game-container">
                                    <div>
                                        <c:url value="${game.imageUrl}" var="imgUrl" />
                                        <img src="${imgUrl}" alt="game-image" class="search-result-image"/>
                                    </div>
                                    <div class="search-result-container-text">
                                        <h6><c:out value="${game.name}"/></h6>
                                        <div>
                                            <c:forEach var="genre" items="${game.genres}" end="1">
                                                <span class="chip-small"><spring:message code="${genre.name}"/> </span>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                            </a>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:if>
    </div>
</div>
</body>
</html>