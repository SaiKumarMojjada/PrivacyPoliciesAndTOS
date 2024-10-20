package PPAndTOS.PrivacyPoliciesAndTOS.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/home", "/dashboard", "/homepage").permitAll()
                        .requestMatchers("/register", "/login", "/logout").permitAll()
                        .requestMatchers("/assets/**", "/css/**", "/js/**", "/images/**").permitAll() // Adjusted for static resources
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")                      // Custom login page
                        .loginProcessingUrl("/signin")            // URL to submit the username and password
                        .usernameParameter("userEmail")            // Parameter for username
                        .passwordParameter("userPassword")
                        .defaultSuccessUrl("/dashboard", true)    // Redirect after successful login
                        .failureUrl("/login?error=true")          // Redirect after login failure
                        .permitAll())                             // Allow access to all users to the login page
                .logout(logout -> logout
                        .logoutUrl("/perform_logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}