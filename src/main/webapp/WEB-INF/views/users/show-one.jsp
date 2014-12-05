<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>User: ${user.username}</title>
    <jsp:include page="../inc/assets.jsp"/>
</head>
<body>
<jsp:include page="../inc/nav.jsp"/>
<div class="panel panel-default" style="width: 900px; margin: 15px auto;">
    <div class="panel-heading">
        <h3 style="display: inline; margin-right: 10px">
            <strong>${user.username}</strong> Details
        </h3>
        <div style="display: inline;float: right;position: relative;top: -5px">
            <button class="btn btn-link">
                <a href="<c:url value="/users/edit/${user.id}"/>"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Edit</a>
            </button>
            <form action="<c:url value="/users/remove/${user.id}" />" method="post" style="display: inline">
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
            <div class="col-md-9"><strong>${user.id}</strong></div>
        </div>
        <div class="row">
            <div class="col-md-3">Username:</div>
            <div class="col-md-9"><strong>${user.username}</strong></div>
        </div>
        <div class="row">
            <div class="col-md-3">Full Name:</div>
            <div class="col-md-9"><strong>${user.fullName}</strong></div>
        </div>
        <div class="row">
            <div class="col-md-3">Email:</div>
            <div class="col-md-9"><strong>${user.email  }</strong></div>
        </div>
        <div class="row">
            <div class="col-md-3">Roles:</div>
            <div class="col-md-9">
                <strong>
                    <c:choose>
                        <c:when test="${not empty user.roles}">
                            <ul>
                                <c:forEach items="${user.roles}" var="each">
                                    <li>${each.name}</li>
                                </c:forEach>
                            </ul>
                        </c:when>
                        <c:otherwise>
                            <em>role-less</em>
                        </c:otherwise>
                    </c:choose>
                </strong>
            </div>
        </div>
    </div>
</div>
</body>
</html>