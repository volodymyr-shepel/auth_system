package com.ackerman.config;


import com.ackerman.util.AdminAuthenticationFilter;
import com.ackerman.util.CustomerAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// Used to configure routes which are accessible through api gateway

@Configuration
public class GatewayConfig {

    @Autowired
    private CustomerAuthenticationFilter customerAuthenticationFilter;

    @Autowired
    private AdminAuthenticationFilter adminAuthenticationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("registration-service-route", r -> r.path("/api/auth/**")
                        .uri("lb://AUTHENTICATION"))


                .route("email-service-route", r -> r.path("/api/email/**")
                        //.filters(f->f.filter(customerAuthenticationFilter.apply(new CustomerAuthenticationFilter.Config())))
                        .uri("lb://EMAIL"))
                .route("customer-service-route", r -> r.path("/api/customer/**")
                        .filters(f->f.filter(customerAuthenticationFilter.apply(new CustomerAuthenticationFilter.Config())))
                        .uri("lb://CUSTOMER-SERVICE"))
                .route("admin-service-route", r -> r.path("/api/admin/**")
                        .filters(f->f.filter(adminAuthenticationFilter.apply(new AdminAuthenticationFilter.Config())))
                        .uri("lb://ADMIN-SERVICE"))
                .build();
    }
}