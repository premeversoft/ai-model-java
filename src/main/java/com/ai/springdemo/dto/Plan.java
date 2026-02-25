package com.ai.springdemo.dto;

public record Plan(String from,
                   String to,
                   String transport,
                   String activities,
                   String accommodation,
                   String dateOfVisit) {
}
