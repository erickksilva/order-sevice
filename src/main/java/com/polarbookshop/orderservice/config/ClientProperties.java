package com.polarbookshop.orderservice.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "polar")
public record ClientProperties(

        //essa variavel se refere ao valor de propriedade no app.yml
        @NotNull
        URI catalogServiceUri

) {}
