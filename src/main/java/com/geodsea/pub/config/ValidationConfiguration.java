package com.geodsea.pub.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class ValidationConfiguration {

    private final Logger log = LoggerFactory.getLogger(ValidationConfiguration.class);

    public ValidationConfiguration() {
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        log.debug("Configuring validator");
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        return bean;
    }

}
