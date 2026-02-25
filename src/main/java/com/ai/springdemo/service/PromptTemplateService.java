package com.ai.springdemo.service;

import com.ai.springdemo.dto.PromptTemplate;
import com.ai.springdemo.dto.PromptTemplate.TemplateVariable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class PromptTemplateService {

    public List<PromptTemplate> getTemplates() {
        return List.of(
                new PromptTemplate(
                        "code-review",
                        "Code Reviewer",
                        "Review code for bugs, security, and style issues.",
                        loadTemplate("prompts/code-review.st"),
                        List.of(
                                new TemplateVariable("language", "Programming Language", "e.g., Java, Python", true),
                                new TemplateVariable("focus", "Focus Area", "e.g., security, performance, style", true),
                                new TemplateVariable("code", "Code Snippet", "Paste your code here", true)
                        )
                ),
                new PromptTemplate(
                        "learning-plan",
                        "Learning Plan",
                        "Generate a structured learning plan for a topic.",
                        loadTemplate("prompts/learning-plan.st"),
                        List.of(
                                new TemplateVariable("topic", "Topic", "e.g., Spring Boot", true),
                                new TemplateVariable("level", "Level", "e.g., beginner, intermediate", true),
                                new TemplateVariable("duration", "Duration (weeks)", "e.g., 4", true)
                        )
                ),
                new PromptTemplate(
                        "product-spec",
                        "Product Spec",
                        "Draft a concise product specification.",
                        loadTemplate("prompts/product-spec.st"),
                        List.of(
                                new TemplateVariable("product", "Product", "e.g., AI chat app", true),
                                new TemplateVariable("audience", "Audience", "e.g., developers", true)
                        )
                ),
                new PromptTemplate(
                        "rewrite",
                        "Rewrite with Tone",
                        "Rewrite text with a specific tone and length.",
                        loadTemplate("prompts/rewrite.st"),
                        List.of(
                                new TemplateVariable("tone", "Tone", "e.g., professional, friendly", true),
                                new TemplateVariable("length", "Max Words", "e.g., 120", true),
                                new TemplateVariable("text", "Text", "Paste your text here", true)
                        )
                )
        );
    }

    private String loadTemplate(String resourcePath) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8).trim();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load prompt template: " + resourcePath, ex);
        }
    }
}
