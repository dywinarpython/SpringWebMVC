package com.webapp.springBoot;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.*;

@OpenAPIDefinition(
        info = @Info(
                title = "API приложения",
                description = "API управления приложением, пользователем и многим другим!"
        ),
        security = @SecurityRequirement(name = "oauth2")
)
@SecurityScheme(
        name = "oauth2",
        type = SecuritySchemeType.OAUTH2,
        scheme = "bearer",
        bearerFormat = "JWT",
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "http://localhost:8080/realms/loop/protocol/openid-connect/auth",
                        tokenUrl = "http://localhost:8080/realms/loop/protocol/openid-connect/token",
                        scopes = {
                                @OAuthScope(name = "openid", description = "OpenID Scope"),
                                @OAuthScope(name = "profile", description = "User profile")
                        }
                )
        )
)
public class SwaggerDocs {
}
