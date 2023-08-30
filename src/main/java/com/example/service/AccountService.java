package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/29 12:49
 */
public interface AccountService extends IService<Account>, UserDetailsService {
	public Account findAccountByNameOrEmail(String text);
	String registerEmailVerifyCode(String type,String email,String ip);
}
