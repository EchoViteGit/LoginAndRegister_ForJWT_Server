package com.example.filter;

import com.example.entity.RestBean;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/*
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/31 19:59
 */

/**
 * 流量限制过滤器
 * 进行限流操作
 */
@Component
@Order(Const.ORDER_LIMIT)
public class FlowLimitFilter extends HttpFilter {

	@Resource
	StringRedisTemplate Template;

	/**
	 * 过滤器方法，对请求进行流量限制判断
	 *
	 * @param request  HTTP请求对象
	 * @param response HTTP响应对象
	 * @param chain    过滤器链
	 * @throws IOException      IO异常
	 * @throws ServletException Servlet异常
	 */
	@Override
	protected void doFilter(HttpServletRequest request , HttpServletResponse response , FilterChain chain) throws IOException, ServletException {
		String ipaddr = request.getRemoteAddr();
		if (this.tryCount(ipaddr)) {
			chain.doFilter(request , response);
		} else {
			this.writeBlockMessage(response);
		}
	}

	/**
	 * 写入被限制访问的消息到响应
	 *
	 * @param response HTTP响应对象
	 * @throws IOException IO异常
	 */
	private void writeBlockMessage(HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write(RestBean.forbidden("操作频繁，请稍后再试").asJsonString());
	}

	/**
	 * 尝试计数操作
	 *
	 * @param ip IP地址
	 * @return 是否尝试计数成功
	 */
	private boolean tryCount(String ip) {
		synchronized (ip.intern()) {
			if (Boolean.TRUE.equals(Template.hasKey(Const.FLOW_LIMIT_BLOCK + ip)))
				return false;
			return this.limitPeriodCheck(ip);
		}
	}

	/**
	 * 限制周期检查操作
	 *
	 * @param ip IP地址
	 * @return 是否限制周期检查成功
	 */
	private boolean limitPeriodCheck(String ip) {
		//限流操作
		if (Boolean.TRUE.equals(Template.hasKey(Const.FLOW_LIMIT_COUNTER + ip))) {
			Long increment = Optional.ofNullable(Template.opsForValue().increment(Const.FLOW_LIMIT_COUNTER + ip)).orElse(0L);
			if (increment > 10) {
				Template.opsForValue().set(Const.FLOW_LIMIT_BLOCK + ip , "" , 30 , TimeUnit.SECONDS);
				return false;
			}
		} else {
			Template.opsForValue().set(Const.FLOW_LIMIT_COUNTER + ip , "1" , 3 , TimeUnit.SECONDS);
		}
		return true;
	}
}
