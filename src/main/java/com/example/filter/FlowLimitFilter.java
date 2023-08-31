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

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/31 19:59
 */
@Component
@Order(Const.ORDER_LIMIT)
//进行限流
public class FlowLimitFilter extends HttpFilter {

	@Resource
	StringRedisTemplate Template;

	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		String ipaddr = request.getRemoteAddr();
		if(this.tryCount(ipaddr)){
			chain.doFilter(request,response);
		}else {
			this.writeBlockMessage(response);
		}
	}

	private  void writeBlockMessage(HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write(RestBean.forbidden("操作频繁，请稍后再试").asJsonString());
	}
	private boolean tryCount(String ip){
		synchronized (ip.intern()){
			if(Boolean.TRUE.equals(Template.hasKey(Const.FLOW_LIMIT_BLOCK + ip)))
				return false;
			return this.limitPeriodCheck(ip);
		}
	}

	private boolean limitPeriodCheck(String ip){
		//限流操作
		if (Boolean.TRUE.equals(Template.hasKey(Const.FLOW_LIMIT_COUNTER + ip))) {
			Long increment = Optional.ofNullable(Template.opsForValue().increment(Const.FLOW_LIMIT_COUNTER + ip)).orElse(0L);
			if(increment > 10){
				Template.opsForValue().set(Const.FLOW_LIMIT_BLOCK+ip,"",30 ,TimeUnit.SECONDS);
				return false;
			}
		}else {
			Template.opsForValue().set(Const.FLOW_LIMIT_COUNTER+ip, "1",3, TimeUnit.SECONDS);
		}
		return true;
	}
}
