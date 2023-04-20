<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:set var="contextPath" value="${pageContext.request.requestURI}"/>

<div>
    <nav>
        <div class="navbar">
            <div>
                <a href="<c:url value="/"/>" class="navbar-logo">
                    <img class="navbar-logo-image" src="<c:url value="/static/review_realm_logo_white_630px.png"/>" alt="Review Realm Logo">
                    <span class="navbar-logo-title">Review Realm</span>
                </a>
            </div>
            <div>
                <ul class="navbar-options center">
                    <li><a href="<c:url value="/game/list"/>" class="${contextPath == "/paw-2023a-04/WEB-INF/jsp/games/game-list.jsp" ? "chosen-tab" : "" }"><span><spring:message code="navbar.games"/></span></a></li>
                    <li><a href="<c:url value="/"/>" class="${contextPath == "/paw-2023a-04/WEB-INF/jsp/review/review-list.jsp" ? "chosen-tab" : "" }"><span><spring:message code="navbar.reviews"/></span></a></li>
                </ul>
            </div>
        </div>
    </nav>
</div>
