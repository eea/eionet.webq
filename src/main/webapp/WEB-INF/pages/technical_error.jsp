<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<p>Something went wrong… </p>

<div class="error-msg">Technical info: <c:out value="${status}" /> - <c:out value="${errorMessage}" /></div>

<p>The error has been logged and it will be addressed by the development team. For assistance please contact the <a href="mailto:helpdesk@eionet.europa.eu">Eionet helpdesk</a></p>