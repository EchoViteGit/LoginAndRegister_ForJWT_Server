package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/30 11:30
 */
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

	@Resource
	AccountService service;
	@GetMapping("/ask-code")
	public RestBean<Void> askVerifyCode(@RequestParam @Email String email,
										@RequestParam @Pattern(regexp = "(register|reset)") String type,
										HttpServletRequest request){
		String message = service.registerEmailVerifyCode(type, email,request.getRemoteAddr());
		return message == null ? RestBean.success():RestBean.failure(400, message);
	}
}
