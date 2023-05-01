<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review.css" />">
    <link rel="stylesheet" href="<c:url value="/css/game.css" />"/>
    <link rel="stylesheet" href="<c:url value="/css/profile.css" />"/>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <title><spring:message code="edit.profile.title"/></title>
</head>
<c:url value="/profile/edit/submit" var="applyChanges"/>
<body>
    <jsp:include page="../static-components/navbar.jsp"/>
    <form:form modelAttribute="editProfileForm" action="${applyChanges}" method="post">
        <div class="row container">
            <div class="col s12 center">
                <h5><spring:message code="edit.profile.fav.genres"/></h5>
            </div>
            <div class="col s12 center">
                <c:forEach items="${profile.preferences}" var="favgenres">
                    <span class="chip-small">
                        <spring:message code="${favgenres.name}"/>
                    </span>
                </c:forEach>
                <c:if test="${empty profile.preferences}">
                    <h6><spring:message code="edit.profile.no.favgenres"/></h6>
                </c:if>
            </div>
            <div class="col s12 center">
                <h5><spring:message code="edit.profile.fav.genres.choose"/></h5>
            </div>
            <div class="col s12 center">
                <c:forEach items="${availableGenres}" var="genre"> <!--hacer sin form:-->
                    <label>
                        <span class="col s4 m3 center margin-for-genres">
                            <input type="checkbox" id="${genre.id}" value="${genre.id}" name="genres"/>
                            <span class="chip-small"><spring:message code="${genre.name}"/></span>
                        </span>
                    </label>
                </c:forEach>
            </div>
        </div>


        <form:errors path="genres" cssClass="error" element="p"/>

        <div class="divider"></div>
        <div class="row">
            <div class="col s12 center">
                <h5><spring:message code="edit.profile.fav.games"/></h5>
            </div>
            <c:forEach items="${games}" var="game">
                <div class="col s4">
                </div>
            </c:forEach>
            <div class="col s12 center">
                <h5><spring:message code="edit.profile.fav.games.choose"/></h5>
            </div>
        </div>



        <form:errors path="games" cssClass="error" element="p"/>


        <div class="row">
            <div class="col s12 center">
                <button class="btn" type="submit">
                    <spring:message code="edit.profile.save.changes"/>
                </button>
            </div>
        </div>

    </form:form>
</body>
</html>
