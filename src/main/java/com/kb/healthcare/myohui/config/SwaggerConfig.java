package com.kb.healthcare.myohui.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local")
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("Auth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")
                )
            )
            .addSecurityItem(new SecurityRequirement().addList("Auth"))
            .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
            .title("KB Healthcare API")
            .description("KB 헬스케어 백엔드 API")
            .version("1.0.0");
    }
}