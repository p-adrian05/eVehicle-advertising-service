package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.core.role.model.Role;
import org.example.security.jwt.JwtTokenFilterConfigurer;
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
                .antMatchers(HttpMethod.DELETE,"/**").hasAnyAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.POST,"/api/role").hasAnyAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.PATCH,"/api/rate","/api/user/roles").hasAnyAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.POST,"/api/authenticate").permitAll()
                .antMatchers(HttpMethod.POST,"/api/auth").permitAll()
                .antMatchers(HttpMethod.GET,"/api/authenticate/activate/*").permitAll()
                .antMatchers(HttpMethod.POST,"/api/user").permitAll()
                .antMatchers(HttpMethod.GET,"/api/user/**").permitAll()
                .antMatchers(HttpMethod.GET,"/api/advertisement/**","/api/advertisements").permitAll()
                .antMatchers(HttpMethod.GET,"/api/roles","/api/user/*/roles").permitAll()
                .antMatchers(HttpMethod.GET,"/api/img/**").permitAll()
                .antMatchers(HttpMethod.GET,"/api/rates","/api/rates/count/*").permitAll()
                .anyRequest().authenticated()
        .and().sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.apply(jwtTokenFilter);
        http.cors();
    }
    @Override
    public void configure(WebSecurity web) throws Exception {
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
