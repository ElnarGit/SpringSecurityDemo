package net.elnar.springsecuritydemo.security;

import lombok.RequiredArgsConstructor;
import net.elnar.springsecuritydemo.model.User;
import net.elnar.springsecuritydemo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsServiceImpl")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email).orElseThrow(() ->
				new UsernameNotFoundException("User doesn't exists"));
		
		return SecurityUser.fromUser(user);
	}
}
