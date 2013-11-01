<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript" src="<c:url value="/js/jquery-1.10.2.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.custom.min.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/jquery-ui-1.10.3.custom.min.css"/>">
<style type="text/css">
    .dialogTemplate {
        display: none;
    }
</style>

<script type="text/javascript">
    function module_view(id) {
        $("#" + id).dialog({
            resizable: false,
            maxHeight: 500,
            width: 800,
            modal: true
        });
    }

    function viewXslSource(downloadLink) {
        $.ajax({
            url: downloadLink,
            dataType: 'text'
        })
        .done(function (data) {
            var xmlAsTextArea = $('<textarea cols="120" rows="30" style="border:none;" readonly="readonly"/>').text(data);
            $('<div title="XSL content"/>').append(xmlAsTextArea).dialog({
                maxHeight: 800,
                width: 1000,
                modal: true
            });
        });
    }
</script>