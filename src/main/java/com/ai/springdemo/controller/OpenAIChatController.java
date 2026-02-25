package com.ai.springdemo.controller;

import com.ai.springdemo.dto.ChatRequest;
import com.ai.springdemo.dto.PromptTemplate;
import com.ai.springdemo.service.OpenAIChatService;
import com.ai.springdemo.service.PromptTemplateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/openai/api")
public class OpenAIChatController {


    private final OpenAIChatService openAIChatService;
    private final PromptTemplateService promptTemplateService;

    @Autowired
    public OpenAIChatController(OpenAIChatService openAIChatService, PromptTemplateService promptTemplateService) {
        this.openAIChatService = openAIChatService;
        this.promptTemplateService = promptTemplateService;
    }


    @GetMapping("/chat")
    public String chat(@RequestParam String message, @RequestParam(defaultValue = "ollama") String model) {
        return openAIChatService.chatWithLLM(message, model);
    }

    @PostMapping("/chat-with-roles")
    public String chatWithRoles(@RequestBody ChatRequest request) {
        return openAIChatService.chatWithRoles(request);
    }

    @PostMapping("/chat-advanced")
    public String chatAdvanced(@RequestBody ChatRequest request) {
        return openAIChatService.chatWithRoles(request);
    }

    @PostMapping(value = "/chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestBody ChatRequest request) {
        return openAIChatService.chatWithStream(request);
    }

    @GetMapping("/prompt-templates")
    public java.util.List<PromptTemplate> getPromptTemplates() {
        return promptTemplateService.getTemplates();
    }
}
