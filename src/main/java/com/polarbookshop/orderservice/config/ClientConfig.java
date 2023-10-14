package com.polarbookshop.orderservice.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Bean
    public WebClient webClient(@NotNull ClientProperties clientProperties, WebClient.@NotNull Builder webBuilder) {
        return webBuilder
                .baseUrl(clientProperties.catalogServiceUri().toString())
                .build();
    }
    /**
     * Um objeto autoconfigurado pelo Spring Boot para construir beans WebClient
     * Configura a URL base do WebClient para a URL do Serviço de Catálogo definida como uma propriedade customizada
     */

}
