package ru.litvast.techtrackapi.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import ru.litvast.techtrackapi.dto.UserCreateDTO;

import java.util.List;

@Configuration
@OpenAPIDefinition
@RequiredArgsConstructor
public class OpenApiConfiguration {

    private final Environment environment;

    @Bean
    public OpenAPI defineOpenAPI() {
        Server server = new Server();
        String serverUrl = environment.getProperty("api.server.url");
        server.setUrl(serverUrl);
        server.setDescription("Development");

        final String securitySchemeName = "bearerAuth";
        OpenAPI openAPI = new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                );

        Contact myContact = new Contact();
        myContact.setName("Богдан Литвинов");
        myContact.setEmail("litvastplay2000@gmail.com");

        Info info = new Info()
                .title("API системы управления технической документацией и историей обслуживания оборудования")
                .version("1.0")
                .description("Этот API представляет эндпоинты для управления пользователями системы, создания различного оборудования, истории к ней, а также различной документации.")
                .contact(myContact);
        return openAPI.info(info).servers(List.of(server));
    }
}
