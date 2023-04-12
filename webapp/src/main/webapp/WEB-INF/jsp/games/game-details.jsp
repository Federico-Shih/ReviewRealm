<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="../css/materialize.min.css" media="screen,projection"/>
    <link rel="stylesheet" href="../css/main.css">
    <link rel="stylesheet" href="../css/game.css">
    <link rel="stylesheet" href="../css/review.css">
    <script src="../js/materialize.min.js"></script>
    <link rel="shortcut icon" type="image/png" href="../static/review_realm_logo_white_32px.png">
    <title>Details of <c:out value="${game.name}"/></title> <!--TODO-->
</head>
<body>
<jsp:include page="../static-components/navbar.jsp"/>
<div class="game-details-section">
    <div class="game-details">
        <span class="game-title"><c:out value="${game.name}"/></span>
        <div class="game-genres">
            <span class="game-category">Categorias:</span>
            <c:forEach items="${game.genres}" var="genre">
                <div class="chip white-text"><c:out value="${genre.name}"/></div>
            </c:forEach>
        </div>
        <div class="divider"></div>
        <p class="game-description"><c:out value="${game.description}"/></p>
        <div class="divider"></div>
    </div>
    <div class="game-card">
        <img class="game-img" src="${game.imageUrl}" alt="Game image">
        <span class="game-card-text">
                            Fecha de publicacion: <c:out value="${game.publishDate}"/>
                        </span>
        <span class="game-card-text">
                            Desarollador: <c:out value="${game.developer}"/>
                        </span>
        <span class="game-card-text">
                            Editor: <c:out value="${game.publisher}"/>
                        </span>
    </div>
</div>
<div class="game-review-section">
    <div class="game-review-header">
        <span class="game-review-section-header">Reseñas de otros usuarios</span>
    </div>
    <div class="game-review-card-list">
        <c:forEach var="review" items="${reviews}">
            <div class="card review-card">
                <div class="review-card-header">
                    <div class="review-card-header-start">
                        <span id="review-card-title"><c:out value="${review.title}"/></span>
                    </div>
                    <div class="review-card-header-end">
                        <span id="review-card-score"><c:out value="${review.rating}"/></span>
                        <span id="review-card-score-outof">/10</span>
                        <i class="material-icons small">star</i>
                    </div>
                </div>
                <div class="review-card-body">
                    <span id="review-card-content"><c:out value="${review.content}"/></span>
                    <span id="review-card-date"><c:out value="${review.created}"/></span>
                </div>
                <div class="divider"></div>
                <div class="review-card-footer">
                    <span id="review-card-bottom-text"> por <span id="review-card-author">@<c:out
                            value="${review.author.email}"/></span>, quien prefiere: </span>
                        <%--                            <c:forEach var="genre" items="${review.author.}">--%>
                        <%--                                <span class="chip-small-inverted"><c:out value="${genre.name}"/></span>--%>
                        <%--                            </c:forEach>--%>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
</body>
</html>
