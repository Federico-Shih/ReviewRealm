<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>
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
</head>
<body>
<jsp:include page="../static-components/navbar.jsp"/>
<div>
    <div class="row">
        <c:if test="${empty games}">
            <div class="col">
                <span><spring:message code="game.list.notfound"/></span>
            </div>
        </c:if>
        <c:forEach items="${games}" var="game">
            <a href="./<c:out value="${game.id}"/>">
                <div class="col s12 m6">
                    <div class="game-card-for-list medium z-depth-2">
                        <img class="game-img" src="<c:out value="${game.imageUrl}"/>"
                             alt="<c:out value="${game.name}"/>">
                        <span class="game-card-title"><c:out value="${game.name}"/></span>
                        <span class="game-card-text"><spring:message code="publishing.date" arguments="${game.publishDate}"/></span>
                        <span class="game-card-text"><spring:message code="developer" arguments="${game.developer}"/></span>
                        <div class="game-genres">
                            <c:forEach items="${game.genres}" var="genre">
                                <span class="chip-small"><spring:message code="${genre.name}"/></span>
                            </c:forEach>
                        </div>

                    </div>
                </div>
            </a>
        </c:forEach>
    </div>
    <ul class="center-align pagination">
        <c:if test="${currentPage > 1}">
            <li class="waves-effect"><a href="?page=${currentPage-1}"><i class="material-icons">chevron_left</i></a></li>
        </c:if>
        <c:forEach var="i" begin="${initialPage}" end="${maxPages}">
            <c:if test="${i == currentPage}">
                <li class="pagination-active"><a href="?page=${i}">${i}</a></li>
            </c:if>
            <c:if test="${i != currentPage}">
                <li class="waves-effect"><a href="?page=${i}">${i}</a></li>
            </c:if>
        </c:forEach >
        <c:if test="${currentPage < maxPages}">
            <li class="waves-effect"><a href="?page=${currentPage+1}"><i class="material-icons">chevron_right</i></a></li>
        </c:if>
    </ul>

</div>

</body>
</html>
