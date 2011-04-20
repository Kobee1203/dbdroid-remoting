<%@ page language="java" %>
<% String welcomeURL = application.getInitParameter("welcomeURL");
   response.sendRedirect(welcomeURL);
%>