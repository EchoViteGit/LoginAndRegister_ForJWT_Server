package com.example.config;

import com.example.entity.RestBean;
import com.example.entity.vo.response.AuthorizeVO;
import com.example.filter.JwtAuthorizeFilter;
import com.example.utils.JWTUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
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

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/28 20:29
 */
@Configuration
public class SecurityConfiguration {

	@Resource
	private JWTUtils Utils;

	@Resource
	private JwtAuthorizeFilter jwtAuthorizeFilter;
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
				.authorizeHttpRequests(
						conf->conf
								.requestMatchers("/api/auth/**").permitAll()
								.anyRequest().authenticated()
				)
				.formLogin(
						conf->conf
								.loginProcessingUrl("/api/auth/login")
								.successHandler(this::onAuthenticationSuccess)
								.failureHandler(this::onAuthenticationFailure)
				)
				.logout(
						conf -> conf
								.logoutUrl("/api/auth/logout")
								.logoutSuccessHandler(this::onLogoutSuccess)
				)
				.sessionManagement(
						conf -> conf
								.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.exceptionHandling(
						conf->conf
								.authenticationEntryPoint(this::onUnauthorized)
								.accessDeniedHandler(this::onAccessDeny)

				)
				.addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
				.csrf(AbstractHttpConfigurer::disable)
				.build();
    }

	public void onAuthenticationSuccess(HttpServletRequest request,
										HttpServletResponse response,
										Authentication authentication) throws IOException {
		response.setContentType("application/json;charset=utf-8");
		User user = (User) authentication.getPrincipal();
		String token = Utils.CreateJwt(user, 1, "小明");
		AuthorizeVO vo = new AuthorizeVO();
		vo.setExpire(Utils.expireTime());
		vo.setRole("user");
        vo.setToken(token);
		vo.setUsername("小明");
		response.getWriter().write(RestBean.success(vo).asJsonString());
	}

	public void onAuthenticationFailure(HttpServletRequest request,
										HttpServletResponse response,
										AuthenticationException exception) throws IOException {
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
	}
	public void onLogoutSuccess(HttpServletRequest request,
								HttpServletResponse response,
								Authentication authentication) throws IOException, ServletException {
		response.setContentType("application/json;charset=utf-8");
		PrintWriter writer = response.getWriter();
		String authorization = request.getHeader("Authorization");
		if(Utils.invalidateJwt(authorization)){
			writer.write(RestBean.success().asJsonString());
		}
		else{
			writer.write(RestBean.failure(400, "退出登录失败").asJsonString());
		}
	}
	public void onAccessDeny(HttpServletRequest request,
							 HttpServletResponse response,
							 AccessDeniedException exception) throws IOException {
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write(RestBean.forbidden(exception.getMessage()).asJsonString());
	}
	public void onUnauthorized(HttpServletRequest request,
							   HttpServletResponse response,
							   AuthenticationException exception) throws IOException {
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
	}
}
