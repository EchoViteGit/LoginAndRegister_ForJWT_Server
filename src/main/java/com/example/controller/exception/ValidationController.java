package com.example.controller.exception;

import com.example.entity.RestBean;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/30 14:43
 */
@Slf4j
@RestControllerAdvice
public class ValidationController {
	@ExceptionHandler(ValidationException.class)
	public RestBean<Void> validateException(ValidationException exception){
		log.error("Resolve[{}:{}]",exception.getClass().getName(),exception.getMessage());
		return RestBean.failure(400, "请求参数有误");
	}
}
