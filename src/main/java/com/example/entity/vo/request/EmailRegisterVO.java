package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/31 10:27
 */
@Data
public class EmailRegisterVO {
	@Email
	@Length(min = 6)
	String email;
	@Length(max = 6,min = 6,message = "验证码长度为6位")
	String code;
	@Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$",message = "不能有特殊字符")
	@Length(min = 4,max = 10)
	String username;
	@Length(min = 6,max = 20)
	@Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\W]{6,20}",message = "密码需包含字母和数字")
	String password;
}
