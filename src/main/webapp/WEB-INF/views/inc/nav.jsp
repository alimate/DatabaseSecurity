<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="navbar navbar-default" role="navigation">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="<c:url value="/"/>">Database Security</a>
        </div>
        <div class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li><a href="<c:url value="/"/>">Home</a></li>
                <security:authorize url="/console" method="GET">
                    <li><a href="<c:url value="/console"/>">Query Console</a></li>
                </security:authorize>
                <security:authorize url="/users" method="GET">
                    <li><a href="<c:url value="/users"/>">Users</a></li>
                </security:authorize>
                <security:authorize url="/roles" method="GET">
                    <li><a href="<c:url value="/roles"/>">Roles</a></li>
                </security:authorize>
                <security:authorize url="/perms" method="GET">
                    <li><a href="<c:url value="/perms"/>">Permissions</a></li>
                </security:authorize>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li>
                    <security:authorize access="isAuthenticated()" var="isAuthenticated"/>
                    <c:choose>
                        <c:when test="${isAuthenticated}">
                            <a href="<c:url value="/logout"/>">Logout</a>
                        </c:when>
                        <c:otherwise>
                            <form class="navbar-form navbar-left" action="<c:url value="/j_spring_security_check"/>"
                                  method="post">
                                <div class="form-group">
                                    <input type="text" class="form-control" placeholder="Username"
                                           name="username" autocomplete="off">
                                    <input type="password" class="form-control" placeholder="Password"
                                           name="password" autocomplete="off">
                                </div>
                                <button type="submit" class="btn btn-default">Login</button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </li>
            </ul>
        </div>
    </div>
</nav>
