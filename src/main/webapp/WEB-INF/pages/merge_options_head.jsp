<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript" src="<c:url value="/js/jquery-1.10.2.min.js"/>"></script>

<script type="text/javascript">
$(function() {
    $("form input[type=button]").click(function () {
        $("#mergeModule").val($(this).attr("id"));
        $("#mergeForm").submit();
    });
});
</script>