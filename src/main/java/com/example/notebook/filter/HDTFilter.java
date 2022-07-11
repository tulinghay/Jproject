package com.example.notebook.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * @Author William
 * @Date 2022/6/23 10:53
 * @Version 1.0
 */
public class HDTFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {


    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("进入了filter");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
