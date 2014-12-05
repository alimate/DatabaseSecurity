<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
    <jsp:include page="/WEB-INF/views/inc/assets.jsp" /> <jsp:include page="/WEB-INF/views/inc/assets.jsp" />
</head>
<body>
    <div id="login-container" class="panel panel-default">
        <c:if test="${param.get('failed')}">
            <p class="alert alert-danger">
                <span class="glyphicon glyphicon-exclamation-sign"></span>
                Specified username/password combo are incorrect. try again
            </p>
        </c:if>
        <c:if test="${param.get('expired')}">
            <p class="alert alert-info">
                <span class="glyphicon glyphicon-exclamation-sign"></span>
                Your session has been expired. to continue using, you can login again
            </p>
        </c:if>
        <form action="<c:url value="/j_spring_security_check"/>" method="post">
            <div class="input-group input-group-lg" style="margin-bottom: 5px">
                <span class="input-group-addon">
                    <span class="glyphicon glyphicon-user"></span>
                </span>
                <input type="text" name="username" id="username" class="form-control"
                       placeholder="Username" autocomplete="off">
            </div>
            <div class="input-group input-group-lg" style="margin-bottom: 5px">
                <span class="input-group-addon">
                    <span class="glyphicon glyphicon-lock" ></span>
                </span>
                <input type="password" name="password" id="password" class="form-control"
                       placeholder="Password" autocomplete="off">
            </div>
            <div class="checkbox">
                <label>
                    <input type="checkbox" name="remember-me" id="remember-me"> Remember Me (for one week)
                </label>
            </div>
            <input type="submit" value="Login" class="btn btn-lg btn-default">
        </form>
    </div>
</body>
</html>