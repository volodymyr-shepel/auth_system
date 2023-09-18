package com.ackerman.config;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest serverRequest, ErrorAttributeOptions errorAttributeOptions) {

        Map<String, Object> errorResponse = super.getErrorAttributes(serverRequest, errorAttributeOptions);

        errorResponse.remove("requestId");
        //extract the status and put custom error message on the map
        HttpStatus status = HttpStatus.valueOf((Integer) errorResponse.get("status"));

        Throwable error = super.getError(serverRequest);
        if (error != null) {
            if (error instanceof ResponseStatusException responseStatusException) {
                // If it's a ResponseStatusException, use its reason (error message)
                errorResponse.put("error", responseStatusException.getReason());
            } else {
                // For other exceptions, use their message
                errorResponse.put("error", error.getMessage());
            }
        }

        return errorResponse;
    }
}

