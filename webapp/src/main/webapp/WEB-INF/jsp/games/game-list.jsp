<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <title><spring:message code="game.list.title"/></title>
    <link rel="stylesheet" href="<c:url value="/css/materialize.min.css" />">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link href="<c:url value="/css/game.css" />" rel="stylesheet"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review.css"/>">
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            var elems = document.querySelector('.collapsible');
            var instance = M.Collapsible.init(elems, { accordion: false });
            <c:if test="${fn:length(filters.selectedGenres) > 0}">
                instance.open(0);
            </c:if>
        });
    </script>
</head>
<body>
<jsp:include page="../static-components/navbar.jsp">
    <jsp:param name="selected" value="game-list"/>
</jsp:include>
<c:url value="/game/list/" var="queryFinish"/>
<div>
    <div class="row">
        <form action="${queryFinish}">
            <div class="col s2" style="width: 20%;">
                <div class="review-filters-panel-section" >
                    <div class="review-filters-panel-section">
                        <button type="submit" class="btn truncate" style="width: 100%"><spring:message code="apply.filters"/></button>
                        <span class="review-filters-panel-title"><spring:message code="order.by"/></span>
                        <div>
                            <c:forEach var="criteria" items="${orderCriteria}">
                                <p>
                                    <label>
                                        <input name="o-crit" value="${criteria.value}" type="radio" <c:if
                                                test="${filters.gameOrderCriteria.value == criteria.value}"> checked </c:if>/>
                                        <span><spring:message code="${criteria.localizedNameCode}"/></span>
                                    </label>
                                </p>
                            </c:forEach>
                            <div class="divider-h"></div>
                            <c:forEach var="direction" items="${orderDirections}">
                                <p>
                                    <label>
                                        <input name="o-dir" value="${direction.value}" type="radio" <c:if
                                                test="${filters.orderDirection.value == direction.value}"> checked </c:if>/>
                                        <span><spring:message code="${direction.localizedNameCode}"/></span>
                                    </label>
                                </p>
                            </c:forEach>
                        </div>
                    </div>
                    <div class="divider-h"></div>
                    <div class="review-filters-panel-section">
                        <span class="review-filters-panel-title"><spring:message code="review.filters"/></span>
                        <a href="${queriesToKeepAtRemoveFilters}"><button type="button" class="remove-filter-button btn-small blue-grey darken-3">
                            <i class="material-icons">clear</i>
                            <span><spring:message code="remove.filters"/></span>
                        </button></a>
                        <ul class="collapsible" style="width: 100%">
                            <li style="width: 100%">
                                <div class="collapsible-header filters-header" style="width: 100%">
                                    <i class="material-icons">videogame_asset</i>
                                    <span class="review-filters-panel-subtitle"><spring:message code="review.genres"/></span>
                                    <i class="material-icons right">arrow_drop_down</i>
                                </div>
                                <div class="collapsible-body row filters-container">
                                    <c:forEach var="genre" items="${filters.selectedGenres}">
                                        <p class="col s6">
                                            <label>
                                                <input name="f-gen" value="${genre.id}" type="checkbox" class="filled-in" checked/>
                                                <span><spring:message code="${genre.name}"/></span>
                                            </label>
                                        </p>
                                    </c:forEach>
                                    <c:forEach var="genre" items="${filters.unselectedGenres}">
                                        <p class="col s6">
                                            <label>
                                                <input name="f-gen" value="${genre.id}" type="checkbox" class="filled-in"/>
                                                <span><spring:message code="${genre.name}"/></span>
                                            </label>
                                        </p>
                                    </c:forEach>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="col push-s1 s8">
                <div class="search-game-list">
                    <input name="search" class="z-depth-1-half search-field" type="search" value="${searchField}"
                           placeholder="<spring:message code="game.list.placeholder.search"/>">
                    <button class="btn-flat button-color white-text" type="submit"><i class="material-icons">search</i>
                    </button>
                </div>
            </div>
        </form>
        <c:if test="${empty games}">
            <div class="col s9 center-align">
                <span><spring:message code="game.list.notfound"/></span>
            </div>
        </c:if>
        <div class="col s9 center-align" >
            <div class="row">
                <c:forEach items="${games}" var="game">
                    <div class="col s12 m6">
                        <div class="game-card-for-list z-depth-2">
                            <a href="<c:url value="/game/${game.id}"/>">
                                <c:url value="${game.imageUrl}" var="imgUrl" />
                                <img class="game-img" src="${imgUrl}"
                                     alt="<c:out value="${game.name}"/>">
                            </a>
                            <div class="game-card-details">
                                <div class="game-card-details-text">
                                    <a class="game-card-title white-text" href="<c:url value="/game/${game.id}"/>">
                                        <span ><c:out value="${game.name}"/></span>
                                    </a>
                                    <span class="game-card-text"><spring:message code="publishing.date"
                                                                                 arguments="${game.publishDate}"/></span>
                                    <span class="game-card-text"><spring:message code="developer"
                                                                                 arguments="${game.developer}"/></span>
                                </div>
                                <div class="game-card-details-extra">
                                    <div class="game-genres">
                                        <c:forEach items="${game.genres}" var="genre" end="1">
                                            <a href="${queriesToKeepAtRemoveFilters}&f-gen=${genre.id}" class="white-text">
                                            <span class="chip-small">
                                                <spring:message code="${genre.name}"/>
                                            </span>
                                            </a>
                                        </c:forEach>
                                    </div>
                                    <c:if test="${game.averageRating >0}">
                                <span class="game-card-details-extra-text"><spring:message
                                        code="game.details.review.statistics.rating"/></span>
                                        <span class=game-card-details-extra-number>${game.averageRatingString}
                                        <span class="game-card-details-extra-minor">/10</span>
                                    </span>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <ul class="center-align pagination">
                <c:if test="${currentPage > 1}">
                    <li class="waves-effect"><a href="${queriesToKeepAtPageChange}page=${currentPage-1}"><i
                            class="material-icons">chevron_left</i></a></li>
                </c:if>
                <c:forEach var="i" begin="${initialPage}" end="${maxPages}">
                    <c:if test="${i == currentPage}">
                        <li class="pagination-active"><a href="${queriesToKeepAtPageChange}page=${i}">${i}</a></li>
                    </c:if>
                    <c:if test="${i != currentPage}">
                        <li class="waves-effect"><a href="${queriesToKeepAtPageChange}page=${i}">${i}</a></li>
                    </c:if>
                </c:forEach>
                <c:if test="${currentPage < maxPages}">
                    <li class="waves-effect"><a href="${queriesToKeepAtPageChange}page=${currentPage+1}"><i
                            class="material-icons">chevron_right</i></a></li>
                </c:if>
            </ul>
        </div>
    </div>

</div>

</body>
</html>
