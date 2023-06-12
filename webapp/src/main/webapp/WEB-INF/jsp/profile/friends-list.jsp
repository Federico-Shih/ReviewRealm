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
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <title><spring:message code="${pageName}" arguments="${username}" /></title>
</head>
<body>
    <jsp:include page="../static-components/navbar.jsp"/>
    <div class="row margin-2">
        <div class="col s12 valign-wrapper breadcrumb-align">
            <a href="<c:url value="."/>" class="breadcrumb"><c:out value="${username}"/></a>
            <a href="#" class="breadcrumb"><spring:message code="${page}" /></a>
        </div>
    </div>
    <div class="container row">

        <h3><spring:message code="${pageName}" arguments="${username}" /></h3>

        <c:forEach var="user" items="${users}">
            <div class="col s12 l6 margin-bottom-2">
                <c:set var="user" value="${user}" scope="request"/>
                <c:import url="/WEB-INF/jsp/profile/profile-card.jsp"/>
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
