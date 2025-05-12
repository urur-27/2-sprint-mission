package com.sprint.mission.discodeit.filter;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.UUID;

@Component
public class TraceIdFilter implements Filter {

  private static final String TRACE_ID = "traceId";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    try {
      // Trace ID 생성
      String traceId = UUID.randomUUID().toString().substring(0, 8);
      MDC.put(TRACE_ID, traceId);
      chain.doFilter(request, response);
    } finally {
      // 요청 처리 완료 후 Trace ID 제거
      MDC.remove(TRACE_ID);
    }
  }
}
