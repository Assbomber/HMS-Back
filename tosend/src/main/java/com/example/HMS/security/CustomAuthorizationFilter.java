package com.example.HMS.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.HMS.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.bcel.classfile.ConstantDouble;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * CustomAuthorizationFilter extends OncePerRequestFilter that overrides the doFilterInternal method
 * to intercept all incoming requests
 * */
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Doing Authorization check");
        log.info(request.getServletPath());
        if(request.getServletPath().equals("/login") || request.getServletPath().contains("/hmsapi/patients/open") || request.getServletPath().contains("hmsapi/appointments/download") || !request.getServletPath().contains("hmsapi") ){
            log.info("No check required");
            filterChain.doFilter(request,response);
        }else{
            String authorizationHeader=request.getHeader(AUTHORIZATION);
            if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")){
                try {
                    log.info("verifying JWT");
                    String token=authorizationHeader.substring("Bearer ".length());
                    Algorithm algorithm=Algorithm.HMAC256(Constants.JWT_KEY.getBytes());
                    JWTVerifier verifier= JWT.require(algorithm).build();
                    DecodedJWT decodedJWT = verifier.verify(token);
                    String id=decodedJWT.getSubject();
                    String [] roles=decodedJWT.getClaim("roles").asArray(String.class);
                    Collection<SimpleGrantedAuthority> authorities=new ArrayList();
                    Arrays.stream(roles).forEach(role->{
                        authorities.add(new SimpleGrantedAuthority(role));
                    });
                    UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(id,null,authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request,response);
                }catch (JWTDecodeException e){
                    log.error("Invalid JWT token");
                    Map<String,String> error=new HashMap();
                    response.setStatus(UNAUTHORIZED.value());
                    error.put("error","Authorization Token is Invalid");
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(),error);
                }
                catch(Exception e){
                    log.error("error: "+e.getMessage());
                    response.setStatus(FORBIDDEN.value());
                    Map<String,String> error=new HashMap();
                    error.put("error",e.getMessage());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(),error);
                }
            }else{
                log.error("JWT not present");
                response.setHeader("error","User Not Logged in");
                response.setStatus(UNAUTHORIZED.value());
                Map<String,String> error=new HashMap();
                error.put("error","User Not Logged In");
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),error);
            }

        }
    }
}
