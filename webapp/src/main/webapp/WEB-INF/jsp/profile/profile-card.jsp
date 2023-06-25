<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
<link rel="stylesheet" href="<c:url value="/css/main.css" />">
<link rel="stylesheet" href="<c:url value="/css/profile-card.css" />">
<script src="<c:url value="/js/materialize.min.js" />"></script>

<c:url value="/profile/${user.id}" var="profileUrl" />
<c:set var="avatar" value="/static/avatars/${user.avatarId}.png"/>

<div class="profile-card level-${user.levelRange.rangeTitle}-background">
    <div class="f-row f-jc-sbetween full-width">
        <div class="f-row f-jc-start full-width">
            <a href="${profileUrl}">
                <img class="profile-card-image"
                     src="<c:url value="${avatar}"/>"
                     alt="profilePic"/>
            </a>
            <div class="f-column f-ai-start f-jc-sevenly full-width">
                <a href="${profileUrl}">
                    <span class="profile-card-username white-text"><c:out value="${user.username}"/></span>
                </a>
                <c:if test="${!(empty user.preferences)}">
                    <div class="f-row f-ai-center f-gap-2 full-width">
                        <i class="material-icons profile-card-preferences-icon">
                            favorite
                        </i>
                        <span class="no-wrap profile-card-preferences"><spring:message code="review.author.preferences"/></span>
                    </div>

                </c:if>

                <div class="f-row f-ai-center f-gap-1">

                    <c:forEach var="genre" items="${user.preferences}" end="1">
                            <span class="chip-small-inverted">
                                <a href="<c:url value="/game/list?f-gen=${genre.id}"/>" class="rr-blue-text">
                                            <spring:message code="${genre.name}"/>
                                    </a>
                            </span>
                    </c:forEach>
                    <c:if test="${fn:length(user.preferences) > 2}">
                            <span class="chip-small-inverted">
                                <c:out value="+${fn:length(user.preferences) - 2}"/>
                            </span>
                    </c:if>
                </div>
            </div>
        </div>
        <div class="f-column f-jc-sbetween f-ai-end">
            <c:set var="level" value="${user.level}"/>
            <div class="level-badge-container">
                <i class="material-icons level-badge-icon level-${user.levelRange.rangeTitle}">sports_esports</i>
                <span class="level-badge-text"><c:out value="${level}"/></span>
            </div>
            <c:if test="${user.moderator}">
                <i class="material-icons moderator-badge">
                    engineering
                </i>
            </c:if>
        </div>
    </div>
</div>