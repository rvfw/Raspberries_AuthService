package com.example.raspberriesAuth.dto;

import lombok.Data;
import org.springframework.http.HttpStatusCode;

import java.time.Instant;

@Data
public class ErrorResponse {
    private HttpStatusCode httpStatusCode;
    private String reason;
    private Instant timestamp=Instant.now();
    public ErrorResponse(HttpStatusCode httpStatusCode, String reason) {
        this.httpStatusCode = httpStatusCode;
        this.reason = reason;
    }
}
