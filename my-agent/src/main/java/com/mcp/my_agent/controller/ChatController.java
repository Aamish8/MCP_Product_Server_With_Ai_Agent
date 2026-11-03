package com.mcp.my_agent.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ChatController {
   private final ChatClient chatClient;
   @GetMapping("/chat")
   public String chat(@RequestParam String message) {
	   return chatClient.prompt()
			   .user(message)
			   .call()
			   .content();
   }
}
