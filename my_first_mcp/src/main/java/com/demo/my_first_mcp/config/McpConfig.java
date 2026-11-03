package com.demo.my_first_mcp.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.demo.my_first_mcp.service.ProductService;

@Configuration
public class McpConfig {
@Bean
public ToolCallbackProvider productTool(ProductService service) {
	return MethodToolCallbackProvider.builder()
			.toolObjects(service)
			.build();
}
}
