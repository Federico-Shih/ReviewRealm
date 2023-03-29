
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>

<body>
<h2>Register</h2>
<c:url var="registerUrl" value="/register" />
<form action="${registerUrl}" method="post">
  <div>
    <label for="email">Email:</label>
    <input type="text" name="email" id="email"/>
  </div>
  <div>
    <label for="password">Password:</label>
    <input type="password" name="password" id="password"/>
  </div>
  <div>
    <input type="submit" value="Let's fucking GO!">
  </div>
</form>
</body>
</html>
