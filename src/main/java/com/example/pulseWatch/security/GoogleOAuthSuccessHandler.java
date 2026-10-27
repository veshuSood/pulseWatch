
package com.example.pulseWatch.security;

import com.example.pulseWatch.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class GoogleOAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public GoogleOAuthSuccessHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        Boolean emailVerified = oauth2User.getAttribute("email_verified");

        if (email == null || name == null || !Boolean.TRUE.equals(emailVerified)) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Google account email is missing or not verified."
            );
            return;
        }

        try {
            String token = authService.loginWithGoogle(email, name);

            String encodedToken = URLEncoder.encode(
                    token,
                    StandardCharsets.UTF_8
            );

            response.sendRedirect(
                    frontendUrl + "/oauth-success?token=" + encodedToken
            );

        } catch (org.springframework.web.server.ResponseStatusException ex) {
            response.sendRedirect(
                    frontendUrl + "/?oauthError=" +
                            URLEncoder.encode(ex.getReason(), StandardCharsets.UTF_8)
            );
        }
    }
}
