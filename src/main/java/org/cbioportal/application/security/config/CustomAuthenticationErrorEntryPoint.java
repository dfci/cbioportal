package org.cbioportal.application.security.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class CustomAuthenticationErrorEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      org.springframework.security.core.AuthenticationException authException)
      throws IOException, ServletException {
    response.sendRedirect(request.getContextPath() + "/login?error");
  }
}
