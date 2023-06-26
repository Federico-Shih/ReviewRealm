<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
  <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
  <link type="text/css" rel="stylesheet" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
  <link rel="stylesheet" href="<c:url value="/css/main.css" />">
  <link rel="stylesheet" href="<c:url value="/css/game.css" />">
  <link rel="stylesheet" href="<c:url value="/css/review.css" />">
  <script src="<c:url value="/js/materialize.min.js" />"></script>
  <script src="<c:url value="/js/reviewfeedback.js"/>"></script>
  <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
  <title><spring:message code="game.addition.title"/></title>
  <script>
    document.addEventListener('DOMContentLoaded', function() {
      var elems = document.querySelector('.modal');
      var instances = M.Modal.init(elems);
      var button = document.querySelector('.modal-trigger');
      <c:if test="${!(empty modalSearch)}">
        button.click();
      </c:if>
    });
  </script>
</head>

<body>
  <jsp:include page="../static-components/navbar.jsp">
    <jsp:param name="selected" value="gameSubmission"/>
  </jsp:include>
  <div class="container">
    <div class="row">
      <c:if test="${empty suggestedgames}">
        <div class="f-row f-jc-center">
          <h4 class="white-text"><spring:message code="game.submission.empty"/></h4>
        </div>
      </c:if>
      <c:if test="${!(empty suggestedgames)}">
        <div class="f-row f-jc-center f-ai-center">
          <span class="margin-right-2"><spring:message code="game.addition.question"/></span>
          <button class="waves-effect btn modal-trigger" data-target="search-modal">
            <spring:message code="game.addition.search"/>
          </button>
        </div>
      </c:if>
      <c:forEach var="game" items="${suggestedgames}">
        <div class="col s12">
          <div class="game-card-whole-for-list z-depth-2">
            <div>
              <c:url value="${game.imageUrl}" var="imgUrl" />
              <img class="game-img" src="${imgUrl}"
                   alt="<c:out value="${game.name}"/>">
            </div>
            <div class="game-card-details">
              <div class="game-card-whole-details-text">
                <div class="game-card-title white-text">
                  <span><c:out value="${game.name}"/></span>
                </div>
                <div class="game-genres">
                  <c:forEach items="${game.genres}" var="genre" end="1">
                    <span class="chip-small">
                      <spring:message code="${genre.name}"/>
                    </span>
                  </c:forEach>
                </div>
                <span class="game-card-text"><spring:message code="publishing.date"
                                                             arguments="${game.publishDate}"/></span>
                <span class="game-card-text"><spring:message code="developer"
                                                             arguments="${game.developer}"/></span>
                <span class="game-card-text"><c:out value="${game.description}"/> </span>
                <div class="f-row f-jc-center f-ai-center margin-top-2">
                  <div class="form-btns">
                    <form action="<c:url value="/game/submissions/${game.id}/accept"/>" method="post">
                      <button id="btn-accept" class="btn-large" type="submit"><spring:message code="game.submission.accept"/></button>
                    </form>
                    <form action="<c:url value="/game/submissions/${game.id}/reject"/>" method="post">
                      <button id="btn-reject" class="btn-large" type="submit"><spring:message code="game.submission.reject"/></button>
                    </form>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </c:forEach>
    </div>
  </div>
  <div id="search-modal" class="modal">
    <div class="f-jc-center f-column padding-for-container">
      <form action="<c:url value="/game/submissions"/>" method="get">
        <div class="search-list-container full-width">
            <input name="modalSearch" class="z-depth-1-half search-field" type="search" value="${modalSearch}"
                   placeholder="<spring:message code="game.list.placeholder.search"/>">
            <button class="btn-flat button-color white-text" type="submit"><i class="material-icons">search</i>
            </button>
        </div>
      </form>

      <div class="row full-width">
        <c:if test="${!(empty modalSearch)}">
          <c:if test="${empty searchedgames}">
            <div class="f-row f-jc-center">
              <span><spring:message code="game.addition.no.match"/></span>
            </div>
          </c:if>
          <c:forEach var="game" items="${searchedgames}">
            <div class="col s6">
              <div class="game-card-whole-for-list z-depth-2">
                <div>
                  <c:url value="${game.imageUrl}" var="imgUrl" />
                  <img class="game-img-small" src="${imgUrl}"
                       alt="<c:out value="${game.name}"/>">
                </div>
                <div class="game-card-details">
                  <div class="game-card-whole-details-text">
                    <div class="game-card-title white-text">
                      <span class="overflow-ellipsis"><c:out value="${game.name}"/></span>
                    </div>
                    <div class="game-genres">
                      <c:forEach items="${game.genres}" var="genre" end="1">
                        <span class="chip-small">
                          <spring:message code="${genre.name}"/>
                        </span>
                      </c:forEach>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </c:forEach>
        </c:if>
      </div>
    </div>
  </div>
</body>
</html>
