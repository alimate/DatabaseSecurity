<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${title}</title>
    <jsp:include page="../inc/assets.jsp"/>
</head>
<body>
<jsp:include page="../inc/nav.jsp"/>
<div class="panel panel-default" style="width: 600px; margin: 15px auto;">
    <div class="panel-heading">
        <h3>${title}</h3>
    </div>
    <div class="panel-body">
        <c:if test="${isSucceed}">
            <p class="alert alert-success"><strong><spring:message code="success.user"/></strong></p>
        </c:if>
        <form:form modelAttribute="userForm">
            <form:errors path="*">
                <p class="alert alert-danger">Please fix the following errors</p>
            </form:errors>

            <spring:bind path="username">
                <div class="form-group">
                    <c:choose>
                        <c:when test="${isEditForm}">
                            <form:hidden path="username" name="username" id="username"
                                        cssClass="form-control" />
                        </c:when>
                        <c:otherwise>
                            <label for="username">Username</label>
                            <form:input path="username" name="username" id="username"
                                        cssClass="form-control" />
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${status.error and !isEditForm}">
                        <ul class="alert alert-danger">
                            <c:forEach items="${status.errorMessages}" var="error">
                                <li style="list-style-type: none">${error}</li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </div>
            </spring:bind>
            <spring:bind path="password">
                <div class="form-group">
                    <label for="password">Password</label>
                    <form:password path="password" name="password" id="password" cssClass="form-control"/>
                    <c:if test="${status.error}">
                        <ul class="alert alert-danger">
                            <c:forEach items="${status.errorMessages}" var="error">
                                <li style="list-style-type: none">${error}</li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </div>
            </spring:bind>
            <spring:bind path="passwordRetype">
                <div class="form-group">
                    <label for="passwordRetype">Retype Password</label>
                    <form:password path="passwordRetype" name="passwordRetype" id="passwordRetype"
                                   cssClass="form-control"/>
                    <c:if test="${status.error}">
                        <ul class="alert alert-danger">
                            <c:forEach items="${status.errorMessages}" var="error">
                                <li style="list-style-type: none">${error}</li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </div>
            </spring:bind>
            <div class="form-group">
                <label for="firstName">First Name</label>
                <form:input path="firstName" name="firstName" id="firstName" cssClass="form-control"/>
            </div>
            <div class="form-group">
                <label for="lastName">Last Name</label>
                <form:input path="lastName" name="lastName" id="lastName" cssClass="form-control"/>
            </div>
            <spring:bind path="email">
                <div class="form-group">
                    <c:choose>
                        <c:when test="${isEditForm}">
                            <form:hidden path="email" name="email" id="email"
                                        cssClass="form-control"/>
                        </c:when>
                        <c:otherwise>
                            <label for="email">Email</label>
                            <form:input path="email" name="email" id="email"
                                        cssClass="form-control"/>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${status.error and !isEditForm}">
                        <ul class="alert alert-danger">
                            <c:forEach items="${status.errorMessages}" var="error">
                                <li style="list-style-type: none">${error}</li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </div>
            </spring:bind>
            <spring:bind path="roles">
                <div class="form-group">
                    <label for="roles">Assign Roles</label>
                    <form:select path="roles" multiple="true" cssClass="form-control">
                        <form:option value=""
                                     selected="${empty userForm.roles ? 'selected' : ''}">No Role.</form:option>
                        <c:forEach items="${roles}" var="each">
                            <form:option value="${each.name}">${each.name}</form:option>
                        </c:forEach>
                    </form:select>
                    <c:if test="${status.error}">
                        <ul class="alert alert-danger">
                            <c:forEach items="${status.errorMessages}" var="error">
                                <li style="list-style-type: none">${error}</li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </div>
            </spring:bind>
            <input type="submit" class="btn btn-default" value="${title}">
        </form:form>
    </div>
</div>
</body>
</html>