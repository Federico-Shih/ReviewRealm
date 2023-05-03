<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review.css" />">
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <title><spring:message code="review.list.title"/></title>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            var elem = document.querySelector('.collapsible');
            var instance = M.Collapsible.init(elem, { accordion: false });
            <c:if test="${fn:length(filters.selectedGenres) > 0}">
                instance.open(0);
            </c:if>
        });
    </script>
</head>
<c:url value="/" var="applyFilters"/>
<body class="background has-background-black">
<jsp:include page="../static-components/navbar.jsp"><jsp:param name="selected" value="review-list" /></jsp:include>

<div class="review-list-page">
    <div class="left-panel" style="padding: 0 .75rem">
        <form action="${applyFilters}" class="review-filters-panel">
            <div class="review-filters-panel-section">
                <button type="submit" class="btn" style="width: 100%"><spring:message code="apply.filters"/></button>
                <span class="review-filters-panel-title"><spring:message code="order.by"/></span>
                <div>
                    <c:forEach var="criteria" items="${orderCriteria}">
                        <p>
                            <label>
                                <input name="o-crit" value="${criteria.value}" type="radio" <c:if
                                        test="${filters.reviewOrderCriteria.value == criteria.value}"> checked </c:if>/>
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
                <%--<span class="review-filters-panel-subtitle">Preferencias del reseñador</span>
                <c:forEach var="genre" items="${filters.selectedPreferences}">
                    <p>
                        <label>
                            <input name="f-pref" value="${genre.id}" type="checkbox" class="filled-in" checked/>
                            <span><c:out value="${genre.name}"/></span>
                        </label>
                    </p>
                </c:forEach>
                <c:forEach var="genre" items="${filters.unselectedPreferences}">
                    <p>
                        <label>
                            <input name="f-pref" value="${genre.id}" type="checkbox" class="filled-in"/>
                            <span><c:out value="${genre.name}"/></span>
                        </label>
                    </p>
                </c:forEach>--%>
            </div>
        </form>
    </div>
    <div>
        <div class="divider-v" id="filter-panel-divider"></div>
    </div>

    <div class="right-panel">
        <div class="review-card-list">
            <c:if test="${empty reviews}">
                <div>
                    <span><spring:message code="review.list.notfound"/></span>
                    <a href="${queriesToKeepAtRemoveFilters}">
                        <span><spring:message code="remove.filters"/></span>
                    </a>
                </div>
            </c:if>
            <c:forEach var="review" items="${reviews}">
                <div class="card review-card">
                    <div class="review-card-header">
                        <div class="review-card-header-start">
                            <a id="review-card-game-title" href="./game/<c:out value="${review.reviewedGame.id}"/>">
                            <span >
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
                        <c:url value="/review/${review.id}" var="reviewUrl" />
                        <a href="${reviewUrl}">
                            <span id="review-card-title"><c:out value="${review.title}"/></span>
                        </a>
                        <span id="review-card-content"><c:out value="${review.content}"/></span>
                        <span id="review-card-date"><c:out value="${review.createdFormatted}"/></span>
                    </div>
                    <div class="divider-h"></div>
                    <div class="review-card-footer">
                    <c:url value="/profile/${review.author.id}" var="profileUrl" />
                    <a href="${profileUrl}" id="review-card-bottom-text review-card-author">
                        <spring:message code="review.by" arguments="@${review.author.username}"/>
                    </a>
                            <%--                    TODO: PREFERENCIAS DE USUARIO--%>
                            <%--                    <c:forEach var="genre" items="${review.author.preferences}">--%>
                            <%--                        <span class="chip-small-inverted"><c:out value="${genre.name}"/></span>--%>
                            <%--                    </c:forEach>--%>
                    </div>
                </div>
            </c:forEach>
        </div>
        <div class="row">
            <ul class="center-align pagination">
                <c:if test="${currentPage > 1}">
                    <li class="waves-effect"><a href="${queriesToKeepAtPageChange}page=${currentPage-1}"><i class="material-icons">chevron_left</i></a></li>
                </c:if>
                <c:forEach var="i" begin="${initialPage}" end="${maxPages}">
                    <c:if test="${i == currentPage}">
                        <li class="pagination-active"><a href="${queriesToKeepAtPageChange}page=${i}">${i}</a></li>
                    </c:if>
                    <c:if test="${i != currentPage}">
                        <li class="waves-effect"><a href="${queriesToKeepAtPageChange}page=${i}">${i}</a></li>
                    </c:if>
                </c:forEach >
                <c:if test="${currentPage < maxPages}">
                    <li class="waves-effect"><a href="${queriesToKeepAtPageChange}page=${currentPage+1}"><i class="material-icons">chevron_right</i></a></li>
                </c:if>
            </ul>
        </div>
    </div>
</div>
</body>
</html>
