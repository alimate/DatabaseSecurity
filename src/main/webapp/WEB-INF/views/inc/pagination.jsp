<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:if test="${page.totalPages > 1}">
  <nav>
    <ul class="pagination">
      <li class="${page.hasPrevious() ? '' : 'disabled'}">
        <a href="${page.hasPrevious() ? '?page='.concat(page.number).concat('&size=').concat(page.size) : ''}">
          <span aria-hidden="true">&laquo;</span>
        </a>
      </li>
      <c:forEach begin="0" end="${page.totalPages - 1}" step="1" var="number">
        <li class="${number == page.number ? 'active' : ''}">
          <a href="?page=${number + 1}&size=${page.size}">${number + 1}</a>
        </li>
      </c:forEach>
      <li class="${page.hasNext() ? '' : 'disabled'}">
        <a href="${page.hasNext() ? '?page='.concat(page.number + 2).concat('&size=').concat(page.size) : ''}">
          <span aria-hidden="true">&raquo;</span>
        </a>
      </li>
    </ul>
  </nav>
</c:if>