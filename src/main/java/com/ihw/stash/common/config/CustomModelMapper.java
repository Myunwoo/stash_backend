package com.ihw.stash.common.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomModelMapper {
    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
