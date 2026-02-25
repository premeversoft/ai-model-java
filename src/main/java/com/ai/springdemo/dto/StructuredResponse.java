package com.ai.springdemo.dto;

import java.util.List;

public record StructuredResponse(
        String topic,
        String summary,
        List<String> keyPoints,
        List<String> steps,
        List<String> risks,
        List<String> references
) {
}
