package com.example.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.AccountDTO;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVO;
import com.example.mapper.AccountMapper;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/*
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/29 12:50
 */

/**
 * 账户ServiceImpl类实现了账户服务接口AccountService，
 * 提供了用户认证、注册、密码重置等功能的实现。
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountDTO> implements AccountService {

	@Resource
	AmqpTemplate amqpTemplate;

	@Resource
	StringRedisTemplate stringRedisTemplate;

	@Resource
	FlowUtils flowUtils;

	@Resource
	PasswordEncoder encoder;

	/**
	 * 根据用户名加载用户详情信息。
	 *
	 * @param username 用户名
	 *
	 * @return 用户详情信息
	 *
	 * @throws UsernameNotFoundException 当找不到与用户名对应的用户时抛出异常
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AccountDTO accountDTO = this.findAccountByNameOrEmail(username);
		if (accountDTO == null)
			throw new UsernameNotFoundException("用户名或密码错误！");
		return User
				.withUsername(username)
				.password(accountDTO.getPassword())
				.roles(accountDTO.getRole())
				.build();
	}

	/**
	 * 根据指定的文本在数据库中查找账户信息。
	 *
	 * @param text 指定的文本
	 *
	 * @return 匹配的账户信息
	 */
	public AccountDTO findAccountByNameOrEmail(String text) {
		return this.query()
				.eq("username" , text)
				.or()
				.eq("email" , text)
				.one();
	}

	/**
	 * 生成邮件验证码并发送到指定的邮箱。
	 *
	 * @param type  验证码类型
	 * @param email 邮箱地址
	 * @param ip    用户IP
	 *
	 * @return 如果发送请求过于频繁则返回提示信息，否则返回null
	 */
	@Override
	public String registerEmailVerifyCode(String type , String email , String ip) {
		synchronized (ip.intern()) {
			if (! this.verifyLimit(ip)) {
				return "请求频繁，请稍后再试！";
			}
			Random random = new Random();
			int code = random.nextInt(899999) + 100000;
			Map<String, Object> data = Map.of("type" , type , "email" , email , "code" , code);
			amqpTemplate.convertAndSend("mail" , JSON.toJSON(data));
			stringRedisTemplate.opsForValue()
					.set(Const.VERIFY_EMAIL_DATA + email , String.valueOf(code) , 3 , TimeUnit.MINUTES);//3分钟有效
			return null;
		}
	}

	/**
	 * 检查用户IP是否在指定的时间内超过限制次数。
	 *
	 * @param ip 用户IP
	 *
	 * @return 如果不超过限制次数则返回true，否则返回false
	 */
	private boolean verifyLimit(String ip) {
		String key = Const.VERIFY_EMAIL_LIMIT + ip;
		return flowUtils.limitOnceCheck(key , Const.VERIFY_EMAIL_LIMIT_TIME);
	}

	/**
	 * 注册新用户账户。
	 *
	 * @param vo 邮箱注册信息
	 *
	 * @return 如果注册成功则返回null，否则返回错误提示信息
	 */
	@Override
	public String registerEmailAccount(EmailRegisterVO vo) {
		String email = vo.getEmail();
		String username = vo.getUsername();
		String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + email);
		if (code == null)
			return "请先获取验证码！";
		if (! code.equals(vo.getCode()))
			return "验证码输入错误，请重新输入！";
		if (this.existsAccountByEmail(email))
			return "此电子邮件已被注册！";
		if (this.existsAccountByUsername(username))
			return "此用户名已被注册,请更换一个新的用户名";
		String password = encoder.encode(vo.getPassword());
		AccountDTO accountDTO = new AccountDTO(null , username , password , email , "user" , new Date());
		if (this.save(accountDTO)) {
			stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
			return null;
		} else {
			return "内部错误,请联系管理员！";
		}
	}

	/**
	 * 重置用户邮箱账户密码。
	 *
	 * @param vo 邮箱重置信息
	 *
	 * @return 如果密码重置成功则返回null，否则返回错误提示信息
	 */
	@Override
	public String resetEmailAccountPassword(EmailResetVO vo) {
		String verify = this.resetConfirm(new ConfirmResetVO(vo.getEmail() , vo.getCode()));
		if (verify != null)
			return verify;
		String email = vo.getEmail();
		String password = encoder.encode(vo.getPassword());
		boolean update = this.update().eq("email" , email).set("password" , password).update();
		if (update) {
			stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
		}
		return null;
	}

	/**
	 * 验证密码重置请求是否合法。
	 *
	 * @param vo 验证信息
	 *
	 * @return 如果验证通过则返回null，否则返回错误提示信息
	 */
	@Override
	public String resetConfirm(ConfirmResetVO vo) {
		String email = vo.getEmail();
		AccountDTO accountDTO = this.findAccountByNameOrEmail(email);
		String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + email);
		if (code == null)
			return "请先获取验证码！";
		if (! code.equals(vo.getCode())) {
			return "验证码错误";
		}
		if (accountDTO != null) {
			return null;
		} else {
			return "没有与此邮箱绑定的账号！";
		}
	}

	/**
	 * 判断是否存在指定邮箱的账户。
	 *
	 * @param email 邮箱地址
	 *
	 * @return 如果存在指定邮箱的账户则返回true，否则返回false
	 */
	private boolean existsAccountByEmail(String email) {
		return this.baseMapper.exists(
				Wrappers
						.<AccountDTO>query()
						.eq("email" , email));
	}

	/**
	 * 判断是否存在指定用户名的账户。
	 *
	 * @param username 用户名
	 *
	 * @return 如果存在指定用户名的账户则返回true，否则返回false
	 */
	private boolean existsAccountByUsername(String username) {
		return this.baseMapper.exists(
				Wrappers
						.<AccountDTO>query()
						.eq("username" , username));
	}
}
