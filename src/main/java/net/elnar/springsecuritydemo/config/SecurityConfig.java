package net.elnar.springsecuritydemo.config;

import lombok.RequiredArgsConstructor;
import net.elnar.springsecuritydemo.model.Role;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
@RequiredArgsConstructor
public class SecurityConfig {
	
	@Qualifier("userDetailsServiceImpl")
	private final UserDetailsService userDetailsService;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.authorizeHttpRequests(authorizeRequests -> authorizeRequests
								.requestMatchers("/").permitAll()
								.anyRequest().authenticated()
				)
				.formLogin(formLogin -> formLogin
						.loginPage("/auth/login").permitAll()
						.defaultSuccessUrl("/auth/success", true)
				)
				.logout(logout -> logout
						.logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", "POST"))
						.invalidateHttpSession(true)
						.clearAuthentication(true)
						.deleteCookies("JSESSIONID")
						.logoutSuccessUrl("/auth/login")
				);
				
		
		return http.build();
	}
	
	@Bean
	protected UserDetailsService userDetailsService(){
		return new InMemoryUserDetailsManager(
				User.builder()
						.username("admin")
						.password(passwordEncoder().encode("admin"))
						.authorities(Role.ADMIN.getAuthorities())
						.build(),
				
				User.builder()
						.username("user")
						.password(passwordEncoder().encode("user"))
						.authorities(Role.USER.getAuthorities())
						.build()
		);
	}
	
	@Bean
	protected PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder(12);
	}
	
	@Bean
	protected DaoAuthenticationProvider daoAuthenticationProvider(){
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		return daoAuthenticationProvider;
	}
	
	@Bean
	protected AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}
