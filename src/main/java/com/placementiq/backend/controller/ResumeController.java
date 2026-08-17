package com.placementiq.backend.controller;

import com.placementiq.backend.service.GeminiService;
import com.placementiq.backend.service.ResumeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final ResumeService resumeService;
    private final GeminiService geminiService;

    public ResumeController(
            ResumeService resumeService,
            GeminiService geminiService) {

        this.resumeService = resumeService;
        this.geminiService = geminiService;
    }

    @PostMapping("/upload")
    public String uploadResume(@RequestParam("file") MultipartFile file) {

        try {
            String text = resumeService.extractText(file);

            return text;

        } catch (Exception e) {
            return "Error reading resume: " + e.getMessage();
        }
    }

    @PostMapping("/analyze")
    public String analyzeResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("role") String role) {

        try {

            // 1. Extract text from PDF
            String resumeText = resumeService.extractText(file);

            // 2. Create prompt for Gemini
           String prompt = """
        You are an expert AI career and resume analysis system for a platform called Placement IQ.

        Analyze the candidate's resume specifically for the job role:
        %s

        IMPORTANT ANALYSIS RULES:

        1. Analyze ONLY the information explicitly present in the resume.
        2. Do NOT invent skills, experience, projects, technologies, or achievements.
        3. Do NOT mark a skill as missing simply because it is not clearly mentioned in a project.
        4. A skill is "matched" only when the resume provides clear evidence that the candidate has that skill.
        5. A skill is "partial" when the resume shows related knowledge or experience but does not clearly demonstrate the complete skill.
        6. A skill is "missing" when it is important for the selected role and there is no evidence of it anywhere in the resume.
        7. Evaluate the candidate against realistic requirements for the selected job role.
        8. Give more importance to actual projects, internships, work experience, and technical skills than generic academic subjects.
        9. Do not penalize the candidate for technologies that are optional or not normally required for an entry-level version of the selected role.
        10. The match percentage must be based on the overall evidence in the resume, not on the number of keywords alone.
        11. Keep the analysis realistic for a fresher/entry-level candidate when applicable.
        12. Do not recommend technologies simply because they are popular. Recommend them only when they are relevant to the selected role and useful for closing an actual skill gap.
            IMPORTANT:
Evaluate the uploaded resume exactly as written.

If a technology appears anywhere in the resume, including:
- Technical Skills
- Projects
- Internship
- Education
- Certifications

then consider that evidence.

Do not mark a technology as missing if it is explicitly mentioned anywhere in the resume.

For example, if MySQL is mentioned in a project or technical-skills section, it must NOT appear in missingSkills.

Do not infer that a skill is missing merely because the resume does not describe how deeply it was used.

        OUTPUT REQUIREMENTS:

        Return ONLY valid JSON.
        Do NOT include Markdown.
        Do NOT include ```json.
        Do NOT include introductory or concluding text.

        Use exactly this structure:

        {
          "matchPercentage": 0,
          "matchLevel": "Low Match",
          "summary": "Short professional summary of the candidate's suitability for the selected role.",
          "matchedSkills": [
            "skill supported by clear evidence in the resume"
          ],
          "partialSkills": [
            "related skill where the resume provides incomplete evidence"
          ],
          "missingSkills": [
            "important role-relevant skill not found in the resume"
          ],
          "resumeStrengths": [
            "specific strength supported by the resume"
          ],
          "resumeWeaknesses": [
            "specific weakness supported by the resume"
          ],
          "improvements": [
            "specific actionable improvement"
          ],
          "learningRoadmap": [
            {
              "priority": 1,
              "skill": "Skill to learn",
              "reason": "Why this skill is important for this candidate and role."
            }
          ],
          "interviewQuestions": [
  {
    "question": "How did you design the REST APIs in your Placement IQ project?",
    "category": "Project / REST API",
    "difficulty": "Medium"
  },
  {
    "question": "Why did you choose Spring Boot for the backend?",
    "category": "Spring Boot",
    "difficulty": "Easy"
  },
  {
    "question": "How does dependency injection work in Spring Boot?",
    "category": "Spring Boot",
    "difficulty": "Medium"
  }
]
        }

        MATCH LEVEL RULES:

        80-100 = Excellent Match
        65-79 = Strong Match
        50-64 = Moderate Match
        35-49 = Weak Match
        0-34 = Low Match

        MATCH PERCENTAGE RULES:

        - Consider technical skills.
        - Consider projects.
        - Consider internship/work experience.
        - Consider education where relevant.
        - Consider role-specific technologies.
        - Consider practical evidence rather than keyword frequency.
        - Do not give a high score merely because the resume contains many generic skills.

        LEARNING ROADMAP:

        Provide 3-5 skills in priority order.
        Prioritize the most important gaps first.
        Do not recommend skills that the candidate already clearly demonstrates.

       INTERVIEW QUESTIONS:

Generate a minimum of 15 interview questions.

Generate MORE than 15 questions when the resume contains enough relevant information.
The goal is to comprehensively prepare the candidate for an interview, not to limit the number of questions.

Cover as many relevant areas as possible from the resume and selected job role.

The questions should be distributed across these categories where applicable:

1. Resume Introduction
   - Questions about the candidate's background and experience.

2. Resume Skills
   - Questions about every important technical skill explicitly mentioned in the resume.

3. Projects
   - Questions about every major project.
   - Project purpose
   - Architecture
   - Technologies used
   - Candidate's contribution
   - Difficulties faced
   - Design decisions
   - Database
   - APIs
   - Authentication
   - Performance
   - Testing
   - Deployment
   - Possible improvements

4. Internship / Work Experience
   - Responsibilities
   - Technologies used
   - Problems solved
   - Contributions
   - Technical decisions
   - Challenges

5. Programming Languages
   - Questions about languages mentioned in the resume.
   - Include practical and conceptual questions.

6. DSA
   - Ask relevant DSA questions when DSA is mentioned in the resume.
   - Include concepts, complexity, and practical problem-solving questions.

7. Database / SQL
   - Questions about databases mentioned in the resume.
   - SQL queries, normalization, indexing, relationships, transactions, optimization, etc.

8. Backend Development
   - REST APIs
   - HTTP methods
   - Status codes
   - API design
   - Exception handling
   - Authentication
   - Authorization
   - Validation
   - Security

9. Frameworks and Libraries
   - Ask questions about frameworks and libraries explicitly mentioned in the resume.

10. AI / API Integration
   - If AI, Gemini, OpenAI, APIs, or other integrations are mentioned, ask detailed questions about them.

11. Computer Science Fundamentals
   - Ask relevant questions from OS, DBMS, Computer Networks, OOP, System Design, etc. when these subjects are relevant to the resume or selected role.

12. Behavioral / HR
   - Questions about strengths, weaknesses, challenges, teamwork, leadership, failures, learning, career goals, and project ownership.

13. Role-Specific Questions
   - Ask questions that are commonly expected for the selected job role.

14. Scenario-Based Questions
   - Give practical situations and ask how the candidate would solve them.

15. Follow-Up Questions
   - Ask deeper questions that an interviewer could ask after the candidate explains a project, skill, or technology.

IMPORTANT:

- Questions must be based on information actually present in the resume whenever the question claims to be resume-specific.
- Do NOT invent projects, internships, technologies, companies, achievements, or experience.
- Avoid asking duplicate questions.
- Questions should range from Easy to Medium to Hard.
- Include the difficulty for every question.
- Include the category for every question.
- Prioritize questions about projects and technologies that are most relevant to the selected job role.
- The questions should simulate a real technical interview.
- The candidate should be able to use this question set to thoroughly prepare for an interview.

Generate at least 15 questions.
If the resume contains enough information, generate 20, 25, or more questions.
There is NO fixed maximum as long as the questions remain relevant and non-duplicative.

        RESUME:

        %s
        """.formatted(role, resumeText);

            // 3. Send resume + role to existing Gemini service
            return geminiService.askGemini(prompt);

        } catch (Exception e) {

            return "Error analyzing resume: " + e.getMessage();
        }
    }
}