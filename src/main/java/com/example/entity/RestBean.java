package com.example.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

/*
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/28 20:51
 */

/**
 * REST风格的Java记录类，用于构建RESTful API的响应数据
 *
 * @param <T>     数据类型
 * @param code    状态码
 * @param data    数据
 * @param message 消息
 */
public record RestBean<T>(int code , T data , String message) {

	/**
	 * 创建一个成功的RestBean实例
	 *
	 * @param data 数据
	 * @return RestBean实例
	 */
	public static <T> RestBean<T> success(T data) {
		return new RestBean<>(200 , data , "请求成功");
	}

	/**
	 * 创建一个成功的RestBean实例
	 *
	 * @return RestBean实例
	 */
	public static <T> RestBean<T> success() {
		return success(null);
	}

	/**
	 * 创建一个失败的RestBean实例
	 *
	 * @param code    状态码
	 * @param message 消息
	 * @return RestBean实例
	 */
	public static <T> RestBean<T> failure(int code , String message) {
		return new RestBean<>(code , null , message);
	}

	/**
	 * 创建一个未授权的RestBean实例
	 *
	 * @param message 消息
	 * @return RestBean实例
	 */
	public static <T> RestBean<T> unauthorized(String message) {
		return failure(401 , message);
	}

	/**
	 * 创建一个禁止的RestBean实例
	 *
	 * @param message 消息
	 * @return RestBean实例
	 */
	public static <T> RestBean<T> forbidden(String message) {
		return failure(403 , message);
	}

	/**
	 * 将RestBean实例转换为JSON字符串
	 *
	 * @return JSON字符串
	 */
	public String asJsonString() {
		return JSONObject.toJSONString(this , JSONWriter.Feature.WriteNulls);
	}
}
