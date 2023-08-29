package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/28 21:17
 */
@Component
public class JWTUtils {

	@Value("${spring.security.jwt.key}")
	String key;

	@Value("${spring.security.jwt.expire}")
	int expire;

	public DecodedJWT resolveJwt(String headerToken){
		String token = this.convertToken(headerToken);
		if (token == null)
			return null;
		Algorithm algorithm = Algorithm.HMAC256(key);
		JWTVerifier jwtVerifier = JWT.require(algorithm).build();
		try {
			DecodedJWT Verify = jwtVerifier.verify(token);
			Date expiresAt = Verify.getExpiresAt();
			return new Date().after(expiresAt)?null:Verify;
		}catch (JWTVerificationException e){
			return null;
		}
	}
	public String CreateJwt(UserDetails details,int id,String username){
		Algorithm algorithm = Algorithm.HMAC256(key);
		Date expire = this.expireTime();
		return JWT.create()
				.withClaim("id",id )
				.withClaim("name", username)
				.withClaim("authorities",details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
				.withExpiresAt(expire)
				.withIssuedAt(new Date())
				.sign(algorithm);
	}

	public UserDetails toUser(DecodedJWT jwt){
		Map<String,Claim> claims = jwt.getClaims();
		return User
				.withUsername(claims.get("name").asString())
				.password("******")
				.authorities(claims.get("authorities").asArray(String.class))
				.build();
	}

	public Integer toId(DecodedJWT jwt){
		Map<String,Claim> claims = jwt.getClaims();
		return claims.get("id").asInt();
	}
	public Date expireTime(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR,expire*24);
		return calendar.getTime();
	}

	private String convertToken(String headerToken){
		if(headerToken==null||!headerToken.startsWith("Bearer "))
			return null;
		return headerToken.substring(7);
	}
}
