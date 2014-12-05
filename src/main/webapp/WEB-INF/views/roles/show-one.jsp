<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Role: ${role.name}</title>
    <jsp:include page="../inc/assets.jsp"/>
</head>
<body>
<jsp:include page="../inc/nav.jsp"/>
<div class="panel panel-default" style="width: 800px; margin: 15px auto;">
    <div class="panel-heading">
        <h3 style="display: inline; margin-right: 10px">
            <strong>${role.name}</strong> Details
        </h3>
        <div style="float: right;display: inline;position: relative; top: -4px">
            <button class="btn btn-link">
                <a href="<c:url value="/roles/edit/${role.id}" />"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Edit</a>
            </button>
            <form action="<c:url value="/roles/remove/${role.id}" />" method="post" style="display: inline">
                <button class="btn btn-link">
                    <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> Remove
                </button>
            </form>
        </div>
    </div>
    <div class="panel-body">
        <c:if test="${not empty message}"><p class="alert alert-danger">${message}</p></c:if>

        <div class="row">
            <div class="col-md-3">ID:</div>
            <div class="col-md-9"><strong>${role.id}</strong></div>
        </div>
        <div class="row">
            <div class="col-md-3">Name:</div>
            <div class="col-md-9"><strong>${role.name}</strong></div>
        </div>
        <div class="row">
            <div class="col-md-3">Parent:</div>
            <div class="col-md-9"><strong>${role.parent == null ? '<em>orphan</em>' : role.parent.name}</strong></div>
        </div>
        <div class="row">
            <div class="col-md-3">Mutually Exclusive Roles:</div>
            <div class="col-md-9">
                <strong>
                    <c:choose>
                        <c:when test="${role.mutexRoles.size() > 0}">
                            <ul>
                                <c:forEach items="${role.mutexRoles}" var="each">
                                    <li>${each.name}</li>
                                </c:forEach>
                            </ul>
                        </c:when>
                        <c:otherwise>
                            <em>no one</em>
                        </c:otherwise>
                    </c:choose>
                </strong>
            </div>
        </div>
        <div class="row">
            <div class="col-md-3">Permissions:</div>
            <div class="col-md-9">
                <strong>
                    <c:choose>
                        <c:when test="${role.permissions.size() > 0}">
                            <ul>
                                <c:forEach items="${role.permissions}" var="each">
                                    <li>${each.name}</li>
                                </c:forEach>
                            </ul>
                        </c:when>
                        <c:otherwise>
                            <em>no responsibility</em>
                        </c:otherwise>
                    </c:choose>
                </strong>
            </div>
        </div>
    </div>
</div>
</body>
</html>