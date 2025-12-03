package com.recipe.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir:C:/project/studio-recipe/recipe/uploads/images}")
    private String uploadDir;

    public String saveImage(MultipartFile file) throws IOException {
        // 업로드 디렉토리가 없으면 생성
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 파일명 중복 방지 (UUID 사용)
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFilename = UUID.randomUUID().toString() + extension;

        // 파일 저장
        Path filePath = Paths.get(uploadDir, savedFilename);
        Files.write(filePath, file.getBytes());

        // 저장된 파일의 URL 반환 (예: /images/uuid.jpg)
        return "/images/" + savedFilename;
    }
}