package com.ackerman;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration

public class SecurityConfiguration {
    @Bean

    public SecurityWebFilterChain  securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http
                .authorizeExchange((exchanges) ->exchanges
                .pathMatchers("/api/**").permitAll()
                //.pathMatchers("/api/**").authenticated()
                .anyExchange().authenticated());


        return http.build();
    }
}
