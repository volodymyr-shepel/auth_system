package com.ackerman.config;


import com.ackerman.util.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// Used to configure routes which are accessible through api gateway

@Configuration
public class GatewayConfig {

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("registration-service-route", r -> r.path("/api/auth/**")
                        .uri("lb://AUTHENTICATION"))


                .route("email-service-route", r -> r.path("/api/email/**")
                        .filters(f->f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://EMAIL"))
                .build();
    }
}