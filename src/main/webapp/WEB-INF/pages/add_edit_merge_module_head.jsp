<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript" src="<c:url value="/js/jquery-1.10.2.min.js"/>"></script>

<script type="text/javascript">
    $(function() {
        function findAllSchemas() {
            return $('#xmlSchemas').find('input');
        }

        function createNameWithIndex(i) {
            return "xmlSchemas[" + i + "].xmlSchema";
        }

        $("#addXmlSchema").click(function () {
            var allSchemas = findAllSchemas();
            var lastElement = allSchemas.last().parent("div");
            var newElement = lastElement.clone(true);

            newElement.children("input").val("")
                    .removeAttr("id")
                    .attr("name", createNameWithIndex(allSchemas.length));
            lastElement.after(newElement);
        });

        $(".removeSchema").click(function() {
            $(this).parent("div").remove();
        });

        $("form#saveModule").submit(function() {
            findAllSchemas().each(function (i, elem) {
                $(elem).attr("name", createNameWithIndex(i))
                        .removeAttr("id");
            });
        });
    });
</script>