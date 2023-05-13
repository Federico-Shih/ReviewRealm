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
    <script src="<c:url value="/js/reviewfeedback.js" />"></script>
</head>
<c:url value="/" var="applyFilters"/>

<body class="background has-background-black">
<jsp:include page="../static-components/navbar.jsp"><jsp:param name="selected" value="review-list" /></jsp:include>

<div class="review-list-page">
    <div class="left-panel">
        <form action="${applyFilters}" class="review-filters-panel">
            <div class="review-filters-panel-section">
                <button type="submit" class="btn"><spring:message code="apply.filters"/></button>
                <span class="review-filters-panel-title"><spring:message code="order.by"/></span>
                <div>
                    <c:forEach var="criteria" items="${orderCriteria}">
                        <p>
                            <label>
                                <input name="o-crit" value="${criteria.value}" type="radio" <c:if
                                        test="${selectedOrderCriteria.value == criteria.value}"> checked </c:if>/>
                                <span><spring:message code="${criteria.localizedNameCode}"/></span>
                            </label>
                        </p>
                    </c:forEach>
                    <div class="divider-h"></div>
                    <c:forEach var="direction" items="${orderDirections}">
                        <p>
                            <label>
                                <input name="o-dir" value="${direction.value}" type="radio" <c:if
                                        test="${selectedOrderDirection.value == direction.value}"> checked </c:if>/>
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
                <c:if test="${setPreferences}">
                    <a href="${queriesToKeepAtRemoveFilters}${favGameGenreFilters}"><button type="button" class="btn">
                        <span><spring:message code="set.favgenres"/></span>
                    </button></a>
                </c:if>
                <ul class="collapsible review-filters-collapsible-button">
                    <li>
                        <div class="collapsible-header filters-header f-row f-ai-center">
                            <i class="material-icons">videogame_asset</i>
                            <span class="review-filters-panel-subtitle"><spring:message code="review.genres"/></span>
                            <i class="material-icons right">arrow_drop_down</i>
                        </div>
                        <div class="collapsible-body row filters-container">
                            <c:forEach var="genre" items="${filters.selectedGenres}">
                                <p class="col s12 l6">
                                    <label>
                                        <input name="f-gen" value="${genre.id}" type="checkbox" class="filled-in" checked/>
                                        <span><spring:message code="${genre.name}"/></span>
                                    </label>
                                </p>
                            </c:forEach>
                            <c:forEach var="genre" items="${filters.unselectedGenres}">
                                <p class="col s12 l6">
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
        <div class="review-card-list row">
            <c:if test="${empty reviews}">
                <div>
                    <span><spring:message code="review.list.notfound"/></span>
                    <a href="${queriesToKeepAtRemoveFilters}">
                        <span><spring:message code="remove.filters"/></span>
                    </a>
                </div>
            </c:if>
            <c:forEach var="review" items="${reviews}">
                <c:set var="review" value="${review}" scope="request" />
                <c:import url="/WEB-INF/jsp/review/review-card.jsp" />
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
