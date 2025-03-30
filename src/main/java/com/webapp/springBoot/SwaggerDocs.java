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
        security = @SecurityRequirement(name = "basic")
)
@SecurityScheme(
        name = "basic",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class SwaggerDocs {
}
