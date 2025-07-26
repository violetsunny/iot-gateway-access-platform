/**
 * llkang.com Inc.
 * Copyright (c) 2010-2024 All Rights Reserved.
 */
package com.ennew.iot.gateway.starter.configuration;

import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author kanglele
 * @version $Id: CachingRequestBodyFilter, v 0.1 2024/3/21 14:15 kanglele Exp $
 */
@Component
public class CachingRequestBodyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (req.getRequestURI().equals("/access/std/rtg")) {
            // 只对特定的请求启用请求体缓存
            ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(req);
            chain.doFilter(requestWrapper, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
