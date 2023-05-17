<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
    <script src="<c:url value="/js/reviewfeedback.js"/> "></script>

    <title><spring:message code="profile.title"/></title>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            var elems = document.querySelectorAll('.tooltipped');
            var instances = M.Tooltip.init(elems, {});

            <c:if test="${preferencesChanged}">
                M.toast({html: '<spring:message code="profile.preferences-changed" />', classes: 'created-toast'});
            </c:if>

            <c:if test="${avatarChanged}">
                M.toast({html: '<spring:message code="profile.avatar-changed" />', classes: 'created-toast'});
            </c:if>

            <c:if test="${notificationsChanged}">
                M.toast({html: '<spring:message code="profile.notifications-changed" />', classes: 'created-toast'});
            </c:if>
        });
    </script>
</head>

<c:set var="avatar" value="/static/avatars/${profile.avatarId}.png"/>

<body>
<jsp:include page="../static-components/navbar.jsp">
    <jsp:param name="selected" value="profile"/>
</jsp:include>
<div class="container">
    <div class="profile-info-panel f-row f-jc-sbetween">
        <div class="f-row f-ai-center">
            <img class="profile-info-panel-image"
                 src="<c:url value="${avatar}"/>"
                 alt="profilePic"/>
            <div class="profile-info-panel-info f-column f-jc-center">
                <div class="f-row f-ai-center f-gap-2">
                    <span class="profile-user-name">
                        ${profile.username}
                    </span>
                    <c:if test="${userModerator}">
                         <span class="tooltipped" data-position="top" data-tooltip="<spring:message code="profile.moderator" />">
                            <i class="material-icons profile-user-name">
                                engineering
                            </i>
                         </span>
                    </c:if>
                </div>


                <c:if test="${!(empty profile.preferences)}">
                    <div class="profile-preferences">
                        <span class="white-text no-wrap"><spring:message code="profile.fanof"/></span>
                        <div class="profile-preferences-list f-gap-1 full-width">
                            <c:forEach var="genre" items="${profile.preferences}">
                                <span class="chip-small-inverted">
                                    <a class="rr-blue-text" href="<c:url value="/game/list?f-gen=${genre.id}"/>">
                                        <spring:message code="${genre.name}"/>
                                    </a>
                                </span>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>
                <div class="profile-followers">
                    <a href="<c:url value="/profile/${profile.id}/followers"/>">
                    <span class="white-text no-wrap"><spring:message code="profile.followers"
                                                             arguments="${followerCount}"/></span>
                    </a>
                    <div class="divider-v profile-followers-text-divider"></div>
                    <a href="<c:url value="/profile/${profile.id}/following"/>">
                        <span class="white-text no-wrap">
                          <spring:message code="profile.following" arguments="${followingCount}"/>
                        </span>
                    </a>
                    <div class="divider-v profile-followers-text-divider"></div>
                    <span class="${profile.reputation > 0 ? 'green-text no-wrap' : (profile.reputation == 0) ? 'white-text no-wrap' : 'red-text no-wrap'}">
                        <spring:message code="profile.reputation" arguments="${profile.reputation}"/>
                    </span>
                </div>
                <div class="">
                    <c:if test="${!isProfileSelf}">
                        <c:if test="${following == null || !following}">
                            <c:url value="/profile/follow/${profile.id}" var="follow"/>
                            <form method="post" action="${follow}">
                                <button type="submit" class="waves-effect waves-light btn-small deep-purple">
                                    <spring:message code="profile.follow"/>
                                </button>
                            </form>
                        </c:if>
                        <c:if test="${following != null && following}">
                            <c:url value="/profile/unfollow/${profile.id}" var="unfollow"/>
                            <form method="post" action="${unfollow}">
                                <button type="submit" class="waves-effect waves-light btn-small deep-purple">
                                    <spring:message code="profile.unfollow"/>
                                </button>
                            </form>
                        </c:if>
                    </c:if>
                </div>
            </div>
        </div>
        <div>
            <c:if test="${isProfileSelf}">
                <a class="btn valign-wrapper" href="<c:url value="/profile/settings/"/>">
                    <i class="material-icons" aria-hidden="true">settings</i>
                </a>
            </c:if>
        </div>
    </div>
    <c:if test="${userHasNotSetPreferences}">
        <div class="card lime darken-3">
            <div class="card-content white-text f-row f-gap-2">
                <div class="">
                    <i class="material-icons medium">warning</i>
                </div>
                <div class="">
                    <span class="card-title"><spring:message code="profile.hasnotsetpreferences" /></span>
                    <a href="<c:url value="/profile/settings/preferences"/>" class="no-a-decoration btn-flat waves-effect waves-light border-button f-row f-jc-center f-ai-center">
                        <span><spring:message code="profile.hasnotsetpreferences.doit"/></span>
                    </a>
                </div>
            </div>
        </div>
    </c:if>
    <div class="divider-h"></div>
    <div class="profile-favorite-games-panel">
        <h5><spring:message code="profile.favgames"/></h5>
        <div class="profile-favorite-games-panel-list">
            <c:forEach var="game" items="${games}">
                <a class="profile-favorite-game" href="<c:url value="/game/${game.id}"/> ">
                    <div class="row">
                        <div class="game-card-for-list-small medium z-depth-4">
                            <c:url value="${game.imageUrl}" var="imgUrl" />
                            <img class="game-img-small" src="${imgUrl}"
                                 alt="<c:out value="${game.name}"/>">
                            <div class="game-card-title"><c:out value="${game.name}"/></div>
                            <div class="game-genres">
                                <c:forEach items="${game.genres}" var="genre">
                                    <span class="chip-small"><spring:message code="${genre.name}"/></span>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                </a>
            </c:forEach>
        </div>
        <c:if test="${fn:length(games) == 0}">
            <div class="margin-bottom-2">
                <c:if test="${isProfileSelf}">
                    <a href="<c:url value="/profile/settings/favgames"/>">
                        <span><spring:message code="profile.nofavorites.pick"/></span>
                    </a>
                </c:if>
                <c:if test="${!isProfileSelf}">
                    <span><spring:message code="profile.nofavorites"/></span>
                </c:if>
            </div>
        </c:if>
    </div>
    <div class="divider-h"></div>
    <div class="profile-reviews-panel">
        <h5>
            <spring:message code="profile.reviews" arguments="${profile.username}"/>
            <span class="profile-reviews-count">(${fn:length(reviews)})</span>
        </h5>
        <div class="row">
            <c:forEach var="review" items="${reviews}">
                <c:set var="review" value="${review}" scope="request" />
                <c:import url="/WEB-INF/jsp/review/review-card.jsp" />
            </c:forEach>
            <c:if test="${fn:length(reviews) == 0}">
                <div>
                    <span><spring:message code="profile.noreviews"/></span>
                </div>
            </c:if>
            <div class="col s12 center-align">
                <c:if test="${fn:length(reviews) == currentPageSize}">
                    <button class="btn-flat btn-floating-color no-a-decoration"> <a href="?pageSize=${fn:length(reviews) + defaultPageSize}"> <spring:message code="for-you.more"/> </a></button>
                </c:if>
            </div>
        </div>
    </div>
</div>
</body>
</html>
