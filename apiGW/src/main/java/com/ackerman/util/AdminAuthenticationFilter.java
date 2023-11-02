package com.ackerman.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class AdminAuthenticationFilter extends
        AbstractGatewayFilterFactory<AdminAuthenticationFilter.Config> {



    @Autowired
    private RestTemplate template;

    @Autowired
    private JwtUtil jwtUtil;



    public AdminAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            try{
                String authHeader = JwtUtil.extractAuthToken(exchange);

                jwtUtil.validateToken(authHeader);

                // Extract roles from claims
                jwtUtil.checkUserRole(authHeader,UserRole.ADMIN);

                return chain.filter(exchange);
            }
            catch(Exception e){
                URI loginPage = UriComponentsBuilder.fromPath("/api/ui/login").build().toUri();
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().setLocation(loginPage);
                return response.setComplete();
            }



        }));
    }

    public static class Config {

    }
}
