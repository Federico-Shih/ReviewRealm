<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<c:url value="/review/submit/" var="submitEndpoint"/>
<c:url value="/review/submit/search/" var="searchEndpoint"/>
<html>
<head>
    <title><spring:message code="review.submit.search"/></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/game.css" />">
    <link rel="stylesheet" href="<c:url value="/css/review/review-page.css" />">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <script src="<c:url value="/js/materialize.min.js" />"></script>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/static-components/navbar.jsp"/>
<div class="container">
    <div class="row">
        <div class="col s12">
          <span class="search-game-review-header"><spring:message code="review.new"/></span>
        </div>
        <div class="col s12 center-align">
          <form method="get" action="${searchEndpoint}">
            <div class="search-list-container">
              <input name="searchquery" class="z-depth-1-half search-field" type="search" value="${searchField}"
                     placeholder="<spring:message code="game.list.placeholder.search"/>">
              <button class="btn-flat button-color white-text" type="submit">
                <i class="material-icons">search</i>
              </button>
            </div>
          </form>
        </div>
    </div>
    <div class="review-card-list row">
        <c:if test="${empty games}">
            <div class="col s12">
                <span><spring:message code="game.list.notfound"/></span>
            </div>
        </c:if>
        <c:forEach items="${games}" var="game">
            <div class="col s12 l6">
                <a href="${submitEndpoint}${game.id}">
                    <div class="game-card-for-list z-depth-2">
                        <c:url value="${game.imageUrl}" var="imgUrl"/>
                        <img class="game-img" src="${imgUrl}" alt="<c:out value="${game.name}"/>">
                        <div class="game-card-details">
                            <div class="game-card-details-text">
                                <span class="game-card-title"><c:out value="${game.name}"/></span>
                                <div class="game-genres">
                                    <c:forEach items="${game.genres}" var="genre" end="1">
                                        <span class="chip-small">
                                            <spring:message code="${genre.name}"/>
                                        </span>
                                    </c:forEach>
                                    <c:if test="${fn:length(game.genres) > 2}">
                                        <span class="chip-small">
                                            <c:out value="+${fn:length(game.genres) - 2}"/>
                                        </span>
                                    </c:if>
                                </div>
                                <span class="game-card-text"><spring:message code="publishing.date" arguments="${game.publishDate}"/></span>
                                <span class="game-card-text"><spring:message code="developer" arguments="${game.developer}"/></span>
                            </div>
                            <div class="game-card-details-extra">
                                <c:if test="${game.averageRating >0}">
                                    <span class="game-card-details-extra-text"><spring:message
                                            code="game.details.review.statistics.rating"/>
                                    </span>
                                  <span class=game-card-details-extra-number>${game.averageRatingString}
                                            <span class="game-card-details-extra-minor">/10</span>
                                    </span>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </a>
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
</body>
</html>
