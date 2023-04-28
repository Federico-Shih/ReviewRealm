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
    <title>Friends</title>
</head>
<body>
    <jsp:include page="../static-components/navbar.jsp"/>
    <div class="container row">
        <h3><spring:message code="${pageName}" arguments="${username}" /></h3>

        <c:forEach items="${users}" var="user">
            <div class="col s4 card">
                <a href="<c:url value="/profile/${user.id}"/> ">
                    <div class="card-content">
                        <div class="card-title">
                            <c:out value="${user.username}" />
                        </div>
                    </div>
                </a>
            </div>
        </c:forEach>
        <c:if test="${usersLength == 0}">
            <h5>
                <spring:message code="profile.following.nousers" />
            </h5>
        </c:if>
    </div>
</body>
</html>
