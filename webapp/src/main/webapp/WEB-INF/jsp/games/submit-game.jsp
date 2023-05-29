<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
    <title><spring:message code="game.submit.title" /></title> <!-- Compiled and minified CSS -->
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/materialize.min.css" />" media="screen,projection"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <!-- Compiled and minified JavaScript -->
    <script src="<c:url value="/js/materialize.min.js" />"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/game-submit.css" />" />
    <link href="<c:url value="/css/game.css" />" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="shortcut icon" type="image/png" href="<c:url value="/static/review_realm_logo_white_32px.png" />">
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            var elems = document.querySelectorAll('.collapsible');
            var instances = M.Collapsible.init(elems, {});

            <c:forEach items="${gameForm.genres}" var="genre">
                document.querySelector('#${genre}').setAttribute("checked", "checked");
            </c:forEach>
        });


    </script>
</head>
<!-- general variables
<%--<spring:message code="reviewForm.title.placeholder" var="titlePlaceholder"/>--%>
<%--<spring:message code="reviewForm.content.placeholder" var="contentPlaceholder"/>--%>
<%--<spring:message code="review.author.generic" var="authorPlaceholder"/>--%>
<%--<c:url value="/game/${game.id}" var="gameUrl" />--%>
-->
<body>
<jsp:include page="/WEB-INF/jsp/static-components/navbar.jsp">
    <jsp:param name="selected" value="gameSubmit"/>
</jsp:include>

<div class="container">
    <form:form modelAttribute="gameForm" enctype="multipart/form-data" method="post">
        <div class="card form-text">
            <div class="card-content">
                <div class="card-title">
                    <c:choose>
                        <c:when test="${roles.moderator}">
                            <spring:message code="game.submit.title" />
                        </c:when>
                        <c:otherwise>
                            <spring:message code="game.submit.title.suggest" />
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="row">
                    <div class="input-field col s12">
                        <spring:message code="game.submit.placeholder.title" var="placeholderTitle" />
                        <form:input path="name" id="name" cssClass="white-text" placeholder="${placeholderTitle}" />
                        <form:errors path="name" cssClass="error" element="p" />
                        <form:label for="name" path="name">
                            <spring:message code="game.submit.form.title" />
                        </form:label>
                    </div>
                    <div class="input-field col s12">
                        <spring:message code="game.submit.placeholder.description" var="placeholderDesc" />
                        <form:textarea path="description" id="desc" cssClass="white-text materialize-textarea input-general review-content-input" placeholder="${placeholderDesc}" />
                        <form:errors path="description" cssClass="error" element="p" />
                        <form:label for="desc" path="description">
                            <spring:message code="game.submit.form.description" />
                        </form:label>
                    </div>
                    <div class="input-field col s6">
                        <spring:message code="game.submit.placeholder.developer" var="placeholderDev" />
                        <form:input path="developer" id="dev" cssClass="white-text" placeholder="${placeholderDev}" />
                        <form:errors path="developer" cssClass="error" element="p" />
                        <form:label for="dev" path="developer">
                            <spring:message code="game.submit.form.developer" />
                        </form:label>
                    </div>
                    <div class="input-field col s6">
                        <spring:message code="game.submit.placeholder.publisher" var="placeholderPub" />
                        <form:input path="publisher" id="pub" cssClass="white-text" placeholder="${placeholderPub}" />
                        <form:errors path="publisher" cssClass="error" element="p" />
                        <form:label for="pub" path="publisher">
                            <spring:message code="game.submit.form.publisher" />
                        </form:label>
                    </div>
                    <div class="input-field file-field col s12">
                            <div class="btn" type="">
                                <span><spring:message code="game.submit.form.image" /></span>
                                <form:input type="file" alt="image" id="image" accept="image/gif, image/png, image/jpeg"  path="image"/>
                            </div>

                            <div class="file-path-wrapper">
                                <spring:message code="game.submit.placeholder.image" var="placeholderImage" />
                                <input
                                        class="file-path validate white-text"
                                        type="text"
                                        placeholder="${placeholderImage}"
                                />
                            </div>
                        <form:errors path="image" cssClass="error" element="p" />
                    </div>
                    <div class="col s6 center hide" id="preview-div">
                        <div class="card-for-preview z-depth-2">
                            <img class="preview-image" id="imageShow" src="" alt="Preview"/>
                        </div>
                    </div>
                    <div class="input-field col s12">
                        <ul class="collapsible white-text">
                            <li>
                                <div class="collapsible-header collapsible-btn f-row f-jc-sbetween">
                                    <div class="f-row">
                                        <i class="material-icons">style</i>
                                        <spring:message code="game.submit.form.genres" />
                                    </div>
                                    <i class="material-icons">arrow_drop_down</i>
                                </div>
                                <div class="collapsible-body row no-margin">
                                    <c:forEach items="${genres}" var="genre">
                                        <span class="col s6 l4">
                                            <label>
                                                <input name="genres" id="${genre.id}" type="checkbox" value="${genre.id}" class="filled-in checkbox" />
                                                <span class="no-wrap"><spring:message code="${genre.name}" /></span>
                                            </label>
                                        </span>
                                    </c:forEach>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="card-action">
                <button class="btn" type="submit">
                    <c:choose>
                        <c:when test="${roles.moderator}">
                           <spring:message code="game.submit.create" />
                        </c:when>
                        <c:otherwise>
                            <spring:message code="game.submit.submit.suggestion" />
                        </c:otherwise>
                    </c:choose>
                </button>
            </div>
        </div>
    </form:form>
</div>

<script>
    document.getElementById('image').onchange = function () {
        document.getElementById('imageShow').src = URL.createObjectURL(this.files[0]);
        document.getElementById('preview-div').classList.remove('hide');
    }
</script>

</body>
</html>