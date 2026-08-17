package com.placementiq.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.List;

@Service
public class GeminiService {

    private final RestClient restClient;

    public GeminiService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    public String askGemini(String prompt) {

        String apiKey = System.getenv("GEMINI_API_KEY");

        System.out.println("GEMINI KEY PRESENT: " + (apiKey != null));
        System.out.println("GEMINI KEY LENGTH: " +
                (apiKey == null ? 0 : apiKey.length()));

        String structuredPrompt = """
                You are Placement IQ, an AI career analysis system.

                Analyze the candidate information provided by the user.

                IMPORTANT:
                Return ONLY valid JSON.
                Do NOT use Markdown.
                Do NOT use ```json.
                Do NOT add explanations outside the JSON.

                Use exactly this JSON structure:

                {
                  "matchPercentage": 0,
                  "matchLevel": "",
                  "summary": "",
                  "matchedSkills": [],
                  "partialSkills": [],
                  "missingSkills": [],
                  "resumeStrengths": [],
                  "resumeWeaknesses": [],
                  "improvements": [],
                  "learningRoadmap": [
                    {
                      "priority": 1,
                      "skill": "",
                      "reason": ""
                    }
                  ],
                  "interviewQuestions": [
                    {
                      "question": "",
                      "category": "",
                      "difficulty": ""
                    }
                  ]
                }

                Rules:
                - matchPercentage must be a number from 0 to 100.
                - matchedSkills must contain skills clearly present in the candidate information.
                - partialSkills must contain skills where the candidate has some knowledge but needs improvement.
                - missingSkills must contain important skills required for the selected role but not present.
                - resumeStrengths must contain specific strengths.
                - resumeWeaknesses must contain specific weaknesses.
                - improvements must contain practical resume improvements.
                - learningRoadmap must be ordered by priority.
                - interviewQuestions must be based on the candidate's resume and selected role.
                - Keep the response professional and concise.

                Candidate information / user request:

                """ + prompt;

        Map<String, Object> request = Map.of(
                "model", "gemini-3.6-flash",
                "input", structuredPrompt
        );

        Map response = restClient.post()
                .uri("/v1beta/interactions")
                .header("x-goog-api-key", apiKey)
                .header("Content-Type", "application/json")
                .body(request)
                .retrieve()
                .body(Map.class);

        List<Map<String, Object>> steps =
                (List<Map<String, Object>>) response.get("steps");

        Map<String, Object> modelOutput = steps.stream()
                .filter(step -> "model_output".equals(step.get("type")))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("No model output found"));

        List<Map<String, Object>> content =
                (List<Map<String, Object>>) modelOutput.get("content");

        Map<String, Object> textContent = content.stream()
                .filter(item -> "text".equals(item.get("type")))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("No text response found"));

        return textContent.get("text").toString();
    }
}