package com.ai.springdemo.dto;

import java.util.List;

public record TripPlan(String destination,
                       Integer totalDays,
                       List<Plan> plans) {
}
