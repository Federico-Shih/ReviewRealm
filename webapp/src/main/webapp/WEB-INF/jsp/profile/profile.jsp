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
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <title><spring:message code="profile.title"/></title>
</head>

<body>
<jsp:include page="../static-components/navbar.jsp">
    <jsp:param name="selected" value="profile"/>
</jsp:include>
<div class="container">
    <div class="profile-info-panel">
        <img class="profile-info-panel-image"
             src="https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png"
             alt="profilePic"/> <!-- TODO: imágenes de perfil -->
        <div class="profile-info-panel-info">
            <span class="profile-user-name">${profile.username}</span>
            <div class="profile-preferences">
                <span><spring:message code="profile.fanof"/></span>
                <div class="profile-preferences-list">
                    <c:forEach var="genre" items="${profile.preferences}">
                        <span class="chip-small-inverted"><spring:message code="${genre.name}"/></span>
                    </c:forEach>
                </div>
            </div>
            <div class="profile-followers">
                <a href="<c:url value="/profile/${profile.id}/followers"/>">
                    <span class="white-text"><spring:message code="profile.followers"
                                                             arguments="${followerCount}"/></span>
                </a>
                <div class="divider-v profile-followers-text-divider"></div>
                <a href="<c:url value="/profile/${profile.id}/following"/>">
            <span class="white-text">
              <spring:message code="profile.following" arguments="${followingCount}"/>
            </span>
                </a>
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
                <c:if test="${isProfileSelf}">
                    <a href="<c:url value="/profile/edit"/>" class="waves-effect waves-light btn-small deep-purple">
                        <i class="material-icons left">edit</i>
                        <span>
                            <spring:message code="profile.edit"/>
                        </span>
                    </a>
                </c:if>
            </div>
        </div>
    </div>
    <div class="divider-h"></div>
    <div class="profile-favorite-games-panel">
        <h5><spring:message code="profile.favgames"/></h5>
        <div class="profile-favorite-games-panel-list">
            <c:forEach var="game" items="${games}">
                <a class="profile-favorite-game" href="<c:url value="/game/${game.id}"/> ">
                    <div class="row">
                        <div class="game-card-for-list medium z-depth-4">
                            <c:url value="${game.imageUrl}" var="imgUrl" />
                            <img class="game-img" src="${imgUrl}"
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
            <c:if test="${fn:length(games) == 0}">
                <div>
                    <span><spring:message code="profile.nofavorites"/></span>
                </div>
            </c:if>
        </div>
    </div>
    <div class="divider-h"></div>
    <div class="profile-reviews-panel">
        <h5>
            <spring:message code="profile.reviews" arguments="${profile.username}"/>
            <span class="profile-reviews-count">(${fn:length(reviews)})</span>
        </h5>
        <div class="profile-review-list">
            <c:forEach var="review" items="${reviews}">
                <div class="card review-card">
                    <div class="review-card-header">
                        <div class="review-card-header-start">
                            <a id="review-card-game-title" href="<c:url value="/game/${review.reviewedGame.id}"/>">
                            <span>
                                <c:out value="${review.reviewedGame.name}"/>
                            </span>
                            </a>
                            <div>
                                <c:forEach var="genre" items="${review.reviewedGame.genres}">
                                    <span class="chip-small">
                                        <a href="<c:url value="/game/list?f-gen=${genre.id}"/>" class="white-text">
                                                <spring:message code="${genre.name}"/>
                                        </a>
                                    </span>
                                </c:forEach>
                            </div>
                        </div>
                        <div class="review-card-header-end">
                            <span id="review-card-score"><c:out value="${review.rating}"/></span>
                            <span id="review-card-score-outof">/10</span>
                            <i class="material-icons small">star</i>
                        </div>
                    </div>
                    <div class="divider-h"></div>
                    <div class="review-card-body">
                        <c:url value="/review/${review.id}" var="reviewUrl"/>
                        <a href="${reviewUrl}">
                            <span id="review-card-title"><c:out value="${review.title}"/></span>
                        </a>
                        <span id="review-card-content"><c:out value="${review.content}"/></span>
                        <span id="review-card-date"><c:out value="${review.createdFormatted}"/></span>
                    </div>
                </div>
            </c:forEach>
            <c:if test="${fn:length(reviews) == 0}">
                <div>
                    <span><spring:message code="profile.noreviews"/></span>
                </div>
            </c:if>
        </div>
    </div>
</div>
</body>
</html>
