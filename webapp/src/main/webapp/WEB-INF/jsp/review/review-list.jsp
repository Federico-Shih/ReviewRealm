<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review.css" />">
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <title><spring:message code="review.list.title"/></title>
</head>
<c:url value="/" var="applyFilters"/>
<body class="background has-background-black">
<jsp:include page="../static-components/navbar.jsp"/>
<div class="review-list-page">
    <div>
        <form action="${applyFilters}" class="review-filters-panel">
            <div class="review-filters-panel-section">
                <button type="submit" class="btn"><spring:message code="apply.filters"/></button>
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
                <span class="review-filters-panel-subtitle"><spring:message code="review.genres"/></span>
                <c:forEach var="genre" items="${filters.selectedGenres}">
                    <p>
                        <label>
                            <input name="f-gen" value="${genre.id}" type="checkbox" class="filled-in" checked/>
                            <span><spring:message code="${genre.name}"/></span>
                        </label>
                    </p>
                </c:forEach>
                <c:forEach var="genre" items="${filters.unselectedGenres}">
                    <p>
                        <label>
                            <input name="f-gen" value="${genre.id}" type="checkbox" class="filled-in"/>
                            <span><spring:message code="${genre.name}"/></span>
                        </label>
                    </p>
                </c:forEach>
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

    <div class="review-card-list">
        <c:if test="${empty reviews}">
            <div>
                <span><spring:message code="review.list.notfound"/></span>
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
                                <span class="chip-small"><spring:message code="${genre.name}"/></span>
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
                    <span id="review-card-bottom-text"> <spring:message code="review.by"/>
                        <span id="review-card-author">
                            @<c:out value="${review.author.username}"/>
                        </span><%--, quien prefiere:--%>
                    </span>
<%--                    TODO: PREFERENCIAS DE USUARIO--%>
<%--                    <c:forEach var="genre" items="${review.author.preferences}">--%>
<%--                        <span class="chip-small-inverted"><c:out value="${genre.name}"/></span>--%>
<%--                    </c:forEach>--%>
                </div>
            </div>
        </c:forEach>
    </div>

</div>
</body>
</html>
