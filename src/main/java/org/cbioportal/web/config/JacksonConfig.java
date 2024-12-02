package org.cbioportal.web.config;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {
	static {
        
		StreamReadConstraints.overrideDefaultStreamReadConstraints(
            StreamReadConstraints.builder().maxStringLength(100000000).build()
        );
    }
	
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new CustomObjectMapper();
    }
}
