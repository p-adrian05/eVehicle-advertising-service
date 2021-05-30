package org.example.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    private final JwtTokenFilterConfigurer jwtTokenFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception  {
        auth.userDetailsService(this.userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.DELETE,"/**").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.POST,"/api/role").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.PATCH,"/api/rate","/api/user/roles").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.GET,"/api/user/*/roles").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.POST,"/api/authenticate").permitAll()
                .antMatchers(HttpMethod.GET,"/api/authenticate/activate/*").permitAll()
                .antMatchers(HttpMethod.POST,"/api/user").permitAll()
                .antMatchers(HttpMethod.GET,"/api/advertisement/**").permitAll()
                .antMatchers(HttpMethod.GET,"/api/advertisements").permitAll()
                .antMatchers(HttpMethod.GET,"/api/img/**").permitAll()
                .antMatchers(HttpMethod.GET,"/api/rates").permitAll()
                .antMatchers(HttpMethod.GET,"/api/rates/count/*").permitAll()
                .antMatchers(HttpMethod.GET,"/api/user/**").permitAll()
                .anyRequest().authenticated()
        .and().sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.apply(jwtTokenFilter);
        http.cors();
    }
    @Override
    public void configure(WebSecurity web) throws Exception {
        // Allow swagger to be accessed without authentication
        web.ignoring().antMatchers("/v2/api-docs")//
                .antMatchers("/swagger-resources/**")//
                .antMatchers("/swagger-ui.html")//
                .antMatchers("/configuration/**")//
                .antMatchers("/webjars/**")//
                .antMatchers("/public");
    }
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
