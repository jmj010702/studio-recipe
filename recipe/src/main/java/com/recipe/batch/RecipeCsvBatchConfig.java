package com.recipe.batch;

import com.recipe.batch.RecipeCsvDto;
import com.recipe.domain.entity.Recipe;
import com.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RecipeCsvBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RecipeRepository recipeRepository;

    // 1️⃣ CSV 파일 읽기
    @Bean
    public FlatFileItemReader<RecipeCsvDto> csvReader() {
        FlatFileItemReader<RecipeCsvDto> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("data/recipe_data_241226.csv"));
        reader.setLinesToSkip(1); // 헤더 한 줄 건너뛰기
        reader.setEncoding("UTF-8");

        DefaultLineMapper<RecipeCsvDto> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(
            "rcpSno", "rcpTtl", "ckgNm", "rgtrId", "rgtrNm", "inqCnt", "rcmmCnt", "srapCnt", 
            "ckgMthActoNm", "ckgStaActoNm", "ckgMtrlActoNm", "ckgKndActoNm", "ckgIpdc", 
            "ckgMtrlCn", "ckgInbunNm", "ckgDodfNm", "ckgTimeNm", "firstRegDt", "rcpImgUrl"
        );

        BeanWrapperFieldSetMapper<RecipeCsvDto> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(RecipeCsvDto.class);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        reader.setLineMapper(lineMapper);

        return reader;
    }

    // 2️⃣ CSV 데이터를 Recipe Entity로 변환
    @Bean
    public ItemProcessor<RecipeCsvDto, Recipe> csvProcessor() {
        return csvDto -> {
            Recipe recipe = new Recipe();
            recipe.setRcpSno(csvDto.getRcpSno());
            recipe.setRcpTtl(csvDto.getRcpTtl());
            recipe.setCkgNm(csvDto.getCkgNm());
            recipe.setCkgDodfNm(csvDto.getCkgDodfNm());
            recipe.setCkgInbunNm(csvDto.getCkgInbunNm());
            recipe.setCkgTimeNm(csvDto.getCkgTimeNm());
            recipe.setCkgKndActoNm(csvDto.getCkgKndActoNm());
            recipe.setCkgMthActoNm(csvDto.getCkgMthActoNm());
            recipe.setCkgMtrlActoNm(csvDto.getCkgMtrlActoNm());
            recipe.setCkgMtrlCn(csvDto.getCkgMtrlCn());
            recipe.setRcpImgUrl(csvDto.getRcpImgUrl());

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDate date = LocalDate.parse(csvDto.getFirstRegDt(), formatter);
                recipe.setFirstRegDt(date.atStartOfDay());
            } catch (Exception e) {
                recipe.setFirstRegDt(LocalDateTime.now());
            }

            recipe.setInqCnt(csvDto.getInqCnt() != null ? csvDto.getInqCnt().intValue() : 0);
            recipe.setRcmmCnt(csvDto.getRcmmCnt() != null ? csvDto.getRcmmCnt().intValue() : 0);

            return recipe;
        };
    }

    // 3️⃣ DB에 저장 (중복 체크 로직 추가)
    @Bean
    public ItemWriter<Recipe> csvWriter() {
        return items -> {
            List<Recipe> newRecipes = new ArrayList<>();
            
            for (Recipe recipe : items) {
                // 이미 존재하는지 확인
                if (!recipeRepository.existsById(recipe.getRcpSno())) {
                    newRecipes.add(recipe);
                } else {
                    log.debug("⚠️ 이미 존재하는 레시피 스킵: {}", recipe.getRcpSno());
                }
            }
            
            if (!newRecipes.isEmpty()) {
                recipeRepository.saveAll(newRecipes);
                log.info("✅ 저장된 레시피 수: {}", newRecipes.size());
            } else {
                log.info("⚠️ 새로 저장할 레시피 없음 (모두 중복)");
            }
        };
    }

    // 4️⃣ Step 정의 (읽기 → 변환 → 저장)
    @Bean
    public Step importStep() {
        return new StepBuilder("importStep", jobRepository)
            .<RecipeCsvDto, Recipe>chunk(100, transactionManager)
            .reader(csvReader())
            .processor(csvProcessor())
            .writer(csvWriter())
            .build();
    }

    // 5️⃣ Job 정의
    @Bean
    public Job importRecipeJob() {
        return new JobBuilder("importRecipeJob", jobRepository)
            .start(importStep())
            .build();
    }
}