package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/29 8:09
 */
@RestController
@RequestMapping("/api/test")
public class TestController {
	@GetMapping("/hello")
	public String test(){
		return "hello world!";
	}
}
