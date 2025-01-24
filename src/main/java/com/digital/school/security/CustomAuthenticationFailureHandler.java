package com.digital.school.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        // Déterminez le type d'erreur et redirigez avec un paramètre
        if (exception.getMessage().contains("Bad credentials")) {
            response.sendRedirect("/login?error=bad_credentials");
        } else if (exception.getMessage().contains("User is disabled")) {
            response.sendRedirect("/login?error=disabled");
        } else if (exception.getMessage().contains("User account is locked")) {
            response.sendRedirect("/login?error=locked");
        } else if (exception.getMessage().contains("Credentials expired")) {
            response.sendRedirect("/login?error=credentials_expired");
        } else {
            response.sendRedirect("/login?error=unknown");
        }
    }
}