<%--
  Created by IntelliJ IDEA.
  User: Fedes
  Date: 4/22/2023
  Time: 1:29 PM
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
<link rel="stylesheet" href="<c:url value="/css/main.css" />">
<link rel="stylesheet" href="<c:url value="/css/review/review-page.css" />">
<!-- Compiled and minified JavaScript -->
<script src="<c:url value="/js/materialize.min.js" />"></script>
<div class="card-background">
    <div class="short-game-container">
        <div>
            <c:url value="${game.imageUrl}" var="imgUrl"/>
            <img src="${imgUrl}" alt="game-image" class="game-image"/>
        </div>
        <div class="short-game-container-text">
            <a href="${gameUrl}">
                <span class="white-text search-result-game-title overflow-ellipsis"><c:out value="${game.name}"/></span>
            </a>
            <div>
                <span><spring:message code="developer" arguments="${game.developer}"/></span>
            </div>
            <div>
                <span><spring:message code="genres"/> </span>
                <c:forEach var="genre" items="${game.genres}">
                <span class="chip-small">
                    <a href="<c:url value="/game/list?f-gen=${genre.id}"/>" class="white-text">
                            <spring:message code="${genre.name}"/>
                    </a>
                </span>
                </c:forEach>
            </div>
        </div>
    </div>
</div>
