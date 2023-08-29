package com.example.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/28 20:51
 */
public record RestBean<T>(int code,T data ,String message) {
	public static <T> RestBean<T> success(T data) {
		return new RestBean<T>(200, data, "请求成功");
	}
	public static <T> RestBean<T> success() {
		return success(null);
	}
	public static<T> RestBean<T> failure(int code, String message) {
		return new RestBean<>(code,null,message);
	}
	public static<T> RestBean<T> unauthorized(String message){
		return failure(401,message);
	}
	public static<T> RestBean<T> forbidden(String message){
		return failure(403,message);
	}
	public String asJsonString(){
		return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
	}

}
