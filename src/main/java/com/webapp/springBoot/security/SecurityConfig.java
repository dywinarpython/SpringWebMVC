package com.webapp.springBoot.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.webapp.springBoot.security.JWTConfig.Deserializer.AccessTokenJWTStringDeserializer;
import com.webapp.springBoot.security.JWTConfig.Deserializer.RefreshTokenJWEStringDeserializer;
import com.webapp.springBoot.security.JWTConfig.Seriazble.AccessTokenJWTStringSeriazler;
import com.webapp.springBoot.security.JWTConfig.Seriazble.RefreshTokenJWEStringSeriazler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.text.ParseException;


@EnableMethodSecurity(securedEnabled = true)
@Configuration
public class SecurityConfig{


    @Bean
    public ConfigureJWTAuthetication configureJWTAuthetication(
            @Value("${spring.jwt.access-token-key}") String accessToken,
            @Value("${spring.jwt.refresh-token-key}") String refreshToken
    ) throws ParseException, JOSEException {
        return new ConfigureJWTAuthetication()
                .setAccessTokenStringSeriazble(new AccessTokenJWTStringSeriazler(new MACSigner(OctetSequenceKey.parse(accessToken))))
                .setRefreshTokenStringSeriazble((new RefreshTokenJWEStringSeriazler(new DirectEncrypter(OctetSequenceKey.parse(refreshToken)))))
                .setAccessTokenDesiriazle(new AccessTokenJWTStringDeserializer(new MACVerifier(OctetSequenceKey.parse(accessToken))))
                .setRefreshTokenDesiriazle(new RefreshTokenJWEStringDeserializer(new DirectDecrypter(OctetSequenceKey.parse(refreshToken))
                ));

    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ConfigureJWTAuthetication configureJWTAuthetication, @Value("${antPathRequestMatcher.Notauthinicated}") String noAuthinicated, @Autowired CorsConfigurationSource cors) throws Exception {
        String [] noAuthenticatedArray = noAuthinicated.split(", ");
        CorsFilter corsFilter = new CorsFilter(cors);
        http
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                {
                    authorizationManagerRequestMatcherRegistry.requestMatchers(noAuthenticatedArray).permitAll();
                    authorizationManagerRequestMatcherRegistry.anyRequest().authenticated();})
                .requiresChannel(channelRequestMatcherRegistry -> channelRequestMatcherRegistry.anyRequest().requiresSecure())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(AbstractHttpConfigurer::disable);
        http.addFilterBefore(corsFilter, SecurityContextHolderFilter.class);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.apply(configureJWTAuthetication);
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Primary
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Разрешить отправку кук и заголовков авторизации
        config.addAllowedOrigin("https://localhost:8443"); // Разрешенные домены (можно использовать "*" для всех, но это небезопасно)
        config.addAllowedHeader("*"); // Разрешенные заголовки
        config.addAllowedMethod("*"); // Разрешенные HTTP-методы (GET, POST и т.д.)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Применить ко всем endpoint'ам
        return source;
    }

}