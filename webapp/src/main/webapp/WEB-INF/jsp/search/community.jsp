<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/noUiSlider/14.6.1/nouislider.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/noUiSlider/14.6.1/nouislider.min.js"></script>
    <title><spring:message code="community.title"/></title>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            var clinks = document.querySelectorAll('.criteria-selector');
            var cinputElement = document.getElementById('o-crit');
            var dlinks = document.querySelectorAll('.direction-selector');
            var dinputElement = document.getElementById('o-dir');
            var form = document.getElementById('community-form');

            for (let i = 0; i < dlinks.length; i++) {
                dlinks[i].addEventListener('click', function(event) {
                    dinputElement.value = this.getAttribute('data-direction-id');
                    form.submit();
                });
            }
            for (let i = 0; i < clinks.length; i++) {
                clinks[i].addEventListener('click', function(event) {
                    cinputElement.value = this.getAttribute('data-criteria-id');
                    form.submit();
                });
            }
        });
    </script>
    <style>
        .search-size {
            min-width: 500px;
            width: 60%;
            margin-top: 20px;
        }
    </style>
</head>

<body class="background has-background-black">
<jsp:include page="../static-components/navbar.jsp">
    <jsp:param name="selected" value="community"/>
</jsp:include>

<c:url value="/community" var="searchUsers"/>

<div class="community-container f-column">

    <div class="f-column">
        <form action="${searchUsers}" id="community-form">
            <div class="search-size row">
                <div class="col s12">
                    <div class="f-row">
                        <input name="search" class="z-depth-1-half search-field" type="search" value="${userSearch}"
                               placeholder="<spring:message code="community.user.search.placeholder"/>">
                        <button class="btn-flat button-color white-text" type="submit">
                            <i class="material-icons">search</i>
                        </button>
                    </div>
                </div>
                <div class="col s12 wide-selector">
                    <div class="col s10 f-row f-jc-sbetween">
                        <div class="f-row f-gap-2 f-jc-start f-ai-center">
                            <span class="no-wrap"><spring:message code="order.by"/></span>
                            <c:forEach var="criteria" items="${criteriaOptions}">
                                <a class="criteria-selector selector-option f-row f-ai-center <c:if test="${criteria.value == orderCriteria}"> selected </c:if>" data-criteria-id="${criteria.value}">
                                    <i class="material-icons selector-option-icon">
                                        <c:choose>
                                            <c:when test="${criteria.value == 1}">
                                                people
                                            </c:when>
                                            <c:when test="${criteria.value == 2}">
                                                thumbs_up_down
                                            </c:when>
                                            <c:otherwise>
                                                sports_esports
                                            </c:otherwise>
                                        </c:choose>
                                    </i>
                                    <span class="selector-option-text"><spring:message code="${criteria.localizedNameCode}"/></span>
                                </a>
                            </c:forEach>
                        </div>
                    </div>
                    <div class="col s2 f-row f-gap-2 f-jc-start f-ai-center">
                        <c:forEach var="direction" items="${directionOptions}">
                            <a class="direction-selector selector-option f-row f-ai-center <c:if test="${direction.value == orderDirection}"> selected </c:if>" data-direction-id="${direction.value}">
                                <i class="material-icons selector-option-icon">
                                    <c:choose>
                                        <c:when test="${direction.value == 1}">
                                            arrow_upward
                                        </c:when>
                                        <c:otherwise>
                                            arrow_downward
                                        </c:otherwise>
                                    </c:choose>
                                </i>
                            </a>
                        </c:forEach>
                    </div>
                </div>
            </div>




            <div class="input-field hide">
                <input type="text" id="o-crit" name="o-crit" value="${orderCriteria}" readonly>
            </div>

            <div class="input-field hide">
                <input type="text" id="o-dir" name="o-dir" value="${orderDirection}" readonly>
            </div>


        </form>

        <c:if test="${not empty userSearch}">
            <span class="section-subheader">
                <spring:message code="community.search.results"/>
            </span>
            <c:if test="${!empty searchedUsers.list}">
                <div class="row">
                    <c:forEach var="user" items="${searchedUsers.list}">
                        <div class="col s12 l6 margin-bottom-2 f-row f-jc-center">
                            <c:set var="user" value="${user}" scope="request"/>
                            <c:import url="/WEB-INF/jsp/profile/profile-card.jsp"/>
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
                                <li class="pagination-active"><a href="${queriesToKeepAtPageChange}page=${i}">${i}</a>
                                </li>
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
            </c:if>
            <c:if test="${empty searchedUsers.list}">
                <span class="community-text">
                    <spring:message code="search.noResults"/>
                </span>
            </c:if>
        </c:if>
    </div>


    <c:if test="${empty userSearch}">
        <c:if test="${isLoggedIn}">
            <div class="row full-width height-fit-content">

                <div class="col s12 l6 f-column f-ai-center">
                    <span class="section-subheader">
                        <spring:message code="community.same.preferences"/>
                    </span>
                    <c:if test="${!empty samePreferencesUsers}">
                        <div class="row">
                            <c:forEach var="user" items="${samePreferencesUsers}">
                                <div class="col s12 margin-bottom-2 f-row f-jc-center">
                                    <c:set var="user" value="${user}" scope="request"/>
                                    <c:import url="/WEB-INF/jsp/profile/profile-card.jsp"/>
                                </div>
                            </c:forEach>
                        </div>
                    </c:if>
                    <c:if test="${empty samePreferencesUsers && userSetPreferences}">
                        <span class="community-text">
                            <spring:message code="community.not.found"/>
                        </span>
                    </c:if>
                    <c:if test="${empty samePreferencesUsers && !userSetPreferences}">
                        <span class="community-text">
                            <spring:message code="community.set.preferences.text"/>
                        </span>
                        <a href="<c:url value="/profile/settings/preferences"/>">
                            <span class="community-text"><spring:message
                                    code="community.set.preferences.button"/></span>
                        </a>
                    </c:if>
                </div>
                <div class="col s12 l6 f-column f-ai-center">
                    <span class="section-subheader">
                        <spring:message code="community.same.games"/>
                    </span>
                    <c:if test="${!empty sameGamesUsers}">
                        <div class="row">
                            <c:forEach var="user" items="${sameGamesUsers}">
                                <div class="col s12 margin-bottom-2 f-row f-jc-center">
                                    <c:set var="user" value="${user}" scope="request"/>
                                    <c:import url="/WEB-INF/jsp/profile/profile-card.jsp"/>
                                </div>
                            </c:forEach>
                        </div>
                    </c:if>
                    <c:if test="${empty sameGamesUsers && userHasReviewedAnything}">
                        <span class="community-text">
                            <spring:message code="community.not.found"/>
                        </span>
                    </c:if>
                    <c:if test="${empty sameGamesUsers && !userHasReviewedAnything}">
                        <span class="community-text">
                            <spring:message code="community.review.something.text"/>
                        </span>
                        <a href="<c:url value="/review/submit/search"/>">
                            <span class="community-text"><spring:message
                                    code="community.review.something.button"/></span>
                        </a>
                    </c:if>
                </div>

            </div>
            <div class="divider-h margin-bottom-2"></div>
        </c:if>
        <div class="row full-width height-fit-content">
            <div class="col s12 f-column f-ai-center">
            <span class="section-subheader margin-bottom-2">
                <spring:message code="community.default.users"/>
            </span>
            <div class="row">
                <c:forEach var="user" items="${defaultUsers.list}">
                    <div class="col s12 l6 margin-bottom-2 f-row f-jc-center">
                        <c:set var="user" value="${user}" scope="request"/>
                        <c:import url="/WEB-INF/jsp/profile/profile-card.jsp"/>
                    </div>
                </c:forEach>
            </div>
            </div>
        </div>

    </c:if>


</div>
</body>
</html>
