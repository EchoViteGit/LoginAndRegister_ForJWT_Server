package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.AccountDTO;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVO;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/29 12:49
 */
public interface AccountService extends IService<AccountDTO>, UserDetailsService {
	AccountDTO findAccountByNameOrEmail(String text);

	String registerEmailVerifyCode(String type , String email , String ip);

	String registerEmailAccount(EmailRegisterVO vo);

	String resetConfirm(ConfirmResetVO vo);

	String resetEmailAccountPassword(EmailResetVO vo);
}
