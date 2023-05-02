<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <link rel="stylesheet" href="<c:url value="/css/main.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/user.css"/>">
    <title><spring:message code="login.title"/></title>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
</head>
<c:url value="/login" var="postPath"/>
<body>
<jsp:include page="/WEB-INF/jsp/static-components/navbar.jsp"><jsp:param name="selected" value="login"/></jsp:include>
    <div class="container">
        <h3><spring:message code="login.header.title"/></h3>
        <form class="card form-body" action="${postPath}" method="post">
            <div class="input-field">
                <i class="material-icons prefix">email</i>
                <label for="email"><spring:message code="login.email.field"/></label>
                <input type="text" name="email" id="email" class="white-text" required>
            </div>
            <div class="input-field">
                <i class="material-icons prefix">lock</i>
                <label for="password"><spring:message code="login.password.field"/></label>
                <input type="password" name="password" id="password" class="white-text" required>
            </div>
            <div>
                <label for="remember-me">
                    <input type="checkbox" name="remember-me" id="remember-me" />
                    <span> <spring:message code="login.rememberme" /></span>
                </label>
            </div>
            <div class="error">
                <c:if test="${error}">
                    <span><spring:message code="login.error"/></span>
                </c:if>
            </div>
            <div>
                <a href="<c:url value="/register" />"><spring:message code="login.to.register.link"/></a>
            </div>
            <div>
                <a href="<c:url value="/recover" />"><spring:message code="login.to.validate.link"/></a>
            </div>
            <div class="form-submit-button">
                <button class="btn waves-effect waves-light" type="submit" name="action"><spring:message code="login.submit"/>
                    <i class="material-icons right">send</i>
                </button>
            </div>
        </form>
    </div>
</body>
</html>