package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/28 21:45
 */
@Data
public class AuthorizeVO {
	String username;
	String role;
	String token;
	Date expire;
}
