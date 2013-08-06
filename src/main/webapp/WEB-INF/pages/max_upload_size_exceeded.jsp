<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="error-msg">Maximum size of file exceeded. Allowed size is ${exception.maxUploadSize / 1024 / 1024} megabytes</div>

<a href="<c:url value="/"/>">Back to upload page</a>