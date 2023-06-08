<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/for-you.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review.css" />">
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <script src="<c:url value="/js/reviewfeedback.js"/> "></script>
    <title><spring:message code="navbar.foryou"/></title>
    <script>
        <c:if test="${endOfQueue}">
            document.addEventListener('DOMContentLoaded', function () {
                M.toast({html: '<spring:message code="for-you.outofrecommended" />', classes: 'created-toast'});
            });
        </c:if>
    </script>

</head>
<body>
<jsp:include page="../static-components/navbar.jsp">
    <jsp:param name="selected" value="for-you"/>
</jsp:include>
<c:url var="setPreferences" value="/profile/settings/preferences"/>
<c:url var="searchUsers" value="/"/>
<c:url value="/for-you/" var="sendSearch"/>
<c:url value="/for-you/discovery" var="discovery"/>
<div class="for-you-page">
    <div class="for-you-section">
        <div class="full-width row">
            <c:if test="${!userSetPreferences}">
                <div class="card lime darken-3">
                    <div class="card-content white-text f-row f-gap-2">
                        <div class="">
                            <i class="material-icons medium">warning</i>
                        </div>
                        <div class="">
                            <span class="card-title"><spring:message code="for-you.nopreferencesset" /></span>
                            <a href="<c:url value="/profile/settings/preferences"/>" class="no-a-decoration btn-flat waves-effect waves-light border-button f-row f-jc-center f-ai-center">
                                <span><spring:message code="for-you.nopreferencesset.doit"/></span>
                            </a>
                        </div>
                    </div>
                </div>
            </c:if>
            <c:if test="${userSetPreferences}">
                <div class="card blue-grey darken-4">
                    <div class="card-content white-text f-row f-gap-2">
                        <div class="">
                            <i class="material-icons medium">videogame_asset</i>
                        </div>
                        <div class="">
                            <span class="card-title"><spring:message code="for-you.discovery" /></span>
                            <a href="<c:url value="/for-you/discovery"/>" class="no-a-decoration btn-flat waves-effect waves-light border-button f-row f-jc-center f-ai-center">
                                <span><spring:message code="for-you.discovery.btn"/></span>
                            </a>
                        </div>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
<%--    <div class="for-you-section">--%>
<%--        <span class="for-you-section-header"><spring:message code="for-you.search.users"/></span>--%>
<%--        <form action="${sendSearch}" class="entire-width">--%>
<%--            <div class="search-section">--%>
<%--                <div class="search-game-list">--%>
<%--                    <input name="search" class="z-depth-1-half search-field" type="search" value="${search}"--%>
<%--                           placeholder="<spring:message code="for-you.search-placeholder"/>">--%>
<%--                    <button class="btn-flat button-color white-text" type="submit"><i class="material-icons">search</i>--%>
<%--                    </button>--%>
<%--                    <input type="hidden" name="page" value="${currentPage}">--%>
<%--                    <input type="hidden" name="pagesize" value="${pagesize}">--%>
<%--                </div>--%>
<%--            </div>--%>
<%--        </form>--%>
<%--        <c:if test="${not empty search}">--%>
<%--            <div class="container row">--%>
<%--                <c:forEach var="user" items="${users}">--%>
<%--                    <div class="col s5 card user-card-small">--%>
<%--                        <a href="<c:url value="/profile/${user.id}"/> ">--%>
<%--                            <div class="card-content">--%>
<%--                                <div class="card-title">--%>
<%--                                    <div class="align-icon-text">--%>
<%--                                        <i class="material-icons">person</i>--%>
<%--                                        <span><c:out value="${user.username}" /></span>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </a>--%>
<%--                    </div>--%>
<%--                </c:forEach>--%>
<%--            </div>--%>
<%--        </c:if>--%>
<%--    </div>--%>
    <div class="for-you-section">
        <span class="for-you-section-header"><spring:message code="${contentTabHeaderCode}"/></span>
        <div class="full-width row">
            <div class="col s12">
                <div class="card indigo darken-4">
                      <div class="card-content f-row f-jc-start f-ai-baseline f-gap-2">
                          <a href="<c:url value="/for-you/?content=FOLLOWING"/>" class="white-text">
                              <div class="chip-content-tab f-row f-ai-end f-jc-sbetween ${(contentTab == "FOLLOWING")? 'active-chip-tab':''}">
                                    <i class="material-icons">groups</i>
                                    <span class="chip-content-padding"><spring:message code="for-you.reviews.following.chip"/></span>
                              </div>
                          </a>
                          <a href="<c:url value="/for-you/?content=RECOMMENDED"/>" class="white-text">
                              <div class="chip-content-tab  f-row f-ai-end f-jc-sbetween ${(contentTab == "RECOMMENDED")? 'active-chip-tab':''}">
                                    <i class="material-icons">local_fire_department</i>
                                    <span class="chip-content-padding"><spring:message code="for-you.reviews.recommended.chip"/></span>
                              </div>
                          </a>
                          <a href="<c:url value="/for-you/?content=NEW"/>" class="white-text">
                              <div class="chip-content-tab f-row f-ai-end f-jc-sbetween ${(contentTab == "NEW")? 'active-chip-tab':''}">
                                    <i class="material-icons">newspaper</i>
                                    <span class="chip-content-padding"><spring:message code="for-you.reviews.new.chip"/></span>
                              </div>
                          </a>
                      </div>
                </div>
            </div>
        </div>
        <div class="review-card-list row">
            <c:if test="${empty reviews}">
                <div class="s12 col center">
                    <span> <spring:message code="${contentNotFoundCode}"/></span>
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
