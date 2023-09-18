package com.ackerman.util;

import com.google.common.net.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticationFilter extends
        AbstractGatewayFilterFactory<AuthenticationFilter.Config> {



    @Autowired
    private RestTemplate template;

    @Autowired
    private JwtUtil jwtUtil;



    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new RuntimeException("missing authorization header");
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            }

            // now it is able to resolve the name of the server but not the problem is with blocking
            // change it or just move the validation process inside the api gateway?
            //template.getForObject("http://localhost:8083/api/auth/validate?token=" + authHeader, String.class);
            jwtUtil.validateToken(authHeader);

            return chain.filter(exchange);
        }));
    }

    public static class Config {

    }
}
