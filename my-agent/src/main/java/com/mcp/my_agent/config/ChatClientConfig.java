package com.mcp.my_agent.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {
 @Bean
 public ChatClient chatClient(ChatClient.Builder builder,ToolCallbackProvider provider) {
	 return builder
			 .defaultToolCallbacks(provider)
			 .defaultSystem("""
					    You manage a product database using the provided tools.
					    Always call a tool to read or change data. Never claim an action
					    succeeded unless a tool result confirms it.
					    """)
			 .build();
 }
}
