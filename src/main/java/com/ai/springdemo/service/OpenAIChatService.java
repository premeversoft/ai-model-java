package com.ai.springdemo.service;

import com.ai.springdemo.advisor.AuditTokenUsageAdvisor;
import com.ai.springdemo.dto.ChatRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAIChatService {

    private final OpenAiChatModel openAiChatModel;
    private final OllamaChatModel ollamaChatModel;

    public OpenAIChatService(OpenAiChatModel openAiChatModel, OllamaChatModel ollamaChatModel) {
        this.openAiChatModel = openAiChatModel;
        this.ollamaChatModel = ollamaChatModel;
    }

    public String chatWithLLM(String message, String model) {
        if ("openai".equalsIgnoreCase(model)) {
            return ChatClient.create(openAiChatModel)
                    .prompt()
                    .advisors(List.of(new AuditTokenUsageAdvisor()))
                    .user(message)
                    .call()
                    .content();
        } else {
            // Default to Ollama
            return ChatClient.create(ollamaChatModel)
                    .prompt()
                    .advisors(List.of(new AuditTokenUsageAdvisor()))
                    .user(message)
                    .call()
                    .content();
        }
    }

    public String chatWithRoles(ChatRequest request) {
        if ("openai".equalsIgnoreCase(request.getModel())) {
            Prompt prompt = buildOpenAiPrompt(request);
            return ChatClient.create(openAiChatModel)
                    .prompt(prompt)
                    .options(buildOpenAiOptions(request))
                    .advisors(List.of(new AuditTokenUsageAdvisor()))
                    .call()
                    .content();
        }

        String ollamaPrompt = buildOllamaPrompt(request);
        return ChatClient.create(ollamaChatModel)
                .prompt()
                .options(buildOllamaOptions(request))
                .advisors(List.of(new AuditTokenUsageAdvisor()))
                .user(ollamaPrompt)
                .call()
                .content();
    }

    public Flux<String> chatWithStream(ChatRequest request) {
        if ("openai".equalsIgnoreCase(request.getModel())) {
            Prompt prompt = buildOpenAiPrompt(request);
            return ChatClient.create(openAiChatModel)
                    .prompt(prompt)
                    .options(buildOpenAiOptions(request))
                    .advisors(List.of(new AuditTokenUsageAdvisor()))
                    .stream()
                    .content();
        }

        String ollamaPrompt = buildOllamaPrompt(request);
        return ChatClient.create(ollamaChatModel)
                .prompt()
                .options(buildOllamaOptions(request))
                .advisors(List.of(new AuditTokenUsageAdvisor()))
                .user(ollamaPrompt)
                .stream()
                .content();
    }

    private Prompt buildOpenAiPrompt(ChatRequest request) {
        List<Message> messages = new ArrayList<>();

        if (request.getSystemPrompt() != null && !request.getSystemPrompt().isEmpty()) {
            messages.add(new SystemMessage(request.getSystemPrompt()));
        }

        if (request.getConversationHistory() != null) {
            for (ChatRequest.ConversationMessage msg : request.getConversationHistory()) {
                if (msg.getRole() == null) {
                    continue;
                }
                switch (msg.getRole().toLowerCase()) {
                    case "system":
                        messages.add(new SystemMessage(msg.getContent()));
                        break;
                    case "user":
                        messages.add(new UserMessage(msg.getContent()));
                        break;
                    case "assistant":
                        messages.add(new AssistantMessage(msg.getContent()));
                        break;
                }
            }
        }

        messages.add(new UserMessage(request.getMessage()));
        return new Prompt(messages);
    }

    private String buildOllamaPrompt(ChatRequest request) {
        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("You are a helpful assistant. Follow the SYSTEM INSTRUCTIONS strictly.\n\n");

        if (request.getSystemPrompt() != null && !request.getSystemPrompt().isEmpty()) {
            contextBuilder.append("SYSTEM INSTRUCTIONS:\n")
                    .append(request.getSystemPrompt())
                    .append("\n\n");
        }

        if (request.getConversationHistory() != null) {
            contextBuilder.append("CONVERSATION HISTORY:\n");
            for (ChatRequest.ConversationMessage msg : request.getConversationHistory()) {
                if (msg.getRole() == null) {
                    continue;
                }
                if ("user".equalsIgnoreCase(msg.getRole())) {
                    contextBuilder.append("User: ").append(msg.getContent()).append("\n");
                } else if ("assistant".equalsIgnoreCase(msg.getRole())) {
                    contextBuilder.append("Assistant: ").append(msg.getContent()).append("\n");
                } else if ("system".equalsIgnoreCase(msg.getRole())) {
                    contextBuilder.append("System: ").append(msg.getContent()).append("\n");
                }
            }
            contextBuilder.append("\n");
        }

        contextBuilder.append("USER REQUEST:\n")
                .append(request.getMessage())
                .append("\n\nASSISTANT RESPONSE:\n");
        return contextBuilder.toString();
    }

    private OpenAiChatOptions buildOpenAiOptions(ChatRequest request) {
        ChatRequest.ChatOptions options = request.getOptions();
        OpenAiChatOptions.Builder builder = OpenAiChatOptions.builder();
        if (options != null) {
            if (options.getTemperature() != null) {
                builder.temperature(options.getTemperature());
            }
            if (options.getMaxTokens() != null) {
                builder.maxTokens(options.getMaxTokens());
            }
            if (options.getTopP() != null) {
                builder.topP(options.getTopP());
            }
            if (options.getPresencePenalty() != null) {
                builder.presencePenalty(options.getPresencePenalty());
            }
        }
        return builder.build();
    }

    private OllamaChatOptions buildOllamaOptions(ChatRequest request) {
        ChatRequest.ChatOptions options = request.getOptions();
        OllamaChatOptions ollamaOptions = new OllamaChatOptions();
        if (options != null) {
            if (options.getTemperature() != null) {
                ollamaOptions.setTemperature(options.getTemperature());
            }
            if (options.getTopP() != null) {
                ollamaOptions.setTopP(options.getTopP());
            }
            if (options.getMaxTokens() != null) {
                ollamaOptions.setMaxTokens(options.getMaxTokens());
            }
        }
        return ollamaOptions;
    }
}
