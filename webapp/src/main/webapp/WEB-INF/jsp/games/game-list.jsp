<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: USUARIO
  Date: 6/4/2023
  Time: 10:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Lista de Juegos</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link href="css/game.css" rel="stylesheet"/>
    <link rel="stylesheet" href="css/main.css">
</head>
<body>
    <jsp:include page="../staticComponents/navbar.jsp"/>
    <div>
        <div class="row">
            <c:forEach items="${games}" var="game">
                <a href="idontknowwhattoputhere.jsp"> <!--TODO: fix href -->
                    <div class="col s12 m6">
                        <div class="card medium z-depth-2">
                            <div class="card-image">
                                <img src="${game.imageUrl}" alt="JugandoConHugo">
                            </div>
                            <div class="card-content">
                                <span class="card-title white-text"><c:out value="${game.name}"/></span>
                                <p class="white-text">Fecha de Publicación: <c:out value="${game.publishDate}"/></p>
                                <p class="white-text">Desarrollador: <c:out value="${game.developer}"/></p>
                                <c:forEach items="${game.genres}" var="genre">
                                    <span class="chip blue lighten-3"><c:out value="${genre.name}"/></span>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                </a>
            </c:forEach>
        </div>
    </div>

    <!-- la otra opción -->
    <div class="row">
        <c:forEach begin="1" end="10">
            <a href="index.jsp">
                <div class="col s12 m6 card">
                    <div class="game-card-for-list">
                        <img class="game-img" src="https://cdn2.unrealengine.com/egs-deadcells-motiontwin-s1-2560x1440-312502186.jpg" alt="JugandoConHugo">
                        <p class="game-card-text">Desarollador: qwe"/></p>
                        <span class="chip blue lighten-3"><c:out value="Rogue-Like"/></span>
                    </div>
                </div>
            </a>
        </c:forEach>
    </div>

</body>
</html>
