<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Permission: ${perm.name}</title>
    <jsp:include page="../inc/assets.jsp"/>
</head>
<body>
<jsp:include page="../inc/nav.jsp"/>
<div class="panel panel-default" style="width: 800px; margin: 15px auto;">
    <div class="panel-heading">
        <h3 style="display: inline; margin-right: 10px">
            <strong>${perm.name}</strong> Details
        </h3>
        <div style="display: inline;float: right;position: relative;top: -4px">
            <button class="btn btn-link">
                <a href="<c:url value="/perms/edit/${perm.id}" />"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Edit</a>
            </button>
            <form action="<c:url value="/perms/remove/${perm.id}" />" method="post" style="display: inline">
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
            <div class="col-md-9"><strong>${perm.id}</strong></div>
        </div>
        <div class="row">
            <div class="col-md-3">Name:</div>
            <div class="col-md-9"><strong>${perm.name}</strong></div>
        </div>
    </div>
</div>
</body>
</html>