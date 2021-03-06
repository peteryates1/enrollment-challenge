package com.challenge;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configure swagger.
 */
@Configuration
@Import(SpringDataRestConfiguration.class)
@EnableSwagger2
public class SwaggerConfiguration {}
