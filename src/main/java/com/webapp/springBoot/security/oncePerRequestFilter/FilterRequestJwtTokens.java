package com.webapp.springBoot.security.oncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.springBoot.security.JWTConfig.*;
import com.webapp.springBoot.security.JWTConfig.Factory.DefaultAccessTokenFactory;
import com.webapp.springBoot.security.JWTConfig.Factory.DefaultRefreshTokenFactory;
import com.webapp.springBoot.security.service.CustomUsersDetailsService;
import com.webapp.springBoot.security.service.TokenUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

@Setter
@Getter
public class FilterRequestJwtTokens extends OncePerRequestFilter {

    private  final RequestMatcher requestMatcher = new AntPathRequestMatcher("/api/security/login", HttpMethod.POST.name());

    private  final SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();


    private  final Function<Authentication, RecordToken> refreshToken = new DefaultRefreshTokenFactory();

    private  final Function<RecordToken, RecordToken> accessToken = new DefaultAccessTokenFactory();

    private Function<RecordToken, String> refreshTokenStringSeriazble = Objects::toString;

    private Function<RecordToken, String> accessTokenStringSeriazble = Objects::toString;

    private final  ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        if(this.requestMatcher.matches(request)) {
            if (this.securityContextRepository.containsContext(request)) {
                SecurityContext context = this.securityContextRepository.loadDeferredContext(request).get();
                if (context != null && !(context.getAuthentication() instanceof PreAuthenticatedAuthenticationToken)) {
                    RecordToken refreshToken = this.refreshToken.apply(context.getAuthentication());
                    RecordToken accessToken = this.accessToken.apply(refreshToken);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    this.objectMapper.writeValue(response.getWriter(),
                            new Tokens(this.accessTokenStringSeriazble.apply(accessToken),this.refreshTokenStringSeriazble.apply(refreshToken)));
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}
