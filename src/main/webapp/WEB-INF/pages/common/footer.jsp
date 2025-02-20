<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div id="pagefoot" style="max-width: none;">
    <%
        String currentUrl = request.getRequestURL().toString();
        if(currentUrl.contains("webformsbdr.eionet.europa.eu")){ %>
            <p><a href="mailto:ServiceDesk@eea.europa.eu?subject=Feedback%20from%20the%20Webforms%20BDR%20website">E-mail | Feedback</a></p>
      <%}
        else{ %>
            <p><a href="mailto:ServiceDesk@eea.europa.eu?subject=Feedback%20from%20the%20Webforms%20website">E-mail | Feedback</a></p>
      <%}
    %>
    <p><a href="https://www.eea.europa.eu/"><b>European Environment Agency</b></a>
    <br/>Kgs. Nytorv 6, DK-1050 Copenhagen K, Denmark</p>
</div>
