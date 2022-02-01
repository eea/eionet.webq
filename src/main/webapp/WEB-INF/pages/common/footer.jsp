<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div id="pagefoot" style="max-width: none;">
    <%
        String currentUrl = request.getRequestURL().toString();
        if(currentUrl.contains("webformsbdr.eionet.europa.eu")){ %>
            <p><a href="mailto:BDR.helpdesk@eea.europa.eu">E-mail</a> | <a href="mailto:BDR.helpdesk@eea.europa.eu?subject=Feedback%20from%20the%20WebQ2%20website">Feedback</a></p>
      <%}
        else{ %>
            <p><a href="mailto:helpdesk@eionet.europa.eu">E-mail</a> | <a href="mailto:helpdesk@eionet.europa.eu?subject=Feedback%20from%20the%20WebQ2%20website">Feedback</a></p>
      <%}
    %>
    <p><a href="https://www.eea.europa.eu/"><b>European Environment Agency</b></a>
    <br/>Kgs. Nytorv 6, DK-1050 Copenhagen K, Denmark</p>
</div>
