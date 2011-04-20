<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<title>DBDroid Remoting - Contact Manager</title>
	<style type="text/css">
		body {
			font-family: sans-serif;
		}
		.data {
			border-collapse: collapse;
			width: 80%;
			border: 1px solid #aaa;
			margin: 2px;
			padding: 2px;
		}
		.data td {
			border: 1px solid #aaa;
			margin: 2px;
			padding: 2px;
		}
		.data th {
			border: 1px solid #aaa;
			font-weight: bold;
			background-color: #5C82FF;
			color: white;
		}
	</style>
</head>
<body>

<h2>Contact Manager</h2>

<form:form method="post" commandName="contact">
	<form:hidden path="id" />
	<table>
	<tr>
		<td><form:label path="firstname"><spring:message code="label.firstname"/></form:label></td>
		<td><form:input path="firstname" /></td> 
	</tr>
	<tr>
		<td><form:label path="lastname"><spring:message code="label.lastname"/></form:label></td>
		<td><form:input path="lastname" /></td>
	</tr>
	<tr>
		<td><form:label path="email"><spring:message code="label.email"/></form:label></td>
		<td><form:input path="email" /></td>
	</tr>
	<tr>
		<td><form:label path="telephone"><spring:message code="label.telephone"/></form:label></td>
		<td><form:input path="telephone" /></td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="submit" value="<spring:message code="label.updatecontact"/>"/>
		</td>
	</tr>
</table>	
</form:form>

</body>
</html>
