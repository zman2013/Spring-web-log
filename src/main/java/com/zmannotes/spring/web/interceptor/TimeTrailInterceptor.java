package com.zmannotes.spring.web.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 记录请求从接收到返回相应的时间
 */
public class TimeTrailInterceptor extends HandlerInterceptorAdapter {

    private static Logger logger = LoggerFactory.getLogger(TimeTrailInterceptor.class);

    public static ThreadLocal<Long> threadData = new ThreadLocal<Long>();

    private static final String SPACE = " ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        long startTime = System.currentTimeMillis();
        threadData.set(startTime);
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

        long startTime = threadData.get();
        long requestTime = System.currentTimeMillis() - startTime;

        StringBuilder sb = new StringBuilder(request.getMethod()).append(SPACE)
                .append(request.getRequestURL().toString()).append("?");

        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            for (String v : values) {
                sb.append(key).append("=").append(v).append("&");
            }
        }

        String content = sb.toString() + SPACE + requestTime;

        logger.info(content);
        super.afterCompletion(request, response, handler, ex);
    }

}
