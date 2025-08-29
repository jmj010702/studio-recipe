package com.recipe;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
@Log4j2
public class RecommendedApplication implements ApplicationRunner {

    private final JobLauncher jobLauncher;
    private final Job recipeDataMigrationJob;

	public static void main(String[] args) {
		SpringApplication.run(RecommendedApplication.class, args);
	}


    @Override
    public void run(ApplicationArguments args) throws Exception {
        //Job 실행 시 Job 인스턴스 구분하기 위한 고유 값
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("jobId", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        try {
            jobLauncher.run(recipeDataMigrationJob,  jobParameters);
        } catch (Exception e) {
            log.error("Batch Job Error", e);
        }
    }
}
