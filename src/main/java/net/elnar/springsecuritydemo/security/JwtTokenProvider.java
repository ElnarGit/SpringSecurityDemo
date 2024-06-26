package net.elnar.springsecuritydemo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
	
	@Qualifier("userDetailsServiceImpl")
	private final UserDetailsService userDetailsService;
	
	@Value("${jwt.expiration}")
	private Long validityInMilliseconds;
	
	@Value("${jwt.header}")
	private String authorizationHeader;
	
	private SecretKey secretKey;
	
	@PostConstruct
	protected void init(){
		secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	}
	
	public String createToken(String email, String role){
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", role);
		claims.put("email", email);
		
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);
		
		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(validity)
				.signWith(secretKey)
				.compact();
	}
	
	public boolean validateToken(String token){
		try {
			Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
			return !claimsJws.getBody().getExpiration().before(new Date());
		}catch (JwtException | IllegalArgumentException e){
			throw new JwtAuthenticationException("JWT token is expired or invalid", HttpStatus.UNAUTHORIZED);
		}
	}
	
	public Authentication getAuthentication(String token) {
		String email = getEmailFromToken(token);
		log.info("Extracted email from token: " + email);
		
		if (email == null) {
			log.error("Email is null, cannot authenticate");
			throw new UsernameNotFoundException("Email is null");
		}
		UserDetails userDetails = userDetailsService.loadUserByUsername(email);
		log.info("UserDetails loaded: " + userDetails);
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}
	
	public String getEmailFromToken(String token) {
		return Jwts.parser()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.get("email", String.class);
	}
	
	public String resolveToken(HttpServletRequest request){
		return request.getHeader(authorizationHeader);
	}
}
