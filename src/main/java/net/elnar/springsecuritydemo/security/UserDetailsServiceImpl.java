package net.elnar.springsecuritydemo.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.elnar.springsecuritydemo.model.User;
import net.elnar.springsecuritydemo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email).orElseThrow(() -> {
			log.error("User with email {} not found", email);
			return new UsernameNotFoundException("User doesn't exists");
		});
		
		
		log.info("User with email {} found", email);
		return SecurityUser.fromUser(user);
	}
}
