package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/*
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/28 21:17
 */

/**
 * JWT工具类
 */
@Component
public class JWTUtils {

	@Value("${spring.security.jwt.key}")
	String key;

	@Value("${spring.security.jwt.expire}")
	int expire;

	@Resource
	StringRedisTemplate template;

	/**
	 * 使JWT无效
	 *
	 * @param headerToken JWT头令牌
	 * @return 是否使JWT无效
	 */
	public boolean invalidateJwt(String headerToken) {
		String token = this.convertToken(headerToken);
		if (token == null)
			return false;
		Algorithm algorithm = Algorithm.HMAC256(key);
		JWTVerifier jwtVerifier = JWT.require(algorithm).build();
		try {
			DecodedJWT jwt = jwtVerifier.verify(token);
			String id = jwt.getId();
			return deleteToken(id , jwt.getExpiresAt());
		} catch ( JWTVerificationException e ) {
			return false;
		}
	}

	/**
	 * 删除Token
	 *
	 * @param uuid Token的唯一标识
	 * @param time Token过期时间
	 * @return 是否成功删除Token
	 */
	private boolean deleteToken(String uuid , Date time) {
		if (this.isInvalidToken(uuid))
			return false;
		Date now = new Date();
		long expire = Math.max(time.getTime() - now.getTime() , 0);
		template.opsForValue().set(Const.JWT_BLACK_LIST + uuid , "" , expire , TimeUnit.MILLISECONDS);
		return true;
	}

	/**
	 * 判断Token是否无效
	 *
	 * @param uuid Token的唯一标识
	 * @return Token是否无效
	 */
	private boolean isInvalidToken(String uuid) {
		return Boolean.TRUE.equals(template.hasKey(Const.JWT_BLACK_LIST + uuid));
	}

	/**
	 * 解析JWT
	 *
	 * @param headerToken JWT头令牌
	 * @return 解析后的JWT
	 */
	public DecodedJWT resolveJwt(String headerToken) {
		String token = this.convertToken(headerToken);
		if (token == null)
			return null;
		Algorithm algorithm = Algorithm.HMAC256(key);
		JWTVerifier jwtVerifier = JWT.require(algorithm).build();
		try {
			DecodedJWT Verify = jwtVerifier.verify(token);
			if (this.isInvalidToken(Verify.getId()))
				return null;
			Date expiresAt = Verify.getExpiresAt();
			return new Date().after(expiresAt) ? null : Verify;
		} catch ( JWTVerificationException e ) {
			return null;
		}
	}

	/**
	 * 创建JWT
	 *
	 * @param details  用户详情
	 * @param id       用户ID
	 * @param username 用户名
	 * @return 创建的JWT
	 */
	public String CreateJwt(UserDetails details , int id , String username) {
		Algorithm algorithm = Algorithm.HMAC256(key);
		Date expire = this.expireTime();
		return JWT.create()
				.withJWTId(UUID.randomUUID().toString())
				.withClaim("id" , id)
				.withClaim("name" , username)
				.withClaim("authorities" , details.getAuthorities().stream().map(GrantedAuthority :: getAuthority).toList())
				.withExpiresAt(expire)
				.withIssuedAt(new Date())
				.sign(algorithm);
	}

	/**
	 * 将解析后的JWT转换为User对象
	 *
	 * @param jwt 解析后的JWT
	 * @return 转换后的User对象
	 */
	public UserDetails toUser(DecodedJWT jwt) {
		Map<String, Claim> claims = jwt.getClaims();
		return User
				.withUsername(claims.get("name").asString())
				.password("******")
				.authorities(claims.get("authorities").asArray(String.class))
				.build();
	}

	/**
	 * 获取解析后的JWT中的用户ID
	 *
	 * @param jwt 解析后的JWT
	 * @return 用户ID
	 */
	public Integer toId(DecodedJWT jwt) {
		Map<String, Claim> claims = jwt.getClaims();
		return claims.get("id").asInt();
	}

	/**
	 * 获取Token的过期时间
	 *
	 * @return Token的过期时间
	 */
	public Date expireTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR , expire * 24);
		return calendar.getTime();
	}

	/**
	 * 将Token转换为字符串形式
	 *
	 * @param headerToken JWT头令牌
	 * @return 转换后的字符串
	 */
	private String convertToken(String headerToken) {
		if (headerToken == null || ! headerToken.startsWith("Bearer "))
			return null;
		return headerToken.substring(7);
	}
}

