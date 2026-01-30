package com.backend_fullstep.common;

import com.backend_fullstep.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.LocalDate;

public class SecurityResponseUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void writeForbidden(
            HttpServletRequest request,
            HttpServletResponse response,
            String message
    ) {
        try {
            ErrorResponse error = ErrorResponse.builder()
                    .status(403)
                    .error("FORBIDDEN")
                    .message(message)
                    .path(request.getRequestURI())
                    .timestamp(LocalDate.now())
                    .build();

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            mapper.writeValue(response.getOutputStream(), error);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
