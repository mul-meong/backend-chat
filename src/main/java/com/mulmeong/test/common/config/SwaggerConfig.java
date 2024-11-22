package com.mulmeong.test.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "ㅇㅇ 도메인 API", version = "v1",
                description = " {ㅇㅇ 도메인의 기능 나열} 서비스",
                termsOfService = "http://swagger.io/terms/")

)
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private static final String BEARER_TOKEN_PREFIX = "Bearer";

    //Authorize 버튼
    @Bean
    public OpenAPI openApi() {

        String securityJwtName = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securityJwtName);
        Components components = new Components()
                .addSecuritySchemes(securityJwtName, new SecurityScheme()
                        .name(securityJwtName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme(BEARER_TOKEN_PREFIX)
                        .bearerFormat(securityJwtName));

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components)
                // Swagger에서 요청보낼때 API에 추가되는 문자열
                .addServersItem(new Server().url("/ㅇㅇ-service"));
        //.addServersItem(new Server().url("/"));
    }

}