package hello.advanced.app.v2;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceV2 {

  private final OrderRepositoryV2 orderRepository;
  private final HelloTraceV2 trace;

  public void orderItem(TraceId traceId, String itemId) {

    TraceStatus status = null;
    try {
      //begin -> beginSync 로 수정
      status = trace.beginSync(traceId, "OrderService.orderItem()");
      orderRepository.save(status.getTraceId(), itemId); //파라미터로 또 traceId 넘기기 (http 요청에 대한 로그 id 유지해야하니까)
      trace.end(status);
    } catch (Exception e) {
      trace.exception(status, e);
      throw e;
    }
  }
}
