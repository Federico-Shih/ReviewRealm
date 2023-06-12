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
    <link rel="stylesheet" href="<c:url value="/css/user.css" />">
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
            <c:if test="${registered}">
                <div class="card green darken-1">
                    <div class="card-content white-text f-row f-gap-2">
                        <div>
                            <i class="material-icons medium">email</i>
                        </div>
                        <div>
                            <span class="card-title"><spring:message code="registered.success.title" /></span>
                            <p><spring:message code="validation.email.sent"/></p>
                        </div>
                    </div>
                </div>
            </c:if>
            <c:if test="${resent}">
                <div class="card lime darken-3">
                    <div class="card-content white-text f-row f-gap-2">
                        <div>
                            <i class="material-icons medium">email</i>
                        </div>
                        <div>
                            <span class="card-title"><spring:message code="resend.email.success.title" /></span>
                            <p><spring:message code="validation.email.sent"/></p>
                        </div>
                    </div>
                </div>
            </c:if>
            <h5><spring:message code="validation.title" /></h5>
            <c:url var="validateUrl" value="/validate" />
            <form action="${validateUrl}" method="post">
                <div class="input-field ">
                    <input id="code" type="text" class="validate white-text" name="validationCode" />
                    <label for="code"><spring:message code="validate.token.title" /></label>
                    <c:if test="${unknownToken != null && unknownToken}">
                        <p class="error">
                            <spring:message code="validation.content.unknown-token"/>
                        </p>
                    </c:if>
                    <c:if test="${expiredToken != null && expiredToken}">
                        <p class="error">
                            <spring:message code="validation.content.expired-token"/>
                        </p>
                    </c:if>
                </div>
                <div>
                    <span><spring:message code="no-token.text"/></span>
                    <a href="<c:url value="/resend-email" />">
                        <spring:message code="no-token.cta"/>
                    </a>
                </div>
                <button type="submit" class="btn-large margin-top-2">
                    <spring:message code="email.validation.cta"/>
                </button>
            </form>
        </div>
    </article>
</div>
</body>
</html>