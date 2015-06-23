<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript" src="<c:url value="/js/jquery-1.10.2.min.js"/>"></script>

<script type="text/javascript">
    
    $(function() {
        $('#btnSubmitImportProjectsForm').click(function() {
            var msg = 'This action will delete existing project data. Are you sure you want to continue?';
            
            if (!window.confirm(msg)) {
                return;
            }
            
            $('#frmSubmitImportProjects').submit();
        });
    });
    
</script>