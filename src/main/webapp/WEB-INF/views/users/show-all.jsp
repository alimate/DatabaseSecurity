<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>View Users</title>
    <jsp:include page="../inc/assets.jsp"/>
</head>
<body>
<jsp:include page="../inc/nav.jsp"/>
<div class="panel panel-default" style="width: 800px; margin: 15px auto;">
    <div class="panel-heading">
        <h3 style="margin-bottom: 30px">
            List of Users
            <security:authorize url="/users/add">
                <a href="<c:url value="/users/add"/>" class="btn btn-success" style="float: right">
                    Add a new user
                </a>
            </security:authorize>
        </h3>
    </div>
    <div class="panel-body">
        <c:if test="${not empty message}"><p class="alert alert-success"><strong>${message}</strong>
        </p></c:if>
        <c:if test="${page.content.size() > 0}">
            <table class="table table-hover">
                <thead>
                <tr>
                    <th>#</th>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Full Name</th>
                    <th>Email</th>
                    <th>Details</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${page.content}" var="each" varStatus="status">
                    <tr>
                        <td>${(page.number * page.size) + status.index + 1}</td>
                        <td>${each.id}</td>
                        <td>${each.username}</td>
                        <td>${each.fullName}</td>
                        <td>${each.email}</td>
                        <td><a href="<c:url value="/users/${each.id}"/>">more</a></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <jsp:include page="../inc/pagination.jsp">
                <jsp:param name="page" value="${page}"/>
            </jsp:include>
        </c:if>
    </div>
</div>
</body>
</html>