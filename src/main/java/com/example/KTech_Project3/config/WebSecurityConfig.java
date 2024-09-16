package com.example.KTech_Project3.config;

import com.example.KTech_Project3.jwt.JwtTokenFilter;
import com.example.KTech_Project3.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsManager manager;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/users/register",
                                "/users/update",
                                "users/profile",
                                "users/business",
                                "/users/login",
                                "/users/{userId}/avatar",
                                "/items",
                                "/hello",
                                "/token/**"
                        )
                        .permitAll()

                        .requestMatchers("/users/update-user")
                        .hasRole("INACTIVE")

                        .requestMatchers(
                                "users/read-business",
                                "users//{id}/businessStatus",
                                "/shops/sub-list",
                                "/shops/{shopId}/refusal",
                                "/shops/{shopId}/delete"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                "/users/update-business"
                        )
                        .hasAnyRole("USER", "SELLER", "OFFER")

                        .requestMatchers(
                                "/items/{itemId}",
                                "/items/create",
                                "/items/{itemId}/offers"
                        )
                        .hasAnyRole("USER", "BUSINESS", "ADMIN", "OFFER")

                        .requestMatchers(
                                "/items/{itemId}",
                                "/items/{itemId}/update",
                                "/items/{itemId}/delete",
                                "/items/offer/read-seller",
                                "/items/{itemId}/response/{offerId}"
                        )
                        .hasRole("SELLER")

                        .requestMatchers(
                                "/items/offer/read-offer",
                                "/items/{itemId}/status/{offerId}")
                        .hasRole("OFFER")

                        .requestMatchers(
                                "/shops/{shopId}/update",
                                "/shops/request-open",
                                "/shops/{shopId}/request-delete",
                                "/shops/{shopId}/create",
                                "/shops/{shopId}/{goodsId}/update",
                                "/shops/{shopId}/{goodsId}/delete"
                        )
                        .hasRole("BUSINESS")

                        .requestMatchers("/shops/search")
                        .hasAnyRole("USER", "BUSINESS", "ADMIN", "SELLER", "OFFER")

                        .anyRequest()
                        .authenticated()

                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(
                        new JwtTokenFilter(jwtTokenUtils, manager),
                        AuthorizationFilter.class
                )
        ;
        return http.build();
    }
}
