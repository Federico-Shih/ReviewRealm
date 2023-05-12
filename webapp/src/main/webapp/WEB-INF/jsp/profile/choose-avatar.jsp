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
  <link rel="stylesheet" href="<c:url value="/css/profile.css" />"/>
  <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
  <script src="<c:url value="/js/materialize.min.js" />"></script>
  <script src="<c:url value="/js/reviewfeedback.js"/> "></script>
  <title><spring:message code="profile.avatar.title"/></title>
</head>

<body>
<jsp:include page="../static-components/navbar.jsp">
  <jsp:param name="selected" value="profile"/>
</jsp:include>
  <div class="container">
    <div class="row">
      <span class="avatar-section-header col s12"><spring:message code="profile.avatar.selection"/></span>
      <c:forEach begin="1" end="6" var="i">
        <div class="col s4 center">
          <form method="post" action="<c:url value='/profile/settings/avatar/${i}'/>">
            <button class="avatar-button"  type="submit">
              <img class="profile-avatar-card" src="<c:url value="/static/avatars/${i}.png"/>" alt="avatarnumber${i}.png"/>
            </button>
          </form>
        </div>
      </c:forEach>
    </div>
  </div>
</body>
</html>
