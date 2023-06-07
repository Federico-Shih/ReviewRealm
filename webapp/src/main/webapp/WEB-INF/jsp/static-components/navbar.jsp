<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:url value="/css/main.css" var="css" />
<link href="${css}" rel="stylesheet" />
<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
<script>
    document.addEventListener('DOMContentLoaded', function() {
        var elems = document.querySelectorAll('.dropdown-trigger');
        var instances = M.Dropdown.init(elems, {
            alignment: 'right',
            coverTrigger: false,
        });
        var elemsTooltip = document.querySelectorAll('.tooltipped');
        var instancesTooltip = M.Tooltip.init(elemsTooltip, {});
    });
</script>

<c:url value="/review/submit/search" var="submit_review"/>
<div class="fixed-action-btn">
    <a data-position="left" data-tooltip="<spring:message code="review.create" />" class="tooltipped btn-floating btn-large" href="${submit_review}">
        <i class="large material-icons">create</i>
    </a>
</div>
<div class="nav-bar-top">
    <c:url value="/search" var="search_url"/>
    <form action="${search_url}" method="get">
    <nav>
        <div class="navbar">
            <div class="f-row">
                <a href="<c:url value="/"/>" class="navbar-logo">
                    <img class="navbar-logo-image" src="<c:url value="/static/review_realm_logo_white_630px.png"/>" alt="Review Realm Logo">
                    <span class="navbar-logo-title hide-on-med-and-down">Review Realm</span>
                </a>
            </div>
            <div class="navbar-search">
                <div class="input-field  valign-wrapper">
                    <i class="material-icons prefix icon-prefix">search</i>
                    <input
                            name="search"
                            class="z-depth-1-half nav-search-field"
                            type="search"
                            value="${search}"
                            placeholder="<spring:message code="nav.placeholder.search"/>"
                    >
                </div>
            </div>
            <div class="full-width">
                <ul class="navbar-options-list center">
                    <c:if test="${loggedUser != null}">
                        <li class="navbar-option">
                            <a href="<c:url value="/for-you"/>" class="${param.selected == "for-you" ? "chosen-tab" : "" }">
                                <span><spring:message code="navbar.foryou"/></span>
                            </a>
                        </li>
                    </c:if>
                    <li class="navbar-option"><a href="<c:url value="/game/list"/>" class="${param.selected == "game-list" ? "chosen-tab" : "" }"><span><spring:message code="navbar.games"/></span></a></li>
                    <li class="navbar-option"><a href="<c:url value="/"/>" class="${param.selected == "review-list" ? "chosen-tab" : "" }"><span><spring:message code="navbar.reviews"/></span></a></li>

                    <c:if test="${loggedUser != null}">
                        <li class="navbar-dropdown-container">
                            <a class="dropdown-trigger btn navbar-dropdown" href="#" data-target="dropdownProfile">
                                <span class="overflow-ellipsis">
                                    <spring:message code="navbar.welcome" arguments="${loggedUser.username}"/>
                                </span>
                                <i class="material-icons right">arrow_drop_down</i>
                            </a>
                            <ul id="dropdownProfile" class="dropdown-content profile-dropdown">
                                <li>
                                    <a href="<c:url value="/profile/${loggedUser.id}"/>" class="${param.selected == "profile" ? "chosen-tab" : "" }">
                                        <div class="valign-wrapper profile-dropdown-link">
                                            <span class="material-icons small">
                                                account_circle
                                            </span>
                                            <span class="overflow-ellipsis">
                                                <spring:message code="navbar.profile"/>
                                            </span>
                                        </div>
                                    </a>
                                </li>
                                <li>
                                    <a href="<c:url value="/profile/missions"/>">
                                        <div class="valign-wrapper profile-dropdown-link">
                                            <span class="material-icons">
                                                receipt
                                            </span>
                                            <span class="overflow-ellipsis">
                                                <spring:message code="navbar.missions"/>
                                            </span>
                                        </div>
                                    </a>
                                </li>
                                <li>
                                    <a href="<c:url value="/review/submit/search"/>">
                                        <div class="valign-wrapper profile-dropdown-link">
                                            <span class="material-icons">
                                                create
                                            </span>
                                            <span class="overflow-ellipsis">
                                                <spring:message code="navbar.create"/>
                                            </span>
                                        </div>
                                    </a>
                                </li>
                                <li>
                                    <a href="<c:url value="/game/submit"/>">
                                        <div class="valign-wrapper profile-dropdown-link">
                                            <span class="material-icons">
                                                add
                                            </span>
                                            <span class="overflow-ellipsis">
                                                <c:if test="${!roles.moderator}"><spring:message code="navbar.suggest"/></c:if>
                                                <c:if test="${roles.moderator}"><spring:message code="navbar.submitgame"/></c:if>
                                            </span>
                                        </div>
                                    </a>
                                </li>

                        <c:if test="${roles.moderator}">
                                <li>
                                    <a href="<c:url value="/game/submissions" />">
                                        <div class="valign-wrapper profile-dropdown-link">
                                            <span class="material-icons">
                                                playlist_add_check
                                            </span>
                                            <span class="overflow-ellipsis">
                                                <spring:message code="navbar.gamesubmissions"/>
                                            </span>
                                        </div>
                                    </a>
                                </li>
                        </c:if>

                                <li>
                                    <a href="<c:url value="/profile/settings/"/>">
                                        <div class="valign-wrapper profile-dropdown-link">
                                            <span class="material-icons">
                                                settings
                                            </span>
                                            <span class="overflow-ellipsis">
                                                <spring:message code="account.settings"/>
                                            </span>
                                        </div>
                                    </a>
                                </li>
                                <li>
                                    <a href="<c:url value="/logout"/>">
                                        <div class="valign-wrapper profile-dropdown-link">
                                            <span class="material-icons">
                                                logout
                                            </span>
                                            <span class="overflow-ellipsis">
                                                <spring:message code="navbar.logout"/>
                                            </span>
                                        </div>
                                    </a>
                                </li>
                            </ul>
                        </li>
                    </c:if>
                    <c:if test="${loggedUser == null}">
                        <li><a href="<c:url value="/login"/>" class="${param.selected == "login" ? "chosen-tab" : "" }"><span><spring:message code="navbar.login"/></span></a></li>
                        <li><a href="<c:url value="/register"/>" class="${param.selected == "register" ? "chosen-tab" : "" }"><span><spring:message code="navbar.register"/></span></a></li>
                    </c:if>
                </ul>
            </div>
        </div>

    </nav>
    </form>
</div>
