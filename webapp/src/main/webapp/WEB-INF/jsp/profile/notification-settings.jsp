<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/flex.css" />">
    <link rel="stylesheet" href="<c:url value="/css/profile.css" />"/>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <title><spring:message code="notification.settings.title"/></title>
</head>

<c:url value="/profile/settings/notifications" var="saveSettings"/>
<body>
<jsp:include page="../static-components/navbar.jsp">
    <jsp:param name="selected" value="profile"/>
</jsp:include>
<div class="container">
    <h4><spring:message code="notification.settings"/></h4>
    <div class="divider-h"></div>
    <form:form modelAttribute="notificationsForm" action="${saveSettings}" method="post">
        <div class="f-column f-gap-2">
            <c:forEach items="${currentNotificationSettings}" var="setting">
                <label class="margin-top-1">
                    <input type="checkbox" value="${setting.key.typeName}" name="notificationSettings" ${setting.value ? "checked" : ""}/>
                    <span><spring:message code="${setting.key.nameCode}"/></span>
                </label>
            </c:forEach>
            <button class="btn margin-top-1 width-fit-content" type="submit">
                <i class="material-icons left">save</i>
                <spring:message code="notification.settings.save"/>
            </button>
        </div>
    </form:form>
</div>
</body>
</html>
