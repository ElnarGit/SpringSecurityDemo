package net.elnar.springsecuritydemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.authorizeHttpRequests(authorizeRequests ->
						authorizeRequests.anyRequest().authenticated()
				)
				.formLogin(withDefaults());
		
		return http.build();
	}
	
	@Bean
	protected UserDetailsService userDetailsService(){
		return new InMemoryUserDetailsManager(
				User.builder()
						.username("admin")
						.password(passwordEncoder().encode("admin"))
						.roles("ADMIN")
						.build()
		);
	}
	
	@Bean
	protected PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder(12);
	}
}
