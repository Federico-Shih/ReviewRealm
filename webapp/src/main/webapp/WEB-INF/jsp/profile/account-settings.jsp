<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/profile.css" />"/>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <title><spring:message code="account.settings.title"/></title>
</head>

<body>
<jsp:include page="../static-components/navbar.jsp">
    <jsp:param name="selected" value="profile"/>
</jsp:include>
<div class="container">
    <h4><spring:message code="account.settings"/></h4>
    <div class="divider-h"></div>
    <ul class="f-column full-width">
        <li class="full-width settings-option">
            <a href="<c:url value="/profile/settings/preferences"/>" class="full-width no-a-decoration waves-effect waves-light border-button f-row f-jc-start f-ai-center f-gap-2">
                <i class="material-icons medium">sports_esports</i>
                <span class=""><spring:message code="account.settings.preferences"/></span>
            </a>
        </li>
        <li class="full-width settings-option">
            <a href="<c:url value="/profile/settings/notifications"/>" class="full-width no-a-decoration waves-effect waves-light border-button f-row f-jc-start f-ai-center f-gap-2">
                <i class="material-icons medium">notifications</i>
                <span class=""><spring:message code="account.settings.email.notifications"/></span>
            </a>
        </li>
    </ul>
</div>
</body>
</html>
