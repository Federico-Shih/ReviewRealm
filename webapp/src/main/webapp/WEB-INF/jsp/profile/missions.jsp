<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
    <title><spring:message code="missions.title" arguments="PAGE.ARGUMENTS" /></title> <!-- Compiled and minified CSS -->
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/missions.css" />">

    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
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
    <div class="card">
        <div class="card-content">
            <span class="card-title">
                <spring:message code="missions.currentxp.title" />
            </span>
            <div class="progress-fontsize">
                <spring:message code="missions.currentlevel.label" arguments="${level}" />
            </div>
            <div class="progress-fontsize">
                <spring:message code="missions.totalxp.label" arguments="${xp}" />
            </div>
            <div>
                <span class="progress custom-progress" style="height: 5px;">
                    <div class="determinate custom-progress" style="width: ${currentLevelXp}%;"></div>
                </span>
                <span>${currentLevelXp}/${100}</span>
            </div>
        </div>
    </div>
    <div class="card">
        <div class="card-content">
            <span class="card-title">
                <spring:message code="missions.card.title" />
            </span>
            <c:forEach items="${missions}" var="missionProgress">
                <div class="card">
                    <div class="card-content mission-card">
                        <span class="card-title">
                            <spring:message code="${missionProgress.mission.title}" />
                        </span>
                        <div>
                            <spring:message code="${missionProgress.mission.description}" arguments="${missionProgress.mission.target}" />
                        </div>
                        <div>
                            <div class="progress">
                                <div class="determinate" style="width: ${missionProgress.progress/missionProgress.mission.target * 100}%"></div>
                            </div>
                            <div>${missionProgress.progress}/${missionProgress.mission.target}</div>
                        </div>
                        <div>
                            <spring:message code="missions.timescompleted.label" arguments="${missionProgress.times}" />
                        </div>
                        <div class="mission-data">
                            <c:if test="${missionProgress.mission.repeatable}">
                                <span class="mission-chip">
                                        <i class="material-icons">repeat</i>
                                    <span>
                                        <spring:message code="${missionProgress.mission.frequency.name}" />
                                    </span>
                                </span>
                            </c:if>
                            <span class="mission-chip ">
                                <spring:message code="missions.xp.label" arguments="${missionProgress.mission.xp}" />
                            </span>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>
</body>
</html>