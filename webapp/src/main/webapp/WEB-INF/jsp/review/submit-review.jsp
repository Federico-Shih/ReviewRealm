<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Crear reseña</title> <!-- Compiled and minified CSS -->
    <link rel="stylesheet" type="text/css" href="../css/materialize.min.css" media="screen,projection"/>
    <link rel="stylesheet" href="../css/main.css">
    <link rel="stylesheet" href="../css/review/review-page.css">
    <!-- Compiled and minified JavaScript -->
    <script src="../js/materialize.min.js"></script>
    <link rel="shortcut icon" type="image/png" href="../static/review_realm_logo_white_32px.png">
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
</head>

<c:url value="/review/submit/${game.id}" var="submitEndpoint"/>
<body>
<jsp:include page="/WEB-INF/jsp/static-components/navbar.jsp"/>
<div class="container">
    <div class="row">
        <div class="col s8">
            <div class="card card-background">
                <form action="${submitEndpoint}" method="post">
                    <div class="card-content">
                        <div class="row valign-wrapper">
                            <div class="card-title col s10">Review sobre <c:out value="${game.name}"/></div>
                            <div class="col s2 rating-input valign-wrapper">
                                <label for="review-rating"></label>
                                <div class="input-field number-input">
                                    <input
                                            name="review-rating"
                                            id="review-rating"
                                            type="number"
                                            class="white-text"
                                            placeholder="10"
                                    />
                                </div>
                                <div>/10</div>
                            </div>
                        </div>
                        <div class="line"></div>
                        <div class="input-field">
                            <label for="review-title"></label>
                            <input
                                    name="review-title"
                                    id="review-title"
                                    placeholder="Danos un titulo llamativo..."
                                    type="text"
                                    class="input-general"
                                    style="color: black;"
                            >
                        </div>
                        <div class="input-field">
                            <label for="review-content"></label>
                            <textarea
                                    placeholder="Contanos que te parecio..."
                                    name="review-content"
                                    id="review-content"
                                    class="materialize-textarea review-content-input input-general"
                                    style="color: black;"></textarea>
                        </div>
                        <div class="input-field">
                            <label for="review-author">Autor:</label>
                            <input id="review-author" name="review-author" placeholder="Fulanito" type="text"
                                   class="input-general" style="color: black;">
                        </div>
                        <div class="row">
                            <button class="waves-effect waves-light btn submit-btn s2 offset-s9 col" type="submit">Crear
                                resena
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        <div class="col s4">
            <div class="card card-background">
                <div class="card-content">
                    <div>
                        <img src="${game.imageUrl}" alt="game-image" class="game-image"/>
                    </div>
                    <h5><c:out value="${game.name}"/></h5>
                    <div>
                        <span>Categorias: </span>
                        <c:forEach var="genre" items="${game.genres}">
                            <span class="chip blue-grey lighten-3 "><c:out value="${genre.name}"/> </span>
                        </c:forEach>
                    </div>
                    <div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
