<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title><spring:message code="resend.email.title" arguments="PAGE.ARGUMENTS"/></title>
    <!-- Compiled and minified CSS -->
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review/review-page.css" />">
    <!-- Compiled and minified JavaScript -->
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
</head>
<!-- general variables
<%--<spring:message code="reviewForm.title.placeholder" var="titlePlaceholder"/>--%>
<%--<spring:message code="reviewForm.content.placeholder" var="contentPlaceholder"/>--%>
<%--<spring:message code="review.author.generic" var="authorPlaceholder"/>--%>
<%--<c:url value="/game/${game.id}" var="gameUrl" />--%>
-->
<body>
<jsp:include page="/WEB-INF/jsp/static-components/navbar.jsp"/>
<div class="container">
    <article class="card">
        <div class="card-content">
            <h5><spring:message code="resend.email"/></h5>
            <c:url var="resendUrl" value="/resend-email"/>
            <form:form modelAttribute="resendEmailForm" action="${resendUrl}" method="post">
                <div class="input-field">
                    <form:input path="email" class="validate white-text" id="email"/>
                    <form:label for="email" path="email">
                        <spring:message code="login.email.field"/>
                    </form:label>
                    <form:errors cssClass="error" path="email"/>
                </div>
                <div>
                    <span><spring:message code="has-token.text"/></span>
                    <a href="<c:url value="/recover" />">
                        <spring:message code="has-token.cta"/>
                    </a>
                </div>
                <button type="submit" class="btn-large margin-top-2">
                    <spring:message code="register.submit"/>
                </button>
            </form:form>
        </div>
    </article>
</div>
</body>
</html>