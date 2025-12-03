package com.recipe.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@Log4j2
public class ImageController {

    private static final String UPLOAD_DIR = "C:/project/studio-recipe/recipe/uploads/images/";

    // 원래대로 /images 경로 사용
    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        log.info("========================================");
        log.info("이미지 요청: {}", filename);
        
        try {
            Path filePath = Paths.get(UPLOAD_DIR + filename);
            Resource resource = new FileSystemResource(filePath);
            
            if (!resource.exists()) {
                log.error("❌ 파일이 존재하지 않음: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            // MIME 타입 자동 감지
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .body(resource);
                    
        } catch (IOException e) {
            log.error("❌ 에러 발생: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}