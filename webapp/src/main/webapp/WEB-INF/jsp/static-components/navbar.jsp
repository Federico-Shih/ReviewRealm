<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>

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
                    <li><a href="<c:url value="/game/list"/>" class="${param.selected == "game-list" ? "chosen-tab" : "" }"><span><spring:message code="navbar.games"/></span></a></li>
                    <li><a href="<c:url value="/"/>" class="${param.selected == "review-list" ? "chosen-tab" : "" }"><span><spring:message code="navbar.reviews"/></span></a></li>
                    <c:if test="${loggedUser != null}">
                        <li><a href="<c:url value="/profile/${loggedUser.id}"/>" class="${param.selected == "profile" ? "chosen-tab" : "" }"><span><spring:message code="navbar.profile"/></span></a></li>
                        <li><span><spring:message code="navbar.welcome" arguments="${loggedUser.username}"/></span></li>
                        <li><a href="<c:url value="/logout"/>"><span><spring:message code="navbar.logout"/></span></a></li>
                    </c:if>
                    <c:if test="${loggedUser == null}">
                        <li><a href="<c:url value="/login"/>" class="${param.selected == "login" ? "chosen-tab" : "" }"><span><spring:message code="navbar.login"/></span></a></li>
                    </c:if>
                </ul>
            </div>
        </div>
    </nav>
</div>
