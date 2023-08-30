package com.example.utils;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/29 9:20
 */
public class Const {
	public static final String JWT_BLACK_LIST = "jwt:blacklist:";
	public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit";
	public static final String VERIFY_EMAIL_DATA = "verify:email:data";

	public static final int VERIFY_EMAIL_LIMIT_TIME = 60;//限流事件：s
	public static final int ORDER_CORS = -102;
}
