package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/29 12:39
 */
@Data
@TableName("db_account")
@AllArgsConstructor
public class Account implements BaseData {
	@TableId(type = IdType.AUTO)
	Integer id;
	String username;
	String password;
	String email;
	String role;
	Date registerTime;
}
