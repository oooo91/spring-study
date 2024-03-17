package hello.proxy.config.v1_proxy;

import hello.proxy.app.v1.OrderControllerV1;
import hello.proxy.app.v1.OrderControllerV1Impl;
import hello.proxy.app.v1.OrderRepositoryV1;
import hello.proxy.app.v1.OrderRepositoryV1Impl;
import hello.proxy.app.v1.OrderServiceV1;
import hello.proxy.app.v1.OrderServiceV1Impl;
import hello.proxy.config.v1_proxy.interface_proxy.OrderControllerInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderRepositoryInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderServiceInterfaceProxy;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//프록시 빈으로 등록
@Configuration
public class InterfaceProxyConfig {

	@Bean
	public OrderControllerV1 orderControllerV1(LogTrace logTrace) {

		/**
		 * OrderControllerV1 을 스프링 빈으로 등록을 하면, Controller 프록시 객체가 스프링 컨테이너에 저장된다.
		 * OrderControllerV1 : OrderControllerInterfaceProxy

		 * 클라이언트 -> 컨트롤러 프록시 객체 -> 실제 컨트롤러 객체를 호출할 것이다.
		 * 그렇다면, 실제 컨틀롤러 객체는 스프링 컨테이너에 어떻게 관리되나?
		 * 실제 컨틀로러는 컨테이너와 관련이 " 없다 "
		 * 프록시 객체가 스프링 컨테이너에 등록되고, 자바 힙 메모리에 올라간다.
		 * 반면 실제 객체는 자바 힙 메모리에 올라가긴 하지만, 스프링 컨테이너가 관리하지 않는다. 오로지  프록시 객체를 통해서 참조될 뿐이다.
		 */

		//이제 구현체가 아니라 프록시를 반환해야함
		//return new OrderControllerV1Impl(orderServiceV1());
		OrderControllerV1Impl controllerImpl = new OrderControllerV1Impl(orderServiceV1(logTrace)); //컨트롤 구현체는 서비스 프록시를 주입한다.
		return new OrderControllerInterfaceProxy(controllerImpl, logTrace); //클라이언트는 프록시 컨트롤을 빈으로 등록, 주입한다. 프록시 컨트롤은 컨트롤을 주입한다.
	}

	@Bean
	public OrderServiceV1 orderServiceV1(LogTrace logTrace) {

		OrderServiceV1Impl serviceImpl = new OrderServiceV1Impl(orderRepositoryV1(logTrace));
		return new OrderServiceInterfaceProxy(serviceImpl, logTrace);
	}

	@Bean
	public OrderRepositoryV1 orderRepositoryV1(LogTrace logTrace) {

		OrderRepositoryV1Impl repositoryImpl = new OrderRepositoryV1Impl();
		return new OrderRepositoryInterfaceProxy(repositoryImpl, logTrace);
	}
}
