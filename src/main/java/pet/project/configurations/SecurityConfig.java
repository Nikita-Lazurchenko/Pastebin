package pet.project.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManagerFactories;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pet.project.ott.OttSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,  OttSuccessHandler ottHandler) throws Exception {
        var factorGrantedAuthority = AuthorizationManagerFactories.multiFactor()
                .requireFactors(
                        FactorGrantedAuthority.OTT_AUTHORITY
                )
                .build();

        http.csrf(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .permitAll())
                .oneTimeTokenLogin((ott) -> ott
                        .loginPage("/login/ott")
                        .tokenGenerationSuccessHandler(ottHandler)
                        .defaultSuccessUrl("/login/change-password", true)
                )
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login","/login/**", "/ott/generate", "/registration", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/login/change-password").access(factorGrantedAuthority.authenticated())
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}