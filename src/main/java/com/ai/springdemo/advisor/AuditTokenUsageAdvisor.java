package com.ai.springdemo.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;

public class AuditTokenUsageAdvisor implements CallAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(AuditTokenUsageAdvisor.class);

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        ChatResponse chatResponse = chatClientResponse.chatResponse();

        if (chatResponse != null) {
            Usage usage = chatResponse.getMetadata().getUsage();
            if (usage != null) {
                int inputTokens = usage.getPromptTokens();
                int outputTokens = usage.getCompletionTokens();
                int totalTokens = usage.getTotalTokens();
                logger.info("Token Usage - input Tokens: {}, output Tokens: {}, Total Tokens: {}",
                        inputTokens, outputTokens, totalTokens);
            }
        }

        return chatClientResponse;
    }

    @Override
    public String getName() {
        return "AuditTokenUsageAdvisor";
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
