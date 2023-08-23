package hello.servlet.basic.response;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "responseHeaderServlet", urlPatterns = "/response-header")
public class ResponseHeaderServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //[status-line]
        response.setStatus(HttpServletResponse.SC_OK); //200

        //[response-header] -> 안 넣은 정보는 톰캣이 알아서 setting
        response.setHeader("Content-type", "text/plain;charset=utf-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); //캐시 완전 무효화
        response.setHeader("Pragma", "no-cache"); //과거 캐시 무효화
        response.setHeader("my-header", "hello"); //내가 원하는 임의의 헤더

        //[Header 편의 메서드]
        content(response);

        //[Cookie 편의 메서드]
        cookie(response);

        //[redirect 편의 메서드]
        redirect(response);

        //message body -> getWriter 꺼내서 데이터 담을 수 있다.
        PrintWriter writer = response.getWriter();
        writer.println("ok");
    }

    private void content(HttpServletResponse response) {
        //Content-Type: text/plain;charset=utf-8
        //Content-Length: 2
        //response.setHeader("Content-Type", "text/plain;charset=utf-8");
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        //response.setContentLength(2); //(생략시 자동 생성)
    }

    private void cookie(HttpServletResponse response) {
        //Set-Cookie: myCookie=good; Max-Age=600;
        //response.setHeader("Set-Cookie", "myCookie=good; Max-Age=600");
        Cookie cookie = new Cookie("myCookie", "good");
        cookie.setMaxAge(600); //600초
        response.addCookie(cookie);
    }

    private void redirect(HttpServletResponse response) throws IOException {
        //Status Code 302
        //Location: /basic/hello-form.html

        //response.setStatus(HttpServletResponse.SC_FOUND); //302 -> redirect 코드
        //response.setHeader("Location", "/basic/hello-form.html"); //이 주소로 redirect 해주세여
        response.sendRedirect("/basic/hello-form.html");
    }
}