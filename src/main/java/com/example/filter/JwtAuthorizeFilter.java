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
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/28 22:15
 */
@Component
public class JwtAuthorizeFilter extends OncePerRequestFilter {

	@Resource
	JWTUtils utils;
	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {
		String authorization = request.getHeader("Authorization");
		DecodedJWT jwt = utils.resolveJwt(authorization);
		if(jwt != null){
			UserDetails user = utils.toUser(jwt);
			UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			request.setAttribute("id", utils.toId(jwt));
		}
		filterChain.doFilter(request, response);
	}
}