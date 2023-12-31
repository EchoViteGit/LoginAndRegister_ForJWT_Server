package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/*
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/31 18:27
 */

/**
 * 邮箱重置类
 */
@Data
public class EmailResetVO {
	/**
	 * 邮箱地址
	 */
	@Email
	private String email;

	/**
	 * 验证码
	 */
	@Length(min = 6, max = 6)
	private String code;

	/**
	 * 密码
	 */
	@Length(min = 5, max = 20)
	@Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\W]{6,20}", message = "密码需包含字母和数字")
	private String password;
}
