package br.acc.bank.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Sevencc Bank API")
                        .description(
                                "A Sevencc Bank API fornece endpoints para operações bancárias essenciais."
                                        +
                                        "Esta API é projetada para permitir que os clientes interajam com o sistema bancário de forma segura e eficiente. "
                                        +
                                        "Ela inclui autenticação baseada em JWT para proteger o acesso aos recursos e garantir que apenas usuários autorizados possam realizar operações. "
                                        +
                                        "A API é dividida em seções que cobrem o gerenciamento de agencias, clientes, contas, transações e outros recursos relevantes.")
                        .version("1.0"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .packagesToScan("br.acc")
                .build();
    }
}
