package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/31 18:25
 */
@Data
@AllArgsConstructor
public class ConfirmResetVO {
	@Email
	String email;
	@Length(max = 6,min = 6)
	String code;
}

