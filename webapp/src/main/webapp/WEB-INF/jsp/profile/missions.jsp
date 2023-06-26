<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title><spring:message code="missions.title" arguments="PAGE.ARGUMENTS"/></title> <!-- Compiled and minified CSS -->
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/missions.css" />">

    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <!-- Compiled and minified JavaScript -->
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            var elems = document.querySelectorAll('.tooltipped');
            var instances = M.Tooltip.init(elems, {});
        });
    </script>
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
        <div class="card-content f-row full-width f-jc-sbetween">
            <section class="progress-container">
                <span class="card-title">
                    <spring:message code="missions.currentxp.title"/>
                </span>
                <div class="progress-fontsize">
                    <spring:message code="missions.totalxp.label" arguments="${xp}"/>
                </div>
                <div>
                    <fmt:formatNumber value="${currentLevelXp}" maxFractionDigits="0" var="currentxp"/>
                    <span class="progress custom-progress tooltipped" data-position="bottom"
                          data-tooltip="${currentxp}/100" style="height: 5px;">
                    <div class="determinate custom-progress" style="width: ${currentLevelXp}%;"></div>
                </span>
                    <span>${currentxp}/${100}</span>
                </div>
            </section>
            <section class="">
                <div class="level-container no-wrap">
                    <spring:message code="profile.level" arguments="${level}"/>
                </div>
            </section>
        </div>
    </div>
    <div class="card">
        <div class="card-content">
            <span class="card-title">
                <spring:message code="missions.card.title"/>
            </span>
            <div class="row ">
                <c:forEach items="${missions}" var="missionProgress">
                    <div class="col s12 xl6">
                    <div class="card mission-card-container">
                        <div class="card-content mission-card">
                            <span class="card-title">
                                <spring:message code="${missionProgress.mission.title}"/>
                            </span>
                            <div>
                                <spring:message code="${missionProgress.mission.description}"
                                                arguments="${missionProgress.mission.target}"/>
                            </div>
                            <div>
                                <div class="progress">
                                    <div class="determinate"
                                         style="width: ${missionProgress.progress/missionProgress.mission.target * 100}%"></div>
                                </div>
                                <div>
                                    <fmt:formatNumber value="${missionProgress.progress}"
                                                      maxFractionDigits="0"/>/<fmt:formatNumber
                                        value="${missionProgress.mission.target}" maxFractionDigits="0"/>
                                </div>
                            </div>
                            <div>
                                <spring:message code="missions.timescompleted.label"
                                                arguments="${missionProgress.times}"/>
                            </div>
                            <div class="mission-data">
                                <c:if test="${missionProgress.mission.repeatable}">
                                <span class="mission-chip">
                                        <i class="material-icons">repeat</i>
                                    <span>
                                        <spring:message code="${missionProgress.mission.frequency.name}"/>
                                    </span>
                                </span>
                                </c:if>
                                <span class="mission-chip ">
                                <spring:message code="missions.xp.label" arguments="${missionProgress.mission.xp}"/>
                            </span>
                            </div>
                        </div>
                    </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</div>
</body>
</html>