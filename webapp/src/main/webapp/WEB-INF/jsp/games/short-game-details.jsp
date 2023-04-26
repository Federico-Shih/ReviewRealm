<%--
  Created by IntelliJ IDEA.
  User: Fedes
  Date: 4/22/2023
  Time: 1:29 PM
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ page contentType="text/html;charset=UTF-8" %>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
<link rel="stylesheet" href="<c:url value="/css/main.css" />">
<link rel="stylesheet" href="<c:url value="/css/review/review-page.css" />">
<!-- Compiled and minified JavaScript -->
<script src="<c:url value="/js/materialize.min.js" />"></script>
<div class="card card-background">
  <div class="card-content">
    <div>
      <img src="${game.imageUrl}" alt="game-image" class="game-image"/>
    </div>
    <a href="${gameUrl}">
      <h5><c:out value="${game.name}"/></h5>
    </a>
    <div>
      <span><spring:message code="developer" arguments="${game.developer}" /></span>
    </div>
    <div>
      <span><spring:message code="genres"/> </span>
      <c:forEach var="genre" items="${game.genres}">
        <span class="chip-small"><spring:message code="${genre.name}"/> </span>
      </c:forEach>
    </div>
  </div>
</div>