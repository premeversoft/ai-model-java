package com.ai.springdemo.service;

import com.ai.springdemo.dto.StructuredOutputRequest;
import com.ai.springdemo.dto.StructuredResponse;
import com.ai.springdemo.dto.TripPlan;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StructuredOutputService {

    private final OpenAiChatModel openAiChatModel;
    private final OllamaChatModel ollamaChatModel;

    @Value("classpath:prompts/trip-guide-template.st")
    private Resource tripGuideTemplate;

    @Value("classpath:prompts/generic-structured-response.st")
    private Resource genericStructuredTemplate;

    @Value("classpath:prompts/generic-structured-response-user.st")
    private Resource genericStructuredUserTemplate;

    public StructuredOutputService(OpenAiChatModel openAiChatModel, OllamaChatModel ollamaChatModel) {
        this.openAiChatModel = openAiChatModel;
        this.ollamaChatModel = ollamaChatModel;
    }

    public TripPlan getTripPlan(StructuredOutputRequest request) {
        return createClient(request)
                .prompt()
                .system(tripGuideTemplate)
                .user(request.getMessage())
                .call()
                .entity(new BeanOutputConverter<>(TripPlan.class));
    }

        public StructuredResponse getStructuredResponse(StructuredOutputRequest request) {
        return createClient(request)
            .prompt()
            .system(genericStructuredTemplate)
            .user(promptUserSpec -> promptUserSpec.text(genericStructuredUserTemplate)
                .param("message", request.getMessage()))
            .call()
            .entity(new BeanOutputConverter<>(StructuredResponse.class));
        }

    public List<String> getTripSpots(StructuredOutputRequest request) {
        return createClient(request)
                .prompt()
                .user(request.getMessage())
                .call()
                .entity(new ListOutputConverter());
    }

    public Map<String, Object> getTripGuide(StructuredOutputRequest request) {
        return createClient(request)
                .prompt()
                .user(request.getMessage())
                .call()
                .entity(new MapOutputConverter());
    }

    public List<TripPlan> getCompleteTripPlans(StructuredOutputRequest request) {
        return createClient(request)
                .prompt()
                .user(request.getMessage())
                .call()
                .entity(new ParameterizedTypeReference<>() {});
    }

    private ChatClient createClient(StructuredOutputRequest request) {
        if (request != null && "ollama".equalsIgnoreCase(request.getModel())) {
            return ChatClient.create(ollamaChatModel);
        }
        return ChatClient.create(openAiChatModel);
    }
}
