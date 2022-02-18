package com.example.HMS.security;

import com.example.HMS.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * SecurityConfig extends WebSecurityConfigurerAdapter to override the following methods:
 * <li>configure(AuthenticationManagerBuilder auth)</li>
 * <li>configure(HttpSecurity http)</li>
 * */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * configure(AuthenticationManagerBuilder auth) allows to configure that from where spring security will
     * pull our users
     * */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        log.info("Looking DB for user ID and password");
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    /**
     * configure(HttpSecurity http) allows to configure the HttpSecurity
     * */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        super.configure(http);
        log.info("configuring http");
        http.csrf().disable();
        http.cors().configurationSource(corsConfigurationSource());
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/login").permitAll();
//        http.authorizeRequests().antMatchers(HttpMethod.GET,"/login").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/**").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/hmsapi/admin/**").hasAuthority(Role.ADMIN.name());

        //Patients
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/hmsapi/patients/open/**").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/hmsapi/patients/**").hasAnyAuthority(Role.ADMIN.name(),Role.RECEPTIONIST.name());
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/hmsapi/patients/**").hasAnyAuthority(Role.ADMIN.name(),Role.RECEPTIONIST.name());
        http.authorizeRequests().antMatchers(HttpMethod.PUT,"/hmsapi/patients/**").hasAnyAuthority(Role.ADMIN.name(),Role.RECEPTIONIST.name());
        http.authorizeRequests().antMatchers(HttpMethod.DELETE,"/hmsapi/patients/**").hasAuthority(Role.ADMIN.name());

        //Staff
        http.authorizeRequests().antMatchers(HttpMethod.PUT,"/hmsapi/staff/**").hasAuthority(Role.ADMIN.name());
        http.authorizeRequests().antMatchers(HttpMethod.DELETE,"/hmsapi/staff/**").hasAuthority(Role.ADMIN.name());
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/hmsapi/staff/**").hasAuthority(Role.ADMIN.name());

        //Departments
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/hmsapi/departments/**").hasAnyAuthority(Role.ADMIN.name(),Role.RECEPTIONIST.name());
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/hmsapi/departments/**").hasAnyAuthority(Role.ADMIN.name());

        //Appointments
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/hmsapi/appointments/**").hasAnyAuthority(Role.ADMIN.name(),Role.RECEPTIONIST.name());
        http.authorizeRequests().antMatchers(HttpMethod.PUT,"/hmsapi/appointments/{id}").hasAnyAuthority(Role.ADMIN.name(),Role.DOCTOR.name());
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/hmsapi/appointments/download/{id}").permitAll();
        http.authorizeRequests().anyRequest().authenticated();

        http.addFilter(new CustomAuthenticationFilter(authenticationManagerBean()));
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Allowing Cors requests
     * */
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> allowOrigins = Arrays.asList("*");
        configuration.setAllowedOriginPatterns(allowOrigins);
//        configuration.setAllowedOrigins(allowOrigins);
        configuration.setAllowedMethods(singletonList("*"));
        configuration.setAllowedHeaders(singletonList("*"));
        //in case authentication is enabled this flag MUST be set, otherwise CORS requests will fail
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
