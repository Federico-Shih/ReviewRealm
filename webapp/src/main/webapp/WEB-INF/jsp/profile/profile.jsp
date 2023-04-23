<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
  <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
  <link type="text/css" rel="stylesheet" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
  <link rel="stylesheet" href="<c:url value="/css/main.css" />">
  <link rel="stylesheet" href="<c:url value="/css/review.css" />">
  <link rel="stylesheet" href="<c:url value="/css/game.css" />"/>
  <script src="<c:url value="/js/materialize.min.js" />"></script>
  <title>Profile</title>
</head>

<body>
  <jsp:include page="../static-components/navbar.jsp"/>
  <div>
    <div class="row">
      <div class="col s4">
        <h5>${profile.username}</h5>
      </div>
      <div class="col s6">
        <h5><spring:message code="profile.fanof"/></h5>
        <div>
          <c:forEach var="genre" items="${profile.preferences}">
            <span class="chip-small"><spring:message code="${genre.name}"/></span>
          </c:forEach>
        </div>
      </div>
    </div>
    <div class="divider"></div>
    <div>
      <h5><spring:message code="profile.favgames"/></h5>
      <div class="row"><!--debería ser algo como items="$ {profile.favoritegames}"-->
        <c:forEach var="game" items="${games}">
          <a href="./<c:out value="${game.id}"/>">
            <div class="col s12 m4">
              <div class="game-card-for-list medium z-depth-4">
                <img class="game-img" src="<c:out value="${game.imageUrl}"/>"
                     alt="<c:out value="${game.name}"/>">
                <div class="game-card-title"><c:out value="${game.name}"/></div>
                <div class="game-genres">
                  <c:forEach items="${game.genres}" var="genre">
                    <span class="chip-small"><spring:message code="${genre.name}"/></span>
                  </c:forEach>
                </div>
              </div>
            </div>
          </a>
        </c:forEach>
      </div>
    </div>
    <div>
      <div class="divider-h"> </div>
    </div>
    <div>
      <h5><spring:message code="profile.reviews" arguments="${profile.username}"/></h5>
      <c:forEach var="review" items="${reviews}">
        <div>
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
                    <span class="chip-small"><spring:message code="${genre.name}"/></span>
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
              <span id="review-card-title"><c:out value="${review.title}"/></span>
              <span id="review-card-content"><c:out value="${review.content}"/></span>
              <span id="review-card-date"><c:out value="${review.createdFormatted}"/></span>
            </div>
            <div class="divider-h"></div>
            <div class="review-card-footer"> <!-- Hay que escapar el nombre de usuario? -->
                    <span id="review-card-bottom-text"> <spring:message code="review.by" arguments='@${review.author.username}'/>
                    </span>
                <%--                    TODO: PREFERENCIAS DE USUARIO--%>
                <%--                    <c:forEach var="genre" items="${review.author.preferences}">--%>
                <%--                        <span class="chip-small-inverted"><c:out value="${genre.name}"/></span>--%>
                <%--                    </c:forEach>--%>
            </div>
          </div>
        </div>
      </c:forEach>
    </div>
  </div>
</body>
</html>
