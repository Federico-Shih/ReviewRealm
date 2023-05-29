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
    <title><spring:message code="error403.title"/></title>
</head>
<body>
<jsp:include page="../static-components/navbar.jsp"/>
<div class="row">
    <div class="col s12 center">
        <h4><spring:message code="error403.title"/></h4>
    </div>
    <div class="col s12 center">
        <h5><spring:message code="error403.description"/></h5>
    </div>
    <div class="col s12 center">
        <h5><spring:message code="error403.solution"/></h5>
    </div>
    <div class="col s12 center">
        <a href="<c:url value="/"/>">
            <img src="<c:url value="/static/prohibited.jpg"/>" alt='<spring:message code="error403.altimage"/>'/>
        </a>
    </div>
</div>
</body>
</html>
