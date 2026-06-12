package com.tfm.bandas.surveys.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthConverter jwtAuthConverter;
  private final CorsConfigurationSource corsConfigurationSource;

  public SecurityConfig(
          JwtAuthConverter jwtAuthConverter,
          @Autowired(required = false) CorsConfigurationSource corsConfigurationSource) {
    this.jwtAuthConverter = jwtAuthConverter;
    this.corsConfigurationSource = corsConfigurationSource;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    var conv = new JwtAuthenticationConverter();
    conv.setJwtGrantedAuthoritiesConverter(jwtAuthConverter);
    var matcher = PathPatternRequestMatcher.withDefaults();

    http.csrf(AbstractHttpConfigurer::disable);

    if (corsConfigurationSource != null) {
      http.cors(cors -> cors.configurationSource(corsConfigurationSource));
    } else {
      http.cors(AbstractHttpConfigurer::disable);
    }

    http.authorizeHttpRequests(auth -> auth
                    // Endpoints públicos
                    .requestMatchers(matcher.matcher("/actuator/health")).permitAll()
                    .requestMatchers(matcher.matcher("/swagger-ui.html")).permitAll()
                    .requestMatchers(matcher.matcher("/swagger-ui/**")).permitAll()
                    .requestMatchers(matcher.matcher("/v3/api-docs/**")).permitAll()
                    // Respuestas: GET, POST, PUT, DELETE accesibles por ADMIN y MUSICIAN
                    .requestMatchers(matcher.matcher(HttpMethod.GET, "/api/surveys/responses/**")).hasAnyRole("ADMIN", "MUSICIAN")
                    .requestMatchers(matcher.matcher(HttpMethod.POST, "/api/surveys/responses/**")).hasAnyRole("ADMIN", "MUSICIAN")
                    .requestMatchers(matcher.matcher(HttpMethod.PUT, "/api/surveys/responses/**")).hasAnyRole("ADMIN", "MUSICIAN")
                    .requestMatchers(matcher.matcher(HttpMethod.DELETE, "/api/surveys/responses/**")).hasAnyRole("ADMIN", "MUSICIAN")
                    // Encuestas: GET accesible por ADMIN y MUSICIAN, resto solo ADMIN
                    .requestMatchers(matcher.matcher(HttpMethod.GET, "/api/surveys/**")).hasAnyRole("ADMIN", "MUSICIAN")
                    .requestMatchers(matcher.matcher(HttpMethod.POST, "/api/surveys/**")).hasRole("ADMIN")
                    .requestMatchers(matcher.matcher(HttpMethod.PUT, "/api/surveys/**")).hasRole("ADMIN")
                    .requestMatchers(matcher.matcher(HttpMethod.DELETE, "/api/surveys/**")).hasRole("ADMIN")
                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(conv)));

    return http.build();
  }
}