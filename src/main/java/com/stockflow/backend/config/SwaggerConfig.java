package com.stockflow.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI stockFlowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Api rest for stock flow")
                        .version("1.0.0")
                        .description(descriptionApi()));
    }

    @Bean
    public GroupedOpenApi stockFlowApiGroup() {
        return GroupedOpenApi.builder()
                .group("stockflow")
                // equivalente a: .paths(PathSelectors.ant("/api/**"))
                .pathsToMatch("/api/**")
                .build();
    }

    private String descriptionApi() {
        return "StockFlow API is a REST API designed to manage product inventory and catalog data. "
                + "It allows clients to create, retrieve, update, and delete products in a consistent and reliable way, "
                + "including key information such as name, description, price, SKU, image URL, available stock, and creation date.\n\n"
                + "The API supports pagination, sorting, and name-based search, making it easy to browse large product catalogs "
                + "and power administrative interfaces such as internal dashboards or integrations with other systems. "
                + "Its goal is to keep inventory data accurate and accessible for operational processes like stock control, replenishment, and product management.";
    }
}

