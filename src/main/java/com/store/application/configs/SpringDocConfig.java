package com.store.application.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.customizers.OpenApiCustomizer;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Store Management API").version("1.0").description("APIs for managing the store"));
    }

    @Bean
    public OpenApiCustomizer removePaginationParameters() {
        return openApi -> openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
            if (operation.getParameters() != null) {
                operation.getParameters().removeIf(p -> "page".equals(p.getName()) || "size".equals(p.getName()) || "sort".equals(p.getName()));
            }
        }));
    }

    @Bean
    public OpenApiCustomizer limitExposedEndpoints() {
        return openApi -> {
            openApi.getPaths().entrySet().removeIf(entry ->
                    !entry.getKey().startsWith("/users") && !entry.getKey().startsWith("/products"));
        };
    }
}
