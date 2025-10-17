package com.recipe.service;

import com.recipe.domain.entity.Like;
import com.recipe.domain.entity.Recipe;
import com.recipe.domain.entity.User;
import com.recipe.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;

    @Transactional
    public void userLikeToRecipe(User user, Recipe recipe) {
        //레시피에 좋아요 증가 전에 악의적인 접근 및 검증, 좋아요 기록에 있는지
        Optional<Like> checkTheLike = likeRepository.findByUserAndRecipe(user, recipe);
        if(checkTheLike.isPresent()) {
            throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
        }

        Like like = Like.builder()
                .user(user)
                .recipe(recipe)
                .build();
        likeRepository.save(like);
    }
}
