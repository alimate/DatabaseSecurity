<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
            <p class="alert alert-success"><strong><spring:message code="success.perm"/></strong></p>
        </c:if>
        <form:form modelAttribute="permForm">
            <form:errors path="*">
                <p class="alert alert-danger">Please fix following issues.</p>
            </form:errors>
            <spring:bind path="name">
                <div class="form-group">
                    <label form="name">Permission Name</label>
                    <form:input path="name" name="name" id="name" autocomplete="off"
                                placeholder="Permission Name" cssClass="form-control"/>
                    <c:if test="${status.error}">
                        <ul style="list-style-type: none" class="alert alert-danger">
                            <c:forEach items="${status.errorMessages}" var="error">
                                <li><c:out value="${error}"/></li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </div>
                <input type="submit" class="btn btn-default" value="${title}">
            </spring:bind>
        </form:form>
    </div>
</div>
</body>
</html>