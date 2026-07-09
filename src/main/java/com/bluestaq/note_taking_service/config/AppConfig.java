package com.bluestaq.note_taking_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class AppConfig {

	@Value("${spring.data.web.pageable.max-page-size:100}")
	private int maxPageSize;
	
	@Value("${spring.data.web.pageable.default-page-size:20}")
    private int defaultPageSize;	
	
	@Bean
    PageableHandlerMethodArgumentResolverCustomizer paginationCustomizer() {
        return resolver -> {
            resolver.setMaxPageSize(maxPageSize);
            resolver.setFallbackPageable(PageRequest.of(0, defaultPageSize));
        };
    }

	public int getMaxPageSize() {
		return maxPageSize;
	}

	public int getDefaultPageSize() {
		return defaultPageSize;
	}
}
