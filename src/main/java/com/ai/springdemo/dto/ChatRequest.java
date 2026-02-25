package com.ai.springdemo.dto;

import java.util.List;

public class ChatRequest {
    private String message;
    private String model;
    private String systemPrompt;
    private List<ConversationMessage> conversationHistory;
    private ChatOptions options;

    public static class ChatOptions {
        private Double temperature;
        private Integer maxTokens;
        private Double topP;
        private Double presencePenalty;
        private Boolean stream;

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }

        public Integer getMaxTokens() {
            return maxTokens;
        }

        public void setMaxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
        }

        public Double getTopP() {
            return topP;
        }

        public void setTopP(Double topP) {
            this.topP = topP;
        }

        public Double getPresencePenalty() {
            return presencePenalty;
        }

        public void setPresencePenalty(Double presencePenalty) {
            this.presencePenalty = presencePenalty;
        }

        public Boolean getStream() {
            return stream;
        }

        public void setStream(Boolean stream) {
            this.stream = stream;
        }
    }

    public static class ConversationMessage {
        private String role; // "user", "assistant", "system"
        private String content;

        public ConversationMessage() {}

        public ConversationMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public List<ConversationMessage> getConversationHistory() {
        return conversationHistory;
    }

    public void setConversationHistory(List<ConversationMessage> conversationHistory) {
        this.conversationHistory = conversationHistory;
    }

    public ChatOptions getOptions() {
        return options;
    }

    public void setOptions(ChatOptions options) {
        this.options = options;
    }
}
