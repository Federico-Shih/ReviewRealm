<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/game.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review.css" />">
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <title><spring:message code="game.details.title" arguments="${game.name}"/></title>
</head>
<body>
<c:url value="/review/submit?gameId=${game.id}" var="sumbitReview"/>
<jsp:include page="../static-components/navbar.jsp"/>
<div class="game-details-section">
    <div class="game-details">
        <span class="game-title"><c:out value="${game.name}"/></span>
        <div class="game-genres">
            <span class="game-genre"><spring:message code="genres"/></span>
            <c:forEach items="${game.genres}" var="genre">
                <div class="chip white-text"><spring:message code="${genre.name}"/></div>
            </c:forEach>
        </div>
        <div class="divider"></div>
        <p class="game-description"><c:out value="${game.description}"/></p>
        <div class="divider"></div>
    </div>
    <div class="game-card">
        <img class="game-img" src="${game.imageUrl}" alt="Game image">
        <span class="game-card-text">
            <spring:message code="publishing.date" arguments='${game.publishDate}'/>
        </span>
        <span class="game-card-text">
            <spring:message code="developer" arguments='${game.developer}'/>
        </span>
        <span class="game-card-text">
            <spring:message code="editor" arguments='${game.publisher}'/>
        </span>
    </div>
</div>
<div class="game-review-section">
    <div class="game-review-header">
        <span class="game-review-section-header"><spring:message code="game.details.other.reviews"/></span>
        <a class="btn waves-effect-light" href="${sumbitReview}"><spring:message code="game.details.new.review"/></a>
    </div>
    <div class="game-review-card-list row">
        <c:if test="${empty reviews}">
            <div class="no-reviews">
                <div class="s"></div>
                <p id="no-reviews-text"><spring:message code="game.details.first.review"/></p>
            </div>
        </c:if>
        <c:forEach var="review" items="${reviews}">
            <div class="col s12 m6">
                <div class="card">
                    <div class="review-card-header">
                        <div class="review-card-header-start">
                            <span id="review-card-title"><c:out value="${review.title}"/></span>
                        </div>
                        <div class="review-card-header-end">
                            <span id="review-card-score"><c:out value="${review.rating}"/></span>
                            <span id="review-card-score-outof">/10</span>
                            <i class="material-icons small">star</i>
                        </div>
                    </div>
                    <div class="review-card-body">
                        <span id="review-card-content"><c:out value="${review.content}"/></span>
                        <span id="review-card-date"><c:out value="${review.createdFormatted}"/></span>
                    </div>
                    <div class="divider"></div>
                    <div class="review-card-footer">
                    <span id="review-card-bottom-text"> por <span id="review-card-author">@<c:out
                            value="${review.author.username}"/></span><%--, quien prefiere: --%> </span>
                            <%--                            <c:forEach var="genre" items="${review.author.}">--%>
                            <%--                                <span class="chip-small-inverted"><c:out value="${genre.name}"/></span>--%>
                            <%--                            </c:forEach>--%>
                    </div>
                </div>
            </div>

        </c:forEach>
        <c:if test="${fn:length(reviews) % 2 == 1}">
            <div class="col m6"></div>
        </c:if>
    </div>
</div>
</body>
</html>
