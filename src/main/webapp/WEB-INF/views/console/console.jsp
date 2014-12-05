<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Query Console</title>
    <jsp:include page="../inc/assets.jsp"/>
</head>
<body>
<jsp:include page="../inc/nav.jsp"/>
<div class="row" style="margin: 15px 15px;">
    <div class="col-md-8">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3>Query Console</h3>
            </div>
            <div class="panel-body">
                <form:form modelAttribute="queryForm" id="query-form">
                    <div class="form-group">
                        <spring:bind path="query">
                            <form:textarea path="query" cssClass="form-control" name="query" id="query" rows="5"/>
                            <c:if test="${status.error}">
                                <ul class="alert alert-danger">
                                    <c:forEach items="${status.errorMessages}" var="error">
                                        <li style="list-style-type: none">${error}</li>
                                    </c:forEach>
                                </ul>
                            </c:if>
                        </spring:bind>
                    </div>
                    <input type="submit" value="Run Query" class="btn btn-default">
                </form:form>
                <c:choose>
                    <c:when test="${result.select}">
                        <div style="overflow:auto">
                            <c:if test="${not empty result.columns}">
                                <table class="table table-striped table-bordered table-hover">
                                    <thead>
                                    <tr>
                                        <c:forEach items="${result.columns}" var="column">
                                            <th>${column}</th>
                                        </c:forEach>
                                    </tr>
                                    </thead>
                                    <c:if test="${not empty result.results}">
                                        <tbody>
                                        <c:forEach items="${result.results}" var="row">
                                            <tr>
                                                <c:forEach items="${row}" var="field">
                                                    <td>${empty field ? 'null' : field}</td>
                                                </c:forEach>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </c:if>
                                </table>
                            </c:if>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div style="overflow:auto">
                            <c:if test="${not empty result.message}">
                                <p class="alert alert-success">${result.message}</p>
                            </c:if>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="panel panel-default">
            <div class="panel-heading">
                Schema Browser
            </div>
            <div class="panel-body">
                <div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
                    <c:forEach items="${tables.keySet()}" var="namespace">
                        <div class="panel panel-default">
                            <div class="panel-heading" role="tab" id="${namespace}-heading">
                                <h4 class="panel-title">
                                    <a data-toggle="collapse" data-parent="#accordion" href="#${namespace}"
                                       aria-expanded="false" aria-controls="${namespace}">
                                            ${namespace}
                                    </a>
                                </h4>
                            </div>
                            <div id="${namespace}" class="panel-collapse collapse" role="tabpanel"
                                 aria-labelledby="${namespace}-heading">
                                <div class="panel-body">
                                    <%--inner accordion--%>
                                    <div class="panel-group" id="${namespace}-accordion" role="tablist"
                                         aria-multiselectable="true">
                                        <c:forEach items="${tables.get(namespace)}" var="table">
                                            <div class="panel panel-default">
                                                <div class="panel-heading" role="tab" id="${table.name}-heading">
                                                    <h4 class="panel-title">
                                                        <a data-toggle="collapse" data-parent="#${namespace}-accordion" href="#${table.name}"
                                                           aria-expanded="false" aria-controls="${table.name}">
                                                                ${table.name}
                                                        </a>
                                                    </h4>
                                                </div>
                                                <div id="${table.name}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="${table.name}-heading">
                                                    <div class="panel-body">
                                                        <table class="table table-striped table-hover">
                                                            <thead>
                                                                <tr>
                                                                    <th>
                                                                        Column
                                                                        <a href="${namespace}.${table.name}" class="table-browse" style="float: right">
                                                                            <span class="glyphicon glyphicon-check" aria-hidden="true"></span>
                                                                        </a>
                                                                    </th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <c:forEach items="${table.cols}" var="col">
                                                                    <tr class="warning">
                                                                        <td>${col}</td>
                                                                    </tr>
                                                                </c:forEach>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    <%--inner accordion--%>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function() {
        $("a.table-browse").click(function(event) {
            var fromClause = $(this).attr("href");
            var query = "select * from " + fromClause;
            $("#query").val(query);
            $("#query-form").submit();
            return false;
        });
    });
</script>
</body>
</html>