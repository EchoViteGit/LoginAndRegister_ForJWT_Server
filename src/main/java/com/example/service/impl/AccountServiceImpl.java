package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.mapper.AccountMapper;
import com.example.service.AccountService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/29 12:50
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = this.findAccountByNameOrEmail(username);
		if(account == null)
			throw new UsernameNotFoundException("用户名或密码错误！");
		return User
				.withUsername(username)
				.password(account.getPassword())
				.roles(account.getRole())
				.build();
	}

	public Account findAccountByNameOrEmail(String text){
      	return this.query()
				.eq("username", text)
				.or()
				.eq("email", text)
				.one();
	}
}
