package org.cbioportal.security.config;

import java.io.IOException;

import javax.naming.AuthenticationException;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			org.springframework.security.core.AuthenticationException authException)
			throws IOException, ServletException {
//		if (authException instanceof OAuth2AuthenticationException) {
            response.sendRedirect(request.getContextPath() + "/login?test"); // Redirect to your custom error page
//        }
		
	}
}