package com.example.config;

import com.example.entity.RestBean;
import com.example.entity.dto.AccountDTO;
import com.example.entity.vo.response.AuthorizeVO;
import com.example.filter.JwtAuthorizeFilter;
import com.example.service.AccountService;
import com.example.utils.JWTUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

/*
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/28 20:29
 * <p>
 * SpringSecurity的配置
 */

/**
 * SpringSecurity的配置
 */
@Configuration
public class SecurityConfiguration {

	@Resource
	private JWTUtils Utils;
	@Resource
	private JwtAuthorizeFilter jwtAuthorizeFilter;
	@Resource
	private AccountService accountService;

	/**
	 * 创建一个安全过滤链
	 *
	 * @param httpSecurity HttpSecurity对象
	 * @return 安全过滤链
	 * @throws Exception 异常
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
				.authorizeHttpRequests(
						conf -> conf
								.requestMatchers("/api/auth/**" , "/error").permitAll()
								.anyRequest().authenticated()
				)
				.formLogin(
						conf -> conf
								.loginProcessingUrl("/api/auth/login")
								.successHandler(this :: onAuthenticationSuccess)
								.failureHandler(this :: onAuthenticationFailure)
				)
				.logout(
						conf -> conf
								.logoutUrl("/api/auth/logout")
								.logoutSuccessHandler(this :: onLogoutSuccess)
				)
				.sessionManagement(
						conf -> conf
								.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.exceptionHandling(
						conf -> conf
								.authenticationEntryPoint(this :: onUnauthorized)
								.accessDeniedHandler(this :: onAccessDeny)
				)
				.addFilterBefore(jwtAuthorizeFilter , UsernamePasswordAuthenticationFilter.class)
				.csrf(AbstractHttpConfigurer :: disable)
				.build();
	}


	/***
	 * 成功登录
	 * @param request 请求
	 * @param response 回答
	 * @param authentication 身份验证
	 * @throws IOException 异常
	 */
	public void onAuthenticationSuccess(HttpServletRequest request ,
										HttpServletResponse response ,
										Authentication authentication) throws IOException {
		response.setContentType("application/json;charset=utf-8");
		User user = (User) authentication.getPrincipal();
		AccountDTO accountDTO = accountService.findAccountByNameOrEmail(user.getUsername());
		String token = Utils.CreateJwt(user , accountDTO.getId() , accountDTO.getUsername());//生成令牌
		AuthorizeVO vo = accountDTO.asViewObject(AuthorizeVO.class , v -> {
			v.setExpire(Utils.expireTime());
			v.setToken(token); //派发令牌
		});
		response.getWriter().write(RestBean.success(vo).asJsonString()); //回显数据给前端
	}

	/**
	 * 处理身份验证失败的回调方法
	 *
	 * @param request   HTTP请求
	 * @param response  HTTP响应
	 * @param exception 身份验证异常
	 * @throws IOException 输入输出异常
	 */
	public void onAuthenticationFailure(HttpServletRequest request ,
										HttpServletResponse response ,
										AuthenticationException exception) throws IOException {
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
	}

	/**
	 * 处理成功退出登录的回调方法
	 *
	 * @param request        HTTP请求
	 * @param response       HTTP响应
	 * @param authentication 用户身份信息
	 * @throws IOException 输入输出异常
	 */
	public void onLogoutSuccess(HttpServletRequest request ,
								HttpServletResponse response ,
								Authentication authentication) throws IOException {
		response.setContentType("application/json;charset=utf-8");
		PrintWriter writer = response.getWriter();
		String authorization = request.getHeader("Authorization");
		if (Utils.invalidateJwt(authorization)) {
			writer.write(RestBean.success().asJsonString());
		} else {
			writer.write(RestBean.failure(400 , "退出登录失败").asJsonString());
		}
	}

	/**
	 * 处理访问拒绝的回调方法
	 *
	 * @param request   HTTP请求
	 * @param response  HTTP响应
	 * @param exception 访问拒绝异常
	 * @throws IOException 输入输出异常
	 */
	public void onAccessDeny(HttpServletRequest request ,
							 HttpServletResponse response ,
							 AccessDeniedException exception) throws IOException {
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write(RestBean.forbidden(exception.getMessage()).asJsonString());
	}

	/**
	 * 处理未授权的回调方法
	 *
	 * @param request   HTTP请求
	 * @param response  HTTP响应
	 * @param exception 未授权异常
	 * @throws IOException 输入输出异常
	 */
	public void onUnauthorized(HttpServletRequest request ,
							   HttpServletResponse response ,
							   AuthenticationException exception) throws IOException {
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
	}

}
