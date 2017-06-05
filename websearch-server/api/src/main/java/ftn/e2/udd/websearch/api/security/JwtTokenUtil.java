package ftn.e2.udd.websearch.api.security;

import ftn.e2.udd.websearch.api.security.model.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JwtTokenUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

	public static final String CLAIM_KEY_USERNAME = "sub";
	public static final String CLAIM_KEY_CREATED = "created";
	public static final String CLAIM_KEY_AUTHORITIES = "authorities";
	
	public static final String TOKEN_HEADER = "Authorization";

	public static final String JWT_SECRET = "secret";
	private static final int EXPIRATION_TIME_IN_SECONDS = 36000;

	
	public static String generateToken(Map<String, Object> claims) {
		return Jwts.builder()
				.setClaims(claims)
				.setExpiration(generateExpirationDate((Date) claims.get(CLAIM_KEY_CREATED)))
				.signWith(io.jsonwebtoken.SignatureAlgorithm.HS512, JWT_SECRET)
				.compact();
	}

	public static Date generateExpirationDate(Date dateCreated) {
		return new Date(dateCreated.getTime() + EXPIRATION_TIME_IN_SECONDS * 1000);
	}

	public static Claims getClaimsFromToken(String token) {
		Claims claims;
		
		try {
			claims = Jwts.parser()
					.setSigningKey(JWT_SECRET)
					.parseClaimsJws(token)
					.getBody();
		} catch (Exception e) {
			logger.info(String.format("Token parsing exception: %s", e.getMessage()));
			claims = null;
		}
		
		return claims;
	}

	public static String getUsernameFromToken(String token) {
		String username;
		
		try {
			Claims claimsFromToken = getClaimsFromToken(token);
			username = claimsFromToken.getSubject();
		} catch (Exception e) {
			logger.info(String.format("Token parsing exception: %s", e.getMessage()));
			username = null;
		}
		
		return username;
	}

	public static Date getExpirationDateFromToken(String token) {
		Claims claimsFromToken = getClaimsFromToken(token);
		
		return claimsFromToken.getExpiration();
	}

	public static String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		
		claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
		claims.put(CLAIM_KEY_CREATED, generateExpirationDate(new Date()));
		claims.put(CLAIM_KEY_AUTHORITIES, userDetails.getAuthorities());
		
		return generateToken(claims);
	}

	public static boolean isValidToken(String authToken, UserDetails userDetails) {
		JwtUser jwtUser = (JwtUser) userDetails;
		String usernameFromToken = getUsernameFromToken(authToken);
		
		// TODO: Extend a token validation with the additional checks (isExpired, isCreatedBeforeLastPasswordReset)
		return usernameFromToken.equals(jwtUser.getUsername());
	}



	public static String getTokenFromHttpHeader(String tokenHeader) {
		String token = "";
		
		if (tokenHeader != null && tokenHeader.startsWith("Bearer")) {
			token = tokenHeader.substring(7);
		}
		return token;
	}

}
