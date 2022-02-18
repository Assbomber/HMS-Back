package com.example.HMS.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.HMS.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * CustomAuthenticationFilter extends UsernamePasswordAuthentiationFilter from SpringSecurity
 * and then overrides three methods:
 * <li>attemptAuthentication()</li>
 * <li>successfullAuthentication()</li>
 * <li>unsuccessfullAuthentication()</li>
 *
 * to provide custom functionalities for each of them
 * */
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    CustomAuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager=authenticationManager;
    }

    /**
     * Overriding attemptAuthentication to pull out user ID and password from request then sending it for authentication
     * */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        return super.attemptAuthentication(request, response);
        String id=request.getParameter("id");
        String password=request.getParameter("password");
        log.info("User {} attempted Login ",id);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(id,password);
        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    /**
     * Overriding successfulAuthentication to send the client back the JWT token on successful auth
     * */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
//        super.successfulAuthentication(request, response, chain, authResult);
        log.info("Login successful");
        User user =(User) authentication.getPrincipal();
        Algorithm algorithm=Algorithm.HMAC256(Constants.JWT_KEY.getBytes());
        String access_token= JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+10*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles",user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
        Map<String,String> token=new HashMap();
        token.put("token",access_token);
        token.put("role",user.getAuthorities().toString().replaceAll("[\\[\\]]",""));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(),token);
    }


    /**
     * Overriding unsuccessfulAuthentication to send the client back the failure response message
     * */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
//      super.unsuccessfulAuthentication(request, response, failed);
        log.info("Login failed");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        Map<String,String> token=new HashMap();
        token.put("status", HttpStatus.UNAUTHORIZED.toString());
        token.put("error", failed.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(),token);
    }
}
