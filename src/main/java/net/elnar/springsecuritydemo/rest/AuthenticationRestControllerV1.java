package net.elnar.springsecuritydemo.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.elnar.springsecuritydemo.model.User;
import net.elnar.springsecuritydemo.repository.UserRepository;
import net.elnar.springsecuritydemo.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationRestControllerV1 {
	
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	
	@PostMapping("/login")
	public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequestDTO request){
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
			User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User doesn't exists"));
			String token = jwtTokenProvider.createToken(request.getEmail(), user.getRole().name());
			Map<Object, Object> response = new HashMap<>();
			response.put("email", request.getEmail());
			response.put("token", token);
			
			return ResponseEntity.ok(response);
		}catch (AuthenticationException e){
			return new ResponseEntity<>("Invalid email/password combination", HttpStatus.FORBIDDEN);
		}
	}
	
	@PostMapping("/logout")
	public void logout (HttpServletRequest request, HttpServletResponse response) {
		SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
		securityContextLogoutHandler.logout(request, response, null);
	}
}
