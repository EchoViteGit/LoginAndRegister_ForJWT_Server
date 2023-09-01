# 前后端分离项目（JWT方案）
采用SpringBoot3 + Vue3编写的前后端分离模版项目，集成多种技术栈，使用JWT校验方案。

#### 技术栈：
```yaml
   #技术栈      版本
    JDK:      17.0.8
    Node.js:  18.17.1
    MySQL:    8.0.32
    Redis:    7.0.13
    RabbitMQ: 3.8.16
```
***
### 后端功能与技术点
用户注册、用户登录、重置密码等基础功能以及对应接口
* 采用Mybatis-Plus作为持久层框架，使用更便捷
* 采用Redis存储注册/重置操作验证码，带过期时间控制
* 采用RabbitMQ积压短信发送任务，再由监听器统一处理
* 采用SpringSecurity作为权限校验框架，手动整合Jwt校验方案
* 采用Redis进行IP地址限流处理，防刷接口
* 视图层对象和数据层对象分离，编写工具方法利用反射快速互相转换
* 错误和异常页面统一采用JSON格式返回，前端处理响应更统一
* 手动处理跨域，采用过滤器实现
* 项目整体结构清晰，职责明确，注释全面，开箱即用

### 前端功能与技术点
用户注册、用户登录、重置密码等界面，以及一个简易的主页
* 采用Vue-Router作为路由
* 采用Axios作为异步请求框架
* 采用Element-Plus作为UI组件库
* 使用VueUse适配深色模式切换
* 使用unplugin-auto-import按需引入，减少打包后体积


### MYSQL数据库实现
直接运行即可
```mysql
/*
 Navicat Premium Data Transfer

 Source Server         : MySQL
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34)
 Source Host           : localhost:3306
 Source Schema         : jwt

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34)
 File Encoding         : 65001

 Date: 01/09/2023 14:50:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS `jwt`;
CREATE DATABASE jwt;
-- ----------------------------
-- Table structure for db_account
-- ----------------------------
USE jwt;

DROP TABLE IF EXISTS `db_account`;
CREATE TABLE `db_account`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `register_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_name`(`username` ASC) USING BTREE,
  UNIQUE INDEX `unique_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

```