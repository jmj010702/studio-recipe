package com.recipe.repository;

import com.recipe.domain.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    // 기존 메서드
    List<Recipe> findTop10ByOrderByRcmmCntDesc();
    
    // ========== 검색 메서드 ==========
    
    /**
     * 레시피 제목, 요리명, 재료로 검색 (부분 일치)
     */
    @Query("SELECT r FROM Recipe r WHERE " +
           "r.rcpTtl LIKE %:keyword% OR " +
           "r.ckgNm LIKE %:keyword% OR " +
           "r.ckgMtrlCn LIKE %:keyword% " +
           "ORDER BY r.rcmmCnt DESC, r.rcpSno DESC")
    Page<Recipe> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 레시피 제목으로만 검색 (List 반환)
     */
    @Query("SELECT r FROM Recipe r WHERE r.rcpTtl LIKE %:keyword% ORDER BY r.rcmmCnt DESC")
    List<Recipe> findByTitleContaining(@Param("keyword") String keyword);
}