package com.example.filter;

import com.example.utils.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/29 18:53
 * <p>
 * 解决跨域问题
 */

/**
 * 跨域请求过滤器
 */
@Component
@Order(Const.ORDER_CORS)
public class CorsFilter extends HttpFilter {

	/**
	 * 对跨域请求进行过滤处理
	 *
	 * @param request  跨域请求的HttpServletRequest对象
	 * @param response 跨域请求的HttpServletResponse对象
	 * @param chain    跨域请求的FilterChain对象
	 * @throws IOException      如果发生I/O错误
	 * @throws ServletException 如果发生Servlet异常
	 */
	@Override
	public void doFilter(HttpServletRequest request ,
						 HttpServletResponse response ,
						 FilterChain chain) throws IOException, ServletException {
		this.addCorsHeader(request , response);
		chain.doFilter(request , response);
	}

	/**
	 * 添加跨域请求的响应头
	 *
	 * @param request  跨域请求的HttpServletRequest对象
	 * @param response 跨域请求的HttpServletResponse对象
	 */
	private void addCorsHeader(HttpServletRequest request ,
							   HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin" , request.getHeader("Origin"));
//        response.addHeader("Access-Control-Allow-Origin","http://localhost:5173"));
		response.addHeader("Access-Control-Allow-Methods" , "GET,HEAD,POST,PUT,DELETE,OPTIONS,PATCH");
		response.addHeader("Access-Control-Allow-Headers" , "Authorization,Content-Type");
	}
}

