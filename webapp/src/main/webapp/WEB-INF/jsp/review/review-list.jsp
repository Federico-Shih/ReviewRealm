<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="css/materialize.min.css" media="screen,projection"/>
    <link rel="stylesheet" href="css/main.css">
    <link rel="stylesheet" href="css/review.css">
    <title>Poner un Titulo</title> <!--TODO-->
</head>
<c:url value="/reviews" var="applyFilters"/>
<body class="background has-background-black">
<div class="review-list-page">
    <div class="review-card-list">
        <c:forEach var="review" items="${reviews}">
            <div class="card review-card darken-1">
                <div class="review-card-header">
                    <div class="review-card-header-start">
                        <span id="review-card-game-title"><c:out value="${review.reviewedGame.name}"/></span>
                        <div>
                            <c:forEach var="genre" items="${review.reviewedGame.genres}">
                                <span class="chip-small"><c:out value="${genre.name}"/></span>
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
                    <span id="review-card-title"><c:out value="${review.title}"/></span>
                    <span id="review-card-content"><c:out value="${review.content}"/></span>
                    <span id="review-card-date"><c:out value="${review.createdFormatted}"/></span>
                </div>
                <div class="divider-h"></div>
                <div class="review-card-footer">
                <span id="review-card-bottom-text"> por <span id="review-card-author">@<c:out
                        value="${review.author.username}"/></span>, quien prefiere: </span>
                    <c:forEach var="genre" items="${review.author.preferences}">
                        <span class="chip-small-inverted"><c:out value="${genre.name}"/></span>
                    </c:forEach>
                </div>
            </div>
        </c:forEach>
    </div>
    <div class="divider-v"></div>
    <div>
        <form action="${applyFilters}" class="review-filters-panel">
            <button type="submit" class="btn">Aplicar Filtros</button>
            <span class="review-filters-panel-title">Ordenar por</span>
            <div>
                <c:forEach var="criteria" items="${orderCriteria}">
                    <p>
                        <label>
                            <input name="o-crit" value="${criteria.value}" type="radio" <c:if
                                    test="${selectedOrderCriteria == criteria.value}"> checked </c:if>/>
                            <span><c:out value="${criteria}"/></span>
                        </label>
                    </p>
                </c:forEach>
                <c:forEach var="direction" items="${orderDirections}">
                    <p>
                        <label>
                            <input name="o-dir" value="${direction.value}" type="radio" <c:if
                                    test="${selectedOrderDirection == direction.value}"> checked </c:if>/>
                            <span><c:out value="${direction}"/></span>
                        </label>
                    </p>
                </c:forEach>
            </div>
            <div class="divider-h"></div>
            <span class="review-filters-panel-title">Filtros</span>
            <span class="review-filters-panel-subtitle">Género del juego reseñado</span>
            <c:forEach var="genre" items="${selectedGenres}">
                <p>
                    <label>
                        <input name="f-gen" value="${genre.id}" type="checkbox" class="filled-in" checked/>
                        <span><c:out value="${genre.name}"/></span>
                    </label>
                </p>
            </c:forEach>
            <c:forEach var="genre" items="${unselectedGenres}">
                <p>
                    <label>
                        <input name="f-gen" value="${genre.id}" type="checkbox" class="filled-in"/>
                        <span><c:out value="${genre.name}"/></span>
                    </label>
                </p>
            </c:forEach>
            <span class="review-filters-panel-subtitle">Preferencias del reseñador</span>
            <c:forEach var="genre" items="${selectedPreferences}">
                <p>
                    <label>
                        <input name="f-pref" value="${genre.id}" type="checkbox" class="filled-in" checked/>
                        <span><c:out value="${genre.name}"/></span>
                    </label>
                </p>
            </c:forEach>
            <c:forEach var="genre" items="${unselectedPreferences}">
                <p>
                    <label>
                        <input name="f-pref" value="${genre.id}" type="checkbox" class="filled-in"/>
                        <span><c:out value="${genre.name}"/></span>
                    </label>
                </p>
            </c:forEach>
        </form>
    </div>
</div>
</body>
</html>
