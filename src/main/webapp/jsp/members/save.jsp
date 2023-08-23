<%@ page import="hello.servlet.domain.member.Member" %>
<%@ page import="hello.servlet.domain.member.MemberRepository" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  /**
   * request, response 걍 사용 가능 -> 왜? jsp 도 서블릿으로 자동 변환되기 때문이다. (서비스 로직이 그대로 호출된다고 보면 된다.)
   * Servlet이란
   * 웹 기반의 요청에 대한 동적인 처리가 가능한 Server Side에서 돌아가는 Java Program
   * Java 코드 안에 HTML 코드 (하나의 클래스)
   * 웹 개발을 위해 만든 표준

   * JSP란
   * Java 언어를 기반으로 하는 Server Side 스크립트 언어
   * HTML 코드 안에 Java 코드
   * Servlet를 보완하고 기술을 확장한 스크립트 방식 표준
   */

  MemberRepository memberRepository = MemberRepository.getInstance();

  System.out.println("MemberSaveServlet.service");
  String username = request.getParameter("username");
  int age = Integer.parseInt(request.getParameter("age"));

  Member member = new Member(username, age);
  memberRepository.save(member);
%>
<html>
<head>
    <title>Title</title>
</head>
<body>
성공
<ul>
  <li>id=<%=member.getId()%></li>
  <li>username=<%=member.getUsername()%></li>
  <li>age=<%=member.getAge()%></li>
</ul>
<a href="/index.html">메인</a>
</body>
</html>
