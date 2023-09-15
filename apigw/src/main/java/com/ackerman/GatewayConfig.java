package com.ackerman;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



// Used to configure routes which are accessible through api gateway
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("registration-service-route", r -> r.path("/api/registration/**")
                        .uri("lb://REGISTRATION"))

                .route("auth-service-route", r -> r.path("/api/auth/**")
                        .uri("lb://AUTHENTICATION"))

                .route("email-service-route", r -> r.path("/api/email/**")
                        .uri("lb://EMAIL"))
                .build();
    }

}