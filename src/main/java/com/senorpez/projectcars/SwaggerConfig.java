package com.senorpez.projectcars;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.HashSet;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfo(
                        "Project CARS Data API",
                        "Project CARS static data API.",
                        "0.1.0-rc1",
                        "http://projectcars.senorpez.com/tos.html",
                        new Contact("SenorPez", "http://senorpez.com/", "senorpez@senorpez.com"),
                        "MIT License",
                        "https://github.com/SenorPez/project-cars-data-api/blob/master/LICENSE"
                ))
                .host("projectcars.senorpez.com")
                .produces(new HashSet<>(Collections.singletonList("application/json")))
                .protocols(new HashSet<>(Collections.singletonList("http")))
                .tags(
                        new Tag("cars", "Project CARS Cars Data"),
                        new Tag("events", "Project CARS Events Data"),
                        new Tag("tracks", "Project CARS Tracks Data")
                )
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error")))
                .build();
    }
}
