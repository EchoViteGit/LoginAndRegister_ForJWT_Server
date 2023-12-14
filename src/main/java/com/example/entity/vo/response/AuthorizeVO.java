package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

/*
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/28 21:45
 */

/**
 * 授权返回对象
 */
@Data
public class AuthorizeVO {
	/**
	 * 用户名
	 */
	String username;
	/**
	 * 角色
	 */
	String role;
	/**
	 * 令牌
	 */
	String token;
	/**
	 * 到期时间
	 */
	Date expire;
}
