package com.recipe.service;

import com.recipe.domain.dto.Recipe.RecipeSimpleDTO;
import com.recipe.domain.entity.*;
import com.recipe.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class UserRecipeService {
    
    @Autowired
    private LikeRepository likeRepository;
    
    @Autowired
    private BookmarkRepository bookmarkRepository;
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // ========== 좋아요 관련 메서드 ==========
    
    /**
     * 사용자가 좋아요한 레시피 목록 조회 (DTO로 변환)
     */
    public List<RecipeSimpleDTO> getLikedRecipes(String userId) {
        log.info("사용자 {} 의 좋아요 레시피 조회", userId);
        
        List<Like> likes = likeRepository.findByUserId(userId);
        
        if (likes.isEmpty()) {
            log.info("사용자 {} 의 좋아요한 레시피가 없습니다", userId);
            return List.of();
        }
        
        List<RecipeSimpleDTO> recipes = likes.stream()
                .map(like -> {
                    Recipe recipe = like.getRecipe();
                    return RecipeSimpleDTO.builder()
                            .recipeId(recipe.getRcpSno())
                            .title(recipe.getRcpTtl())
                            .imageUrl(recipe.getRcpImgUrl())
                            .viewCount(recipe.getInqCnt())
                            .likeCount(recipe.getRcmmCnt())
                            .build();
                })
                .collect(Collectors.toList());
        
        log.info("사용자 {} 의 좋아요 레시피 {}개 조회 완료", userId, recipes.size());
        
        return recipes;
    }
    
    /**
     * 레시피 좋아요 추가
     */
    @Transactional
    public void addLike(String userId, Long rcpSno) {
        log.info("사용자 {} 가 레시피 {} 좋아요 추가", userId, rcpSno);
        
        // 이미 좋아요 했는지 확인
        if (likeRepository.existsByUserIdAndRcpSno(userId, rcpSno)) {
            log.warn("이미 좋아요한 레시피입니다. userId={}, rcpSno={}", userId, rcpSno);
            throw new IllegalStateException("이미 좋아요한 레시피입니다.");
        }
        
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 사용자입니다. userId={}", userId);
                    return new IllegalArgumentException("존재하지 않는 사용자입니다.");
                });
        
        // 레시피 조회
        Recipe recipe = recipeRepository.findById(rcpSno)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 레시피입니다. rcpSno={}", rcpSno);
                    return new IllegalArgumentException("존재하지 않는 레시피입니다.");
                });
        
        // 좋아요 추가
        Like like = Like.builder()
                .user(user)
                .recipe(recipe)
                .build();
        
        likeRepository.save(like);
        log.info("좋아요 추가 완료. userId={}, rcpSno={}", userId, rcpSno);
    }
    
    /**
     * 레시피 좋아요 취소
     */
    @Transactional
    public void removeLike(String userId, Long rcpSno) {
        log.info("사용자 {} 가 레시피 {} 좋아요 취소", userId, rcpSno);
        
        Like like = likeRepository.findByUserIdAndRcpSno(userId, rcpSno)
                .orElseThrow(() -> {
                    log.error("좋아요하지 않은 레시피입니다. userId={}, rcpSno={}", userId, rcpSno);
                    return new IllegalStateException("좋아요하지 않은 레시피입니다.");
                });
        
        likeRepository.delete(like);
        log.info("좋아요 취소 완료. userId={}, rcpSno={}", userId, rcpSno);
    }
    
    /**
     * 특정 레시피를 좋아요 했는지 확인
     */
    public boolean isLiked(String userId, Long rcpSno) {
        return likeRepository.existsByUserIdAndRcpSno(userId, rcpSno);
    }
    
    // ========== 북마크(찜) 관련 메서드 ==========
    
    /**
     * 사용자가 북마크한 레시피 목록 조회 (DTO로 변환)
     */
    public List<RecipeSimpleDTO> getBookmarkedRecipes(String userId) {
        log.info("사용자 {} 의 북마크 레시피 조회", userId);
        
        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(userId);
        
        if (bookmarks.isEmpty()) {
            log.info("사용자 {} 의 북마크한 레시피가 없습니다", userId);
            return List.of();
        }
        
        List<RecipeSimpleDTO> recipes = bookmarks.stream()
                .map(bookmark -> {
                    Recipe recipe = bookmark.getRecipe();
                    return RecipeSimpleDTO.builder()
                            .recipeId(recipe.getRcpSno())
                            .title(recipe.getRcpTtl())
                            .imageUrl(recipe.getRcpImgUrl())
                            .viewCount(recipe.getInqCnt())
                            .likeCount(recipe.getRcmmCnt())
                            .build();
                })
                .collect(Collectors.toList());
        
        log.info("사용자 {} 의 북마크 레시피 {}개 조회 완료", userId, recipes.size());
        
        return recipes;
    }
    
    /**
     * 레시피 북마크 추가
     */
    @Transactional
    public void addBookmark(String userId, Long rcpSno) {
        log.info("사용자 {} 가 레시피 {} 북마크 추가", userId, rcpSno);
        
        // 이미 북마크 했는지 확인
        if (bookmarkRepository.existsByUserIdAndRcpSno(userId, rcpSno)) {
            log.warn("이미 북마크한 레시피입니다. userId={}, rcpSno={}", userId, rcpSno);
            throw new IllegalStateException("이미 북마크한 레시피입니다.");
        }
        
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 사용자입니다. userId={}", userId);
                    return new IllegalArgumentException("존재하지 않는 사용자입니다.");
                });
        
        // 레시피 조회
        Recipe recipe = recipeRepository.findById(rcpSno)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 레시피입니다. rcpSno={}", rcpSno);
                    return new IllegalArgumentException("존재하지 않는 레시피입니다.");
                });
        
        // 북마크 추가
        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .recipe(recipe)
                .build();
        
        bookmarkRepository.save(bookmark);
        log.info("북마크 추가 완료. userId={}, rcpSno={}", userId, rcpSno);
    }
    
    /**
     * 레시피 북마크 취소
     */
    @Transactional
    public void removeBookmark(String userId, Long rcpSno) {
        log.info("사용자 {} 가 레시피 {} 북마크 취소", userId, rcpSno);
        
        Bookmark bookmark = bookmarkRepository.findByUserIdAndRcpSno(userId, rcpSno)
                .orElseThrow(() -> {
                    log.error("북마크하지 않은 레시피입니다. userId={}, rcpSno={}", userId, rcpSno);
                    return new IllegalStateException("북마크하지 않은 레시피입니다.");
                });
        
        bookmarkRepository.delete(bookmark);
        log.info("북마크 취소 완료. userId={}, rcpSno={}", userId, rcpSno);
    }
    
    /**
     * 특정 레시피를 북마크 했는지 확인
     */
    public boolean isBookmarked(String userId, Long rcpSno) {
        return bookmarkRepository.existsByUserIdAndRcpSno(userId, rcpSno);
    }
}