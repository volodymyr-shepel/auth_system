package com.ackerman.clients;

import feign.Capability;
import feign.micrometer.MicrometerCapability;
import org.springframework.context.annotation.Bean;


public class FeignConfiguration {
    @Bean
    public Capability capability() {
        return new MicrometerCapability();
    }
}
