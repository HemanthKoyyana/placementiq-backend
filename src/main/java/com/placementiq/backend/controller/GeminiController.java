package com.placementiq.backend.controller;

import com.placementiq.backend.service.GeminiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/ask")
    public String ask(@RequestBody String prompt) {
        return geminiService.askGemini(prompt);
    }
}