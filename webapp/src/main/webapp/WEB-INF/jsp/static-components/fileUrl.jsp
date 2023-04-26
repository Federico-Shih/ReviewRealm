<%--
  Created by IntelliJ IDEA.
  User: franc
  Date: 26/4/2023
  Time: 18:27
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
  <title>FileUpload</title>
<%--  TODO BORRAR--%>
</head>
<c:url value="/images/url" var="upload"/>
<body>
<form:form method="POST" action="${upload}" enctype="multipart/form-data" modelAttribute="UrlForm">
  <table>
    <tr>
      <td><form:label for="url" path="url"> Select a file to upload </form:label></td>
      <td><form:input type="text" id="url" path="url" /></td>
    </tr>
    <tr>
      <td><form:button type="submit" value="Submit"> submit</form:button></td>
    </tr>
  </table>
</form:form>
<h1>${imageUploaded.id}</h1>
<h1>${imageUploaded.mediaType}</h1>
</body>
</html>
