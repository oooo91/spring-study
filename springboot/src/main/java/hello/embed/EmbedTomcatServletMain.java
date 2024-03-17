package hello.embed;

import hello.servlet.HelloServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

/**
 * 내장 톰캣
 * 내장 톰캣을 사용한 덕분에 IDE에 별도의 복잡한 톰캣 설정 없이 main() 메서드만 실행하면 톰캣까지 매우 편리하게 실행되었다.
 * 물론 톰캣 서버를 설치하지 않아도 된다.
 */
public class EmbedTomcatServletMain {

    public static void main(String[] args) throws LifecycleException {
        System.out.println("EmbedTomcatServletMain.main");

        //톰캣 설정
        Tomcat tomcat = new Tomcat();

        //연결 설정
        Connector connector = new Connector(); //ctrl alt v = 자동 생성
        connector.setPort(8080);
        tomcat.setConnector(connector);

        //서블릿 등록
        Context context = tomcat.addContext("", "/");

        //== 코드 추가 시작==
        File docBaseFile = new File(context.getDocBase());
        if (!docBaseFile.isAbsolute()) {
            docBaseFile = new File(((org.apache.catalina.Host) context.getParent()).getAppBaseFile(), docBaseFile.getPath());

        }
        docBaseFile.mkdirs();
        //== 코드 추가 종료==

        tomcat.addServlet("", "helloServlet", new HelloServlet());
        context.addServletMappingDecoded("/hello-servlet", "helloServlet");
        tomcat.start();
    }
}
