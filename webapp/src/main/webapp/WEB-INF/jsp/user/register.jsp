<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <link rel="stylesheet" href="<c:url value="/css/main.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/user.css"/>">
    <title><spring:message code="register.title"/></title>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
</head>
<c:url value="/register" var="postPath"/>
<body>
<jsp:include page="/WEB-INF/jsp/static-components/navbar.jsp">
    <jsp:param name="selected" value="register"/>
</jsp:include>
<div class="container">
    <h3><spring:message code="register.header.title"/></h3>
    <form:form class="card form-body" action="${postPath}" method="post" modelAttribute="registerForm">
            <div class="input-field">
                <i class="material-icons prefix">email</i>
                <form:label for="email" path="email"><spring:message code="register.email.field"/></form:label>
                <form:input type="text" name="email" id="email" path="email" class="white-text"/>
                <form:errors path="email" cssClass="error" element="span"/>
            </div>
            <div class="input-field">
                <i class="material-icons prefix">person</i>
                <form:label for="username" path="username"><spring:message code="register.username.field"/></form:label>
                <form:input type="text" name="username" id="username" path="username" class="white-text"/>
                <form:errors path="username" cssClass="error" element="span"/>
            </div>
            <div class="input-field">
                <i class="material-icons prefix">lock</i>
                <form:label for="password" path="password"><spring:message code="register.password.field"/></form:label>
                <form:input type="password" name="password" id="password" path="password" class="white-text"/>
                <form:errors path="password" cssClass="error" element="span"/>
            </div>
            <div class="input-field">
                <i class="material-icons prefix">lock</i>
                <form:label for="repeatPassword" path="repeatPassword"><spring:message
                        code="register.repeat.password.field"/></form:label>
                <form:input type="password" name="repeatPassword" id="repeatPassword" path="repeatPassword" class="white-text"/>
                <form:errors path="repeatPassword" cssClass="error" element="span"/>
            </div>
            <div>
                <a href="<c:url value="/login" />"><spring:message code="register.to.login.link"/></a>
            </div>
            <div>
                <a href="<c:url value="/recover" />"><spring:message code="login.to.validate.link"/></a>
            </div>
            <div class="form-submit-button">
                <button class="btn waves-effect waves-light" type="submit" name="action">
                    <spring:message code="register.submit"/>
                    <i class="material-icons right">send</i>
                </button>
            </div>
        </form:form>


</div>
</body>
</html>