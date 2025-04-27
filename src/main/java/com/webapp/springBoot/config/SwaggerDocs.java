package com.webapp.springBoot.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.*;

@OpenAPIDefinition(
        info = @Info(
                title = "API приложения",
                description = "API управления приложением, пользователем и многим другим!"
        ),
        security = @SecurityRequirement(name = "bearer")
)
@SecurityScheme(
        name = "bearer",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer"
)
public class SwaggerDocs {
}
