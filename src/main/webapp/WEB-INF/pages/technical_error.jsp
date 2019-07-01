<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${status == 500}">
    <p>Technical error. Please try again later. If error repeats, please contact support.</p>
</c:if>
<div class="error-msg">Technical info: <c:out value="${status}" /> - <c:out value="${errorMessage}" /></div>