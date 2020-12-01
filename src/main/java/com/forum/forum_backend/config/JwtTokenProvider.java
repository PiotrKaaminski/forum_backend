package com.forum.forum_backend.config;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expirationInMilis}")
	private int jwtExpiration;

	public String generateToken(Authentication authentication) {
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

		final String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(", "));

		Date expirationDate = new Date(System.currentTimeMillis() + jwtExpiration);

		return Jwts.builder()
				.setSubject(Long.toString(userPrincipal.getId()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(expirationDate)
				.claim("authorities", authorities)
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	public boolean validateToken (String jwtToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken);
			return true;
		} catch (SignatureException e) {
			System.out.println("JwtTokenProvider.validateToken: SignatureException");
		} catch (MalformedJwtException e) {
			System.out.println("JwtTokenProvider.validateToken: MalformedJwtException");
		} catch (ExpiredJwtException e) {
			System.out.println("JwtTokenProvider.validateToken: ExpiredJwtException");
		} catch (UnsupportedJwtException e) {
			System.out.println("JwtTokenProvider.validateToken: UnsupportedJwtException");
		} catch (IllegalArgumentException e) {
			System.out.println("JwtTokenProvider.validateToken: IllegalArgumentException");
		}
		return false;
	}

	public int getUserIdFromToken(String jwtToken) {
		Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).getBody();
		return Integer.parseInt(claims.getSubject());
	}
}
