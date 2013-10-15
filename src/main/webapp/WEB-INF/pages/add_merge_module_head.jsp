<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript" src="<c:url value="/js/jquery-1.10.2.min.js"/>"></script>

<script type="text/javascript">
    $(function() {
        $("#addXmlSchema").click(function () {
            var allSchemas = $("#xmlSchemas input");
            var lastElement = allSchemas.last();
            var schemasSize = allSchemas.length;
            var newInput = lastElement.clone();

            lastElement.after(newInput);
            newInput.attr("name", newInput.attr("name").replace(schemasSize - 1, schemasSize))
                    .removeAttr("id");
        });
    });
</script>