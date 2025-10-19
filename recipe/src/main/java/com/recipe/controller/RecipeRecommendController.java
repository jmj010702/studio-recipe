package com.recipe.controller;

import com.recipe.domain.entity.Recipe;
import com.recipe.repository.UserRepository;
import com.recipe.service.RecipeRecommendService;
import com.recipe.exceptions.user.UserExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecipeRecommendController {

    private final RecipeRecommendService recommendService;
    private final UserRepository userRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getRecommendations(@PathVariable Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> UserExceptions.NOT_FOUND.getUserException("해당 사용자를 찾을 수 없습니다."));

        List<Recipe> recommendations = recommendService.getRecommendedRecipes(userId);

        if (recommendations.isEmpty()) {
            return ResponseEntity.ok("추천 가능한 레시피가 없습니다.");
        }

        log.info("User {} 에게 추천된 레시피 개수: {}", userId, recommendations.size());
        return ResponseEntity.ok(recommendations);
    }
}
