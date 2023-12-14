package com.example.utils;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/29 9:20
 */
public class Const {
	/**
	 * JWT黑名单标识
	 */
	public static final String JWT_BLACK_LIST = "jwt:blacklist:";
	/**
	 * 验证邮箱限制标识
	 */
	public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit";
	/**
	 * 验证邮箱数据标识
	 */
	public static final String VERIFY_EMAIL_DATA = "verify:email:data";

	/**
	 * 验证邮箱限制时间，单位：秒
	 */
	public static final int VERIFY_EMAIL_LIMIT_TIME = 60;
	/**
	 * 订单跨域权限值
	 */
	public static final int ORDER_CORS = - 102;
	/**
	 * 订单限制权限值
	 */
	public static final int ORDER_LIMIT = - 101;
	/**
	 * 流控计数器标识
	 */
	public static final String FLOW_LIMIT_COUNTER = "flow:counter:";
	/**
	 * 流控黑名单标识
	 */
	public static final String FLOW_LIMIT_BLOCK = "flow:block:";
}
