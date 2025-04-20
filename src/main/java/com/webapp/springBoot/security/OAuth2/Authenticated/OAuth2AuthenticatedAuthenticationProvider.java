package com.webapp.springBoot.security.OAuth2.Authenticated;


import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;;

public class OAuth2AuthenticatedAuthenticationProvider implements AuthenticationProvider {
    private static final Log logger = LogFactory.getLog(OAuth2AuthenticatedAuthenticationProvider.class);
    private final UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
    @Setter
    private AuthenticationUserDetailsService<OAuth2AuthenticatedAuthenticationToken> preAuthenticatedUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!this.supports(authentication.getClass())) {
            return null;
        } else {
            logger.debug(LogMessage.format("OAuth2AuthenticatedAuthenticationProvider authentication request: %s", authentication));
            if (authentication.getPrincipal() == null) {
                throw new BadCredentialsException("Не переданны даныне о пользователе");
            } else if (authentication.getCredentials() == null) {
                logger.debug("");
                    throw new BadCredentialsException("Не переданы полномочия пользователя");
            } else {
                UserDetails userDetails = this.preAuthenticatedUserDetailsService.loadUserDetails((OAuth2AuthenticatedAuthenticationToken) authentication);
                this.userDetailsChecker.check(userDetails);
                OAuth2AuthenticatedAuthenticationToken result = new OAuth2AuthenticatedAuthenticationToken(userDetails, authentication.getCredentials(), userDetails.getAuthorities());
                result.setDetails(authentication.getDetails());
                return result;
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2AuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
