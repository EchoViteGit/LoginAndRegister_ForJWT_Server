package com.example.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/30 10:45
 */
// 限流工具
@Component
public class FlowUtils {

	@Resource
	StringRedisTemplate template;

	/**
	 * 检查是否限流
	 *
	 * @param key       键名
	 * @param blockTime 块时长
	 * @return 是否限流
	 */
	public boolean limitOnceCheck(String key , int blockTime) {
		if (Boolean.TRUE.equals(template.hasKey(key))) {
			return false;
		} else {
			template.opsForValue().set(key , "限流" , blockTime , TimeUnit.SECONDS);
			return true;
		}
	}
}

