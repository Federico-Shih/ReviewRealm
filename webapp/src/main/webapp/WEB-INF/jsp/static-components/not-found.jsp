<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <link rel="stylesheet" href="<c:url value="/css/materialize.min.css" />">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <title><spring:message code="error404.title"/></title>
</head>
<body>
<jsp:include page="navbar.jsp"/>
    <h4><spring:message code="error404.notfound"/></h4>
    <div class="middle-column">
        <div>
            <h5><spring:message code="error404.wasteland"/></h5>
        </div>
        <div>
            <h5><spring:message code="error404.solution"/></h5>
        </div>
        <a href="<c:url value="/"/>">
            <img src="<c:url value="/static/gobackportal.png"/>" alt='<spring:message code="error404.redportal"/>'/>
        </a>
    </div>
</body>
</html>
