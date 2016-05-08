package com.zmannotes.spring.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.logging.log4j.ThreadContext;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.util.UUID;

/**
 * 生成traceId，与线程绑定，供logj4j2使用。保证同一请求的线程内log均使用相同的traceId。（参看log4j2.xml中的配置）
 */
public class GeneralParamInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        StringBuilder sb = new StringBuilder();
        String path = !StringUtils.isEmpty(request.getServletPath()) ? request.getServletPath().replace("/", "") : "";
        sb.append("[requestId:").append(path).append("_").append(System.currentTimeMillis()).append("]");

        String requestId = sb.toString();

        //traceId，若无则生成一个traceId
        String traceId = request.getParameter("trace_id");
        if (StringUtils.isEmpty(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        ThreadContext.put("trace_id", traceId + " # " + requestId);

        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        ThreadContext.getContext().remove("trace_id");
    }
}
