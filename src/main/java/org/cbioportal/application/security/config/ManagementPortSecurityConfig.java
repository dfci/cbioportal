package org.cbioportal.application.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

// Actuator endpoints on the dedicated management port (meant to be reachable only
// from inside the deployment network) are exempt from the portal's login requirements.
@Configuration
@ConditionalOnExpression(
    "'${management.server.port:}' != '' && '${management.server.port:}' != '${server.port:8080}'")
public class ManagementPortSecurityConfig {

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public SecurityFilterChain managementPortFilterChain(
      HttpSecurity http, @Value("${management.server.port}") int managementPort) throws Exception {
    RequestMatcher actuatorPath = PathPatternRequestMatcher.withDefaults().matcher("/actuator/**");
    return http.securityMatcher(
            request -> request.getLocalPort() == managementPort && actuatorPath.matches(request))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .build();
  }
}
