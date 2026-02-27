package com.safemenu.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI safeMenuOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SafeMenu API")
                        .description("""
                                Digital Menu & Allergen Compliance Platform.
                                
                                Manages restaurant menus with automatic EU Regulation 1169/2011 
                                allergen detection. Dishes are composed of ingredients, each tagged 
                                with applicable allergens. The API auto-computes the full allergen 
                                profile for every dish and provides a safe-dining endpoint for 
                                customers to filter menus by their specific allergies.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SafeMenu")
                                .url("https://github.com/opsach/SafeMenu-API"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
