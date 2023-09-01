package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVO;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;
import java.util.function.Supplier;

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

	/**
	 * 请求邮件验证码
	 * @param email 请求邮件
	 * @param type 类型
	 * @param request 请求
	 * @return 是否请求成功
	 */
	@GetMapping("/ask-code")
	public RestBean<Void> askVerifyCode(@RequestParam @Email String email,
										@RequestParam @Pattern(regexp = "(register|reset)") String type,
										HttpServletRequest request){
		return this.messageHandle(()->
				service.registerEmailVerifyCode(type, email,request.getRemoteAddr()));
	}

	/***
	 * 实现注册功能
	 * @param vo 接收封装的实体对象
	 * @return 是否注册成功
	 */
	@PostMapping("/register")
	public RestBean<Void> register(@RequestBody @Valid EmailRegisterVO vo){
		return this.messageHandle(vo,service::registerEmailAccount);
	}

	/***
	 * 实现重置密码的邮箱验证
	 * @param vo 接收封装的实体对象
	 * @return 邮箱是否成功
	 */
	@PostMapping("/reset-confirm")
	public RestBean<Void> resetConfirm(@RequestBody @Valid ConfirmResetVO vo){
		return this.messageHandle(vo,service::resetConfirm);
	}

	/***
	 * 重设账号密码
	 * @param vo 接收封装的实体对象
	 * @return 密码是否是否成功
	 */
	@PostMapping("/reset-password")
	public RestBean<Void> resetConfirm(@RequestBody @Valid EmailResetVO vo){
		return this.messageHandle(vo,service::resetEmailAccountPassword);
	}

	private RestBean<Void> messageHandle(Supplier<String> action){
		String message = action.get();
		return message == null ? RestBean.success():RestBean.failure(400, message);
	}

	private <T> RestBean<Void> messageHandle(T vo, Function<T,String> function){
		return messageHandle(()->function.apply(vo));
	}
}
