<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<div>
    <nav>
        <div class="navbar">
            <div>
                <a href="${pageContext.request.contextPath}/" class="navbar-logo">
                    <img class="navbar-logo-image" src="${pageContext.request.contextPath}/static/review_realm_logo_white_630px.png" alt="Review Realm Logo">
                    <span class="navbar-logo-title">Review Realm</span>
                </a>
            </div>
            <div>
                <ul class="navbar-options center">
                    <li><a href="${pageContext.request.contextPath}/game/list"><spring:message code="navbar.games"/></a></li>
                    <li><a href="${pageContext.request.contextPath}/"><spring:message code="navbar.reviews"/></a></li>
                </ul>
            </div>
        </div>
    </nav>
</div>
