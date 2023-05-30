<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
    <title><spring:message code="changepassword.title" arguments="PAGE.ARGUMENTS" /></title> <!-- Compiled and minified CSS -->
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review/review-page.css" />">
    <link rel="stylesheet" href="<c:url value="/css/change-password.css"/>" >
    <!-- Compiled and minified JavaScript -->
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <script>
        <c:if test="${emailSent != null && emailSent}">
            document.addEventListener('DOMContentLoaded', function() {
                    M.toast({html: '<spring:message code="resend.email.sent" />', classes: 'created-toast'})
            }, false)
        </c:if>
    </script>
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
<div class="container form-container">
    <article class="card card-container">
        <h5 class="card-title title-container"><spring:message code="email.changepassword.cta"/></h5>
        <div class="card-content">
            <c:url value="/changepassword/${token}" var="changePasswordUrl"/>
            <form:form modelAttribute="passwordForm" action="${changePasswordUrl}" method="post">
                <div class="input-field">
                    <i class="material-icons prefix">lock</i>
                    <form:label for="password" path="password"><spring:message
                            code="changepassword.password.label"/></form:label>
                    <form:input type="password" name="password" id="password" path="password" class="white-text"/>
                    <form:errors path="password" cssClass="error" element="span"/>
                </div>
                <div class="input-field">
                    <i class="material-icons prefix">lock</i>
                    <form:label for="confirm" path="repeatPassword"><spring:message
                            code="changepassword.confirm.label"/></form:label>
                    <form:input type="password" name="repeatPassword" id="confirm" path="repeatPassword"
                                class="white-text"/>
                    <form:errors path="repeatPassword" cssClass="error" element="span"/>
                </div>
                <div>
                    <spring:message code="changepassword.request.description"/>
                    <a href="<c:url value="/recover-password" />">
                        <spring:message code="changepassword.request.cta"/>
                    </a>
                </div>
                <button class="btn-large" type="submit"><spring:message code="email.changepassword.cta"/></button>
            </form:form>
        </div>
    </article>
</div>
</body>
</html>