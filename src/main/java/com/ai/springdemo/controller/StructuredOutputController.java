package com.ai.springdemo.controller;

import com.ai.springdemo.dto.StructuredOutputRequest;
import com.ai.springdemo.dto.StructuredResponse;
import com.ai.springdemo.dto.TripPlan;
import com.ai.springdemo.service.StructuredOutputService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/openai/api/structured")
public class StructuredOutputController {

    private final StructuredOutputService structuredOutputService;

    public StructuredOutputController(StructuredOutputService structuredOutputService) {
        this.structuredOutputService = structuredOutputService;
    }

    @PostMapping("/trip-plan")
    public TripPlan getTripPlans(@RequestBody StructuredOutputRequest request) {
        return structuredOutputService.getTripPlan(request);
    }

    @PostMapping("/trip-spots")
    public List<String> getTripSpots(@RequestBody StructuredOutputRequest request) {
        return structuredOutputService.getTripSpots(request);
    }

    @PostMapping("/trip-guide")
    public Map<String, Object> getTripGuide(@RequestBody StructuredOutputRequest request) {
        return structuredOutputService.getTripGuide(request);
    }

    @PostMapping("/complete-trip-plans")
    public List<TripPlan> getCompleteTripPlans(@RequestBody StructuredOutputRequest request) {
        return structuredOutputService.getCompleteTripPlans(request);
    }

    @PostMapping("/answer")
    public StructuredResponse getStructuredResponse(@RequestBody StructuredOutputRequest request) {
        return structuredOutputService.getStructuredResponse(request);
    }
}
