package com.pathfinder.server.auth;

import com.pathfinder.server.auth.jwt.service.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.pathfinder.server.auth.jwt.filter.*;
import com.pathfinder.server.auth.jwt.handler.*;


import java.util.List;

import static com.pathfinder.server.auth.utils.AuthConstant.*;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;

    @Value("${frontend.base-url}")
    private String frontBaseUrl;

    @Value("${backend.base-url}")
    private String backBaseUrl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{

        httpSecurity
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors(getCorsConfigurerCustomizer())
                .headers().frameOptions().disable();

        httpSecurity
                .apply(new CustomFilterConfigurer());

        httpSecurity.oauth2Login();

        httpSecurity.exceptionHandling()
                .accessDeniedHandler(new MemberAccessDeniedHandler())
                .authenticationEntryPoint(new MemberAuthenticationEntryPoint());

        httpSecurity.authorizeRequests(getAuthorizeRequestsCustomizer());

        return httpSecurity.build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public DefaultOAuth2UserService defaultOAuth2UserService() {
        return new DefaultOAuth2UserService();
    }

    private Customizer<ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry> getAuthorizeRequestsCustomizer() {
        return (requests) -> requests
                .antMatchers(HttpMethod.GET, "/").permitAll()
                .antMatchers("/auth/**").permitAll()
                .antMatchers(HttpMethod.GET, "/member/**").permitAll()
                .antMatchers(HttpMethod.POST, "/member/email/**").permitAll()
                .antMatchers(HttpMethod.GET, "/diary/**").permitAll()
                .antMatchers(HttpMethod.GET, "/tag/**").permitAll()
                .antMatchers("/scrap/**").permitAll()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated();
    }

    @Bean
    public Customizer<CorsConfigurer<HttpSecurity>> getCorsConfigurerCustomizer() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(frontBaseUrl);
        configuration.addAllowedOrigin(backBaseUrl);
        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of(AUTHORIZATION, REFRESH, LOCATION));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return corsConfigurer -> corsConfigurer.configurationSource(source);
    }

    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, tokenProvider);
            jwtAuthenticationFilter.setFilterProcessesUrl(AUTH_LOGIN_URL);
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());

            JwtRefreshFilter jwtRefreshFilter = new JwtRefreshFilter(tokenProvider, new MemberRefreshFailureHandler());
            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(tokenProvider);

            builder
                    .addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtRefreshFilter, JwtAuthenticationFilter.class)
                    .addFilterAfter(jwtVerificationFilter, JwtRefreshFilter.class);
        }
    }

}
