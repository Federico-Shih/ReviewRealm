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
    <title><spring:message code="edit.preferences.title"/></title>
</head>
<c:url value="/profile/settings/preferences" var="applyChanges"/>
<body>
    <jsp:include page="../static-components/navbar.jsp"/>
    <form:form modelAttribute="editPreferencesForm" action="${applyChanges}" method="post">
        <div class="row container">
            <div class="col s12 center">
                <h5><spring:message code="edit.preferences.fav.genres"/></h5>
            </div>
            <div class="col s12 center">
                <c:forEach items="${profile.preferences}" var="favgenres">
                    <span class="chip-small-inverted">
                        <spring:message code="${favgenres.name}"/>
                    </span>
                </c:forEach>
                <c:if test="${empty profile.preferences}">
                    <h6><spring:message code="edit.preferences.no.favgenres"/></h6>
                </c:if>
            </div>
            <div class="col s12 center">
                <h5><spring:message code="edit.preferences.fav.genres.choose"/></h5>
            </div>
            <div class="col s12 center">
                <c:forEach items="${availableGenres}" var="genre">
                    <label>
                        <span class="col s4 m3 center margin-for-genres">
                            <input type="checkbox" id="${genre.id}" value="${genre.id}" name="genres"/>
                            <span class="chip-small"><spring:message code="${genre.name}"/></span>
                        </span>
                    </label>
                </c:forEach>
            </div>

            <div class="col s12 center">
                <c:forEach items="${profile.preferences}" var="genre">
                    <label>
                        <span class="col s4 m3 center margin-for-genres">
                            <input type="checkbox" checked id="${genre.id}" value="${genre.id}" name="genres"/>
                            <span class="chip-small"><spring:message code="${genre.name}"/></span>
                        </span>
                    </label>
                </c:forEach>
            </div>
        </div>


        <form:errors path="genres" cssClass="error" element="p"/>


        <div class="row">
            <div class="col s12 center">
                <button class="btn" type="submit">
                    <i class="material-icons left">save</i>
                    <spring:message code="edit.preferences.save.changes"/>
                </button>
            </div>
        </div>

    </form:form>
</body>
</html>
