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
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/noUiSlider/14.6.1/nouislider.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/noUiSlider/14.6.1/nouislider.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            var elems = document.querySelector('.collapsible');
            var instance = M.Collapsible.init(elems, { accordion: false });
            <c:if test="${fn:length(genresFilter.selected) > 0}">
                instance.open(0);
            </c:if>
            <c:if test="${showFavoritesShortcut}">
            genresButton = document.getElementById('auto-select-genres-button');
            genresButton.addEventListener('click', function () {
                const userPreferences = ${userPreferences};
                const checkboxes = document.getElementsByName('f-gen');
                for (let i = 0; i < checkboxes.length; i++) {
                    const checkbox = checkboxes[i];
                    checkbox.checked = !!userPreferences.includes(parseInt(checkbox.value));
                }
            });
            </c:if>

        });
        <c:if test="${created}">
        document.addEventListener('DOMContentLoaded', function () {
            M.toast({html: '<spring:message code="game.suggested" />', classes: 'created-toast'});
        });
        </c:if>
    </script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            var rangeSlider = document.getElementById('range-slider');
            var sliderValues = document.getElementById('f-rat');

            const start = [ ${minRating}, ${maxRating} ];
            const min = 1;
            const max = 10;

            noUiSlider.create(rangeSlider, {
                start: start,
                connect: true,
                step: 0.1,
                orientation: 'horizontal',
                range: {
                    'min': min,
                    'max': max
                },
                tooltips: true,
            });

            rangeSlider.noUiSlider.on('update', function(values, handle) {
                if(values[0] == min && values[1] == max) {
                    sliderValues.value = '';
                }
                else {
                    sliderValues.value = values.join('t');
                }
            });
        });
    </script>
</head>
<body>
<jsp:include page="../static-components/navbar.jsp">
    <jsp:param name="selected" value="game-list"/>
</jsp:include>
<c:url value="/game/list/" var="queryFinish"/>
<div>
    <form action="${queryFinish}" class="review-list-page">
        <div class="left-panel">
            <div>
                <div class="review-filters-panel-section">
                    <button type="submit" class="btn truncate"><spring:message code="apply.filters"/></button>
                    <c:if test="${showResetFiltersButton}">
                        <a href="${queriesToKeepAtRemoveFilters}"><button type="button" class="remove-filter-button btn-small blue-grey darken-3 height-fit-content">
                            <i class="material-icons">clear</i>
                            <span><spring:message code="remove.filters"/></span>
                        </button></a>
                    </c:if>
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
                    <div class="f-row f-ai-center f-gap-2 full-width">
                        <i class="material-icons">star</i>
                        <span class="review-filters-panel-subtitle"><spring:message code="game.details.review.statistics.rating"/></span>
                    </div>
                    <div class="input-field range-slider">
                        <div class="slider-styled" id="range-slider"></div>
                    </div>
                    <div class="input-field hide">
                        <label for="f-rat"></label>
                        <input class="white-text" type="text" id="f-rat" name="f-rat" readonly>
                    </div>

                    <ul class="collapsible full-width">
                        <li class="full-width">
                            <div class="collapsible-header filters-header">
                                <i class="material-icons">videogame_asset</i>
                                <span class="review-filters-panel-subtitle"><spring:message code="review.genres"/></span>
                                <i class="material-icons right dropdown-arrow">arrow_drop_down</i>
                            </div>
                            <div class="collapsible-body row filters-container">
                                <c:if test="${showFavoritesShortcut}">
                                    <div class="col s12">
                                        <button type="button" id="auto-select-genres-button"
                                                class="margin-top-2 btn-small blue-grey darken-3 height-fit-content">
                                            <i class="material-icons left">favorite</i>
                                            <span><spring:message code="set.favgenres"/></span>
                                        </button>
                                    </div>
                                </c:if>
                                <c:forEach var="genre" items="${genresFilter.selected}">
                                    <p class="col s12 l6">
                                        <label>
                                            <input name="f-gen" value="${genre.id}" type="checkbox" class="filled-in" checked/>
                                            <span><spring:message code="${genre.name}"/></span>
                                        </label>
                                    </p>
                                </c:forEach>
                                <c:forEach var="genre" items="${genresFilter.unselected}">
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
                </div>
            </div>
        </div>
        <div>
            <div class="divider-v" id="filter-panel-divider"></div>
        </div>
        <div class="right-panel">
            <div class="search-list-container">
                <input name="search" class="z-depth-1-half search-field" type="search" value="${searchField}"
                       placeholder="<spring:message code="game.list.placeholder.search"/>">
                <button class="btn-flat button-color white-text" type="submit"><i class="material-icons">search</i>
                </button>
            </div>
            <div class="review-card-list row">
                <c:if test="${empty games}">
                    <div class="col s12">
                        <span><spring:message code="game.list.notfound"/></span>
                    </div>
                </c:if>
                <c:forEach items="${games}" var="game">
                    <div class="col s12 l6">
                        <div>
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
                                        <div class="game-genres">
                                            <c:forEach items="${game.genres}" var="genre" end="1">
                                                <a href="${queriesToKeepAtRemoveFilters}&f-gen=${genre.id}" class="white-text">
                                                <span class="chip-small">
                                                    <spring:message code="${genre.name}"/>
                                                </span>
                                                </a>
                                            </c:forEach>
                                            <c:if test="${fn:length(game.genres) > 2}">
                                                <span class="chip-small">
                                                    <c:out value="+${fn:length(game.genres) - 2}"/>
                                                </span>
                                            </c:if>
                                        </div>
                                        <span class="game-card-text"><spring:message code="publishing.date"
                                                                                     arguments="${game.publishDate}"/></span>
                                        <span class="game-card-text"><spring:message code="developer"
                                                                                     arguments="${game.developer}"/></span>
                                    </div>
                                    <div class="game-card-details-extra">

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
                    </div>
                </c:forEach>
            </div>
            <div class="row">
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
    </form>
</div>

</body>
</html>
