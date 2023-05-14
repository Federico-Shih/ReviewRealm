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
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/noUiSlider/14.6.1/nouislider.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/noUiSlider/14.6.1/nouislider.min.js"></script>
    <title><spring:message code="review.list.title"/></title>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            var gelem = document.getElementById('genres-collapsible');
            var ginstance = M.Collapsible.init(gelem, {accordion: false});
            <c:if test="${fn:length(genresFilter.selected) > 0}">
            ginstance.open(0);
            </c:if>
            var pelem = document.getElementById('preferences-collapsible');
            var pinstance = M.Collapsible.init(pelem, {accordion: false});
            <c:if test="${fn:length(preferencesFilter.selected) > 0}">
            pinstance.open(0);
            </c:if>
            var lelem = document.getElementById('platforms-collapsible');
            var linstance = M.Collapsible.init(lelem, {accordion: false});
            <c:if test="${fn:length(platformsFilter.selected) > 0}">
            linstance.open(0);
            </c:if>
            var delem = document.getElementById('difficulties-collapsible');
            var dinstance = M.Collapsible.init(delem, {accordion: false});
            <c:if test="${fn:length(difficultiesFilter.selected) > 0}">
            dinstance.open(0);
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

            preferencesButton = document.getElementById('auto-select-preferences-button');
            preferencesButton.addEventListener('click', function () {
                const userPreferences = ${userPreferences};
                const checkboxes = document.getElementsByName('f-prf');
                for (let i = 0; i < checkboxes.length; i++) {
                    const checkbox = checkboxes[i];
                    checkbox.checked = !!userPreferences.includes(parseInt(checkbox.value));
                }
            });
            </c:if>
        });


    </script>
    <script src="<c:url value="/js/reviewfeedback.js" />"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            var rangeSlider = document.getElementById('range-slider');
            var sliderValues = document.getElementById('f-tpl');

            const min = 0;
            const max = 1000;

            noUiSlider.create(rangeSlider, {
                start: ${minTimePlayed},
                connect: 'upper',
                step: 1,
                orientation: 'horizontal',
                range: {
                    'min': min,
                    'max': max
                },
                tooltips: true,
            });

            rangeSlider.noUiSlider.on('update', function (values, handle) {
                if (values[0] == min) {
                    sliderValues.value = '';
                } else {
                    sliderValues.value = values[0].toString();
                }
            });
        });
    </script>
</head>
<c:url value="/" var="applyFilters"/>

<body class="background has-background-black">
<jsp:include page="../static-components/navbar.jsp">
    <jsp:param name="selected" value="review-list"/>
</jsp:include>

<div class="review-list-page">
    <div class="left-panel">
        <form action="${applyFilters}" class="review-filters-panel">
            <div class="review-filters-panel-section">
                <button type="submit" class="btn"><spring:message code="apply.filters"/></button>
                <c:if test="${showResetFiltersButton}">
                    <a href="${queriesToKeepAtRemoveFilters}">
                        <button type="button" class="remove-filter-button btn-small blue-grey darken-3 height-fit-content">
                            <i class="material-icons">clear</i>
                            <span><spring:message code="remove.filters"/></span>
                        </button>
                    </a>
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
                    <i class="material-icons">timer</i>
                    <span class="review-filters-panel-subtitle"><spring:message code="review.min.time.played"/></span>
                </div>
                <div class="input-field range-slider">
                    <div class="slider-styled" id="range-slider"></div>
                </div>
                <div class="input-field hide">
                    <label for="f-tpl"></label>
                    <input class="white-text" type="text" id="f-tpl" name="f-tpl" readonly>
                </div>
                <label class="margin-top-2">
                    <input name="f-cpt" type="checkbox" class="filled-in" <c:if
                            test="${completedFilter == true}"> checked </c:if> />
                    <span><spring:message code="review.completed.game"/></span>
                </label>
                <ul id="genres-collapsible" class="collapsible review-filters-collapsible-button">
                    <li>
                        <div class="collapsible-header filters-header f-row f-ai-center">
                            <span class="review-filters-panel-subtitle"><spring:message code="review.genres"/></span>
                            <i class="material-icons right">arrow_drop_down</i>
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
                                        <input name="f-gen" value="${genre.id}" type="checkbox" class="filled-in"
                                               checked/>
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
                <ul id="preferences-collapsible" class="collapsible review-filters-collapsible-button">
                    <li>
                        <div class="collapsible-header filters-header f-row f-ai-center">
                            <span class="review-filters-panel-subtitle"><spring:message
                                    code="review.user.preferences"/></span>
                            <i class="material-icons right">arrow_drop_down</i>
                        </div>
                        <div class="collapsible-body row filters-container">
                            <c:if test="${showFavoritesShortcut}">
                                <div class="col s12">
                                    <button type="button" id="auto-select-preferences-button"
                                            class="margin-top-2 btn-small blue-grey darken-3 height-fit-content">
                                        <i class="material-icons left">favorite</i>
                                        <span><spring:message code="set.favgenres"/></span>
                                    </button>
                                </div>
                            </c:if>
                            <c:forEach var="genre" items="${preferencesFilter.selected}">
                                <p class="col s12 l6">
                                    <label>
                                        <input name="f-prf" value="${genre.id}" type="checkbox" class="filled-in"
                                               checked/>
                                        <span><spring:message code="${genre.name}"/></span>
                                    </label>
                                </p>
                            </c:forEach>
                            <c:forEach var="genre" items="${preferencesFilter.unselected}">
                                <p class="col s12 l6">
                                    <label>
                                        <input name="f-prf" value="${genre.id}" type="checkbox" class="filled-in"/>
                                        <span><spring:message code="${genre.name}"/></span>
                                    </label>
                                </p>
                            </c:forEach>
                        </div>
                    </li>
                </ul>
                <ul id="platforms-collapsible" class="collapsible review-filters-collapsible-button">
                    <li>
                        <div class="collapsible-header filters-header f-row f-ai-center">
                            <span class="review-filters-panel-subtitle"><spring:message code="review.platform"/></span>
                            <i class="material-icons right">arrow_drop_down</i>
                        </div>
                        <div class="collapsible-body row filters-container">
                            <c:forEach var="platform" items="${platformsFilter.selected}">
                                <p class="col s12 l6">
                                    <label>
                                        <input name="f-plt" value="${platform.id}" type="checkbox" class="filled-in"
                                               checked/>
                                        <span><spring:message code="${platform.code}"/></span>
                                    </label>
                                </p>
                            </c:forEach>
                            <c:forEach var="platform" items="${platformsFilter.unselected}">
                                <p class="col s12 l6">
                                    <label>
                                        <input name="f-plt" value="${platform.id}" type="checkbox" class="filled-in"/>
                                        <span><spring:message code="${platform.code}"/></span>
                                    </label>
                                </p>
                            </c:forEach>
                        </div>
                    </li>
                </ul>
                <ul id="difficulties-collapsible" class="collapsible review-filters-collapsible-button">
                    <li>
                        <div class="collapsible-header filters-header f-row f-ai-center">
                            <span class="review-filters-panel-subtitle"><spring:message
                                    code="review.difficulty"/></span>
                            <i class="material-icons right">arrow_drop_down</i>
                        </div>
                        <div class="collapsible-body row filters-container">
                            <c:forEach var="difficulty" items="${difficultiesFilter.selected}">
                                <p class="col s12 l6">
                                    <label>
                                        <input name="f-dif" value="${difficulty.id}" type="checkbox" class="filled-in"
                                               checked/>
                                        <span><spring:message code="${difficulty.code}"/></span>
                                    </label>
                                </p>
                            </c:forEach>
                            <c:forEach var="difficulty" items="${difficultiesFilter.unselected}">
                                <p class="col s12 l6">
                                    <label>
                                        <input name="f-dif" value="${difficulty.id}" type="checkbox" class="filled-in"/>
                                        <span><spring:message code="${difficulty.code}"/></span>
                                    </label>
                                </p>
                            </c:forEach>
                        </div>
                    </li>
                </ul>
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
                <c:set var="review" value="${review}" scope="request"/>
                <c:import url="/WEB-INF/jsp/review/review-card.jsp"/>
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
</div>
</body>
</html>
