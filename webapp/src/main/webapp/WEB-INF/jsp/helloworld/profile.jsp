<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <link href="css/main.css" rel="stylesheet"/>
</head>
<body>
<h2>Hello <c:out value="${userid}" escapeXml="true"/></h2>
</body>
</html>
