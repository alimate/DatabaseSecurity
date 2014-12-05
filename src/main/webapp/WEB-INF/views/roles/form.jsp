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
            <p class="alert alert-success"><strong><spring:message code="success.role" /></strong></p>
        </c:if>
        <form:form modelAttribute="form">
            <form:errors path="*">
                <p class="alert alert-danger">Please fix following issues.</p>
            </form:errors>
            <spring:bind path="name">
                <div class="form-group">
                    <label form="name">Role Name</label>
                    <form:input path="name" name="name" id="name" autocomplete="off"
                                placeholder="Role Name" cssClass="form-control"/>
                    <c:if test="${status.error}">
                        <ul style="list-style-type: none" class="alert alert-danger">
                            <c:forEach items="${status.errorMessages}" var="error">
                                <li><c:out value="${error}" /></li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </div>
            </spring:bind>
            <spring:bind path="parent">
                <div class="form-group">
                    <label for="parent">Wanna a parent?</label>
                    <form:select path="parent" cssClass="form-control" name="parent" id="parent">
                        <c:choose>
                            <c:when test="${empty form.parent}">
                                <form:option value="" selected="selected">Nope.</form:option>
                            </c:when>
                            <c:otherwise>
                                <form:option value="">Nope.</form:option>
                            </c:otherwise>
                        </c:choose>
                        <c:forEach items="${roles}" var="each">
                            <form:option value="${each}" selected="${each eq form.parent ? 'selected': ''}">${each}</form:option>
                        </c:forEach>
                    </form:select>
                    <c:if test="${status.error}">
                        <ul style="list-style-type: none" class="alert alert-danger">
                            <c:forEach items="${status.errorMessages}" var="error">
                                <li><c:out value="${error}" /></li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </div>
            </spring:bind>
            <spring:bind path="mutex">
                <div class="form-group">
                    <label for="mutex">Select Mutual Roles</label>
                    <form:select path="mutex" multiple="true" cssClass="form-control" name="mutex" id="mutex">
                        <form:option value="" selected="${empty form.mutex ? 'selected' : ''}">I'm ok with everybody.</form:option>
                        <c:forEach items="${roles}" var="each">
                            <form:option value="${each}" selected="${form.mutex.contains(each) ? 'selected' : ''}">${each}</form:option>
                        </c:forEach>
                    </form:select>
                    <c:if test="${status.error}">
                        <ul style="list-style-type: none" class="alert alert-danger">
                            <c:forEach items="${status.errorMessages}" var="error">
                                <li><c:out value="${error}" /></li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </div>
            </spring:bind>
            <spring:bind path="permissions">
                <div class="form-group">
                    <label for="permissions">Add Permissions</label>
                    <form:select path="permissions" multiple="multiple" cssClass="form-control" name="permissions" id="permissions">
                        <form:option value="" selected="${empty form.permissions ? 'selected' : ''}">Irresponsible.</form:option>
                        <c:forEach items="${perms}" var="each">
                            <form:option value="${each}" selected="${form.permissions.contains(each) ? 'selected' : ''}">${each}</form:option>
                        </c:forEach>
                    </form:select>
                    <c:if test="${status.error}">
                        <ul style="list-style-type: none" class="alert alert-danger">
                            <c:forEach items="${status.errorMessages}" var="error">
                                <li><c:out value="${error}" /></li>
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