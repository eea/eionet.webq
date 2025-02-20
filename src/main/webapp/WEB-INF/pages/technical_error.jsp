<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<p>Something went wrong… </p>

<div class="error-msg">Technical info: <c:out value="${status}" /> - <c:out value="${errorMessage}" /></div>

<%
    String currentUrl = request.getRequestURL().toString();
    if(currentUrl.contains("webformsbdr.eionet.europa.eu")){ %>
        <p>The error has been logged and it will be addressed by the development team. For assistance please contact the <a href="mailto:ServiceDesk@eea.europa.eu?subject=Technical%20assistance%20from%20the%20Webforms%20BDR%20website">Eionet Service Desk</a></p>
    <%}
    else{ %>
        <p>The error has been logged and it will be addressed by the development team. For assistance please contact the <a href="mailto:ServiceDesk@eea.europa.eu?subject=Technical%20assistance%20from%20the%20Webforms%20website">Eionet Service Desk</a></p>
    <%}
%>
