package com.recipe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class BatchController {
    
    private final JobLauncher jobLauncher;
    private final Job importRecipeJob;
    
    @PostMapping("/import-recipes")
    public ResponseEntity<String> importRecipes() {
        try {
            JobParameters params = new JobParametersBuilder()
                .addLocalDateTime("startTime", LocalDateTime.now())
                .toJobParameters();
                
            jobLauncher.run(importRecipeJob, params);
            return ResponseEntity.ok("CSV 데이터 임포트 완료!");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("실패: " + e.getMessage());
        }
    }
}