package com.example.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.utils.JWTUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/28 22:15
 */

/**
 * JWT授权过滤器
 */
@Component
public class JwtAuthorizeFilter extends OncePerRequestFilter {

	@Resource
	private JWTUtils utils;

	/**
	 * 过滤器内部方法，处理HTTP请求和响应
	 *
	 * @param request     HTTP请求
	 * @param response    HTTP响应
	 * @param filterChain 过滤器链
	 * @throws ServletException Servlet异常
	 * @throws IOException      IO异常
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request ,
									HttpServletResponse response ,
									FilterChain filterChain) throws ServletException, IOException {
		// 获取请求头中的Authorization
		String authorization = request.getHeader("Authorization");
		// 解析JWT
		DecodedJWT jwt = utils.resolveJwt(authorization);
		if (jwt != null) {
			// 将JWT解析成UserDetails对象
			UserDetails user = utils.toUser(jwt);
			// 创建UsernamePasswordAuthenticationToken对象，并设置认证信息
			UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(user , null , user.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			// 设置SecurityContextHolder中的认证信息
			SecurityContextHolder.getContext().setAuthentication(authentication);
			// 设置请求属性
			request.setAttribute("id" , utils.toId(jwt));
		}
		// 执行过滤器链
		filterChain.doFilter(request , response);
	}
}

