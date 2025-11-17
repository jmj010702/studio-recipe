package com.recipe;

import io.github.cdimascio.dotenv.Dotenv;
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
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RecommendedApplication {
	public static void main(String[] args) {
        Dotenv env = Dotenv.configure().ignoreIfMissing().load();
        env.entries().forEach((entry) -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
		SpringApplication.run(RecommendedApplication.class, args);
	}
}
