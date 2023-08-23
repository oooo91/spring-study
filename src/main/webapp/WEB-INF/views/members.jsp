<%@ page contentType="text/html;charset=UTF-8" language="java" %> <!--JSP 문서라는 뜻-->
<!--<%@ page import="hello.servlet.domain.member.Member" %> -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> <!-- 반복문 -> jstl 문법 -->
<html>
<head>
  <meta charset="UTF-8">
  <title>Title</title>
</head>
<body>
<a href="/index.html">메인</a>
<table>
  <thead>
  <th>id</th>
  <th>username</th>
  <th>age</th>
  </thead>
  <tbody>
  <c:forEach var="item" items="${members}">
    <tr>
      <td>${item.id}</td>
      <td>${item.username}</td>
      <td>${item.age}</td>
    </tr>
  </c:forEach>

  <!--
  <%
    for (Member member : members) {
      out.write(" <tr>");
      out.write(" <td>" + member.getId() + "</td>");
      out.write(" <td>" + member.getUsername() + "</td>");
      out.write(" <td>" + member.getAge() + "</td>");
      out.write(" </tr>");
    }
  %>
  -->
  </tbody>
</table>
</body>
</html>