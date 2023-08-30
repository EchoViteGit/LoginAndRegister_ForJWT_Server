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
@Component
public class FlowUtils {
	//限流工具
	@Resource
	StringRedisTemplate template;
	public boolean limitOnceCheck(String key,int blockTime){
		if(Boolean.TRUE.equals(template.hasKey(key))){
			return false;
		}else {
			template.opsForValue().set(key,"",blockTime, TimeUnit.SECONDS);
			return true;
		}
	}
}
