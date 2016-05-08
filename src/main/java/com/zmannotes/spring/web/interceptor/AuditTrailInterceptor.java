package com.zmannotes.spring.web.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 记录请求相关信息：源IP，url，参数
 */
public class AuditTrailInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(AuditTrailInterceptor.class);

    private final static String SEPARATOR = " ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURL().toString();
        String forwaredIP = request.getHeader("X-Forwarded-For");
        if (StringUtils.isEmpty(forwaredIP)) {
            forwaredIP = request.getRemoteAddr();
        }

        String method = request.getMethod();
        HandlerMethod hm = (HandlerMethod) handler;
        hm.getMethodParameters();

        StringBuilder sb = new StringBuilder();
        sb.append(forwaredIP).append(SEPARATOR).append(method).append(SEPARATOR).append(url).append("?");

        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            for (String v : values) {
                sb.append(key).append("=").append(v).append("&");
            }
        }

        logger.info(sb.toString());
        return super.preHandle(request, response, handler);
    }
}
