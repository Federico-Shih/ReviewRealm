<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/game.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review.css" />">
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <title><spring:message code="reports.view.title"/></title>
</head>
<body>
    <jsp:include page="../static-components/navbar.jsp">
        <jsp:param name="selected" value="gameSubmission"/>
    </jsp:include>
    <div class="community-container">
        <div class="row">
            <c:if test="${empty reports}">
                <div class="f-row f-jc-center">
                    <h4 class="white-text"><spring:message code="reports.empty"/></h4>
                </div>
            </c:if>
            <c:forEach var="report" items="${reports}">
                <div class="col s12">
                    <div class="col s12 l6">
                        <h5 class="white-text"><spring:message code="${report.reason.code}"/></h5>
                        <c:set var="user" value="${report.reportedUser}" scope="request"/>
                        <div class="f-row f-jc-center">
                            <c:import url="/WEB-INF/jsp/profile/profile-card.jsp"/>
                        </div>
                        <div class="margin-top-3 f-row f-jc-center">
                            <form action="<c:url value="/report/reviews/${report.id}/reject"/>" method="post" class="margin-right-2 margin-left-2">
                                <button class="waves-effect btn" id="btn-accept" type="submit">
                                    <spring:message code="reports.cancel.report"/>
                                </button>
                                <input type="hidden" name="page" value="${currentPage}">
                                <input type="hidden" name="pagesize" value="${pageSize}">
                            </form>

                            <form action="<c:url value="/report/reviews/${report.id}/resolve"/>" method="post">
                                <button class="waves-effect btn" id="btn-reject" type="submit">
                                    <spring:message code="review.delete"/>
                                </button>
                                <input type="hidden" name="page" value="${currentPage}">
                                <input type="hidden" name="pagesize" value="${pageSize}">
                            </form>

                        </div>
                    </div>
                    <c:set var="review" value="${report.reportedReview}" scope="request"/>
                    <c:import url="/WEB-INF/jsp/review/review-card.jsp"/>
                    <div class="divider-h"></div>
                </div>
            </c:forEach>
        </div>
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
</body>
</html>
