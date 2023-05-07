<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
    <title><spring:message code="validation.title" arguments="PAGE.ARGUMENTS" /></title> <!-- Compiled and minified CSS -->
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
    <h5><spring:message code="validation.title" /></h5>
    <c:url var="validateUrl" value="/validate" />
    <form action="${validateUrl}" method="post">
        <div class="input-field ">
            <input id="code" type="text" class="validate white-text" name="validationCode" />
            <label for="code"><spring:message code="validate.token.title" /></label>
            <c:if test="${unknownToken != null && unknownToken}">
                <p class="error">
                    <spring:message code="validation.content.unknown-token" />
                </p>
            </c:if>
            <c:if test="${expiredToken != null && expiredToken}">
                <p class="error">
                    <spring:message code="validation.content.expired-token" />
                </p>
            </c:if>
        </div>
        <button type="submit" class="btn-large">
            <spring:message code="email.validation.cta" />
        </button>
    </form>
    <h5><spring:message code="resend.email" /></h5>
    <c:url var="resendUrl" value="/resend-email" />
    <form:form modelAttribute="resendEmailForm" action="${resendUrl}" method="post">
        <div class="input-field">
            <form:input path="email" class="validate white-text" id="email" />
            <form:label for="email" path="email">
                <spring:message code="login.email.field" />
            </form:label>
            <form:errors cssClass="error" path="email" />
        </div>
        <button type="submit" class="btn-large">
            <spring:message code="register.submit" />
        </button>
    </form:form>
    <c:if test="${emailSuccess}">
        <h6><spring:message code="resend.email.sent" /></h6>
    </c:if>
</div>
</body>
</html>