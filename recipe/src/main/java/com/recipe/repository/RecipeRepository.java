package com.recipe.repository;

import com.recipe.domain.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {
    
    // 기존 메서드 (인기순 10개)
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
    
    // ========== 사용자 관련 메서드 ==========

    /**
     * 사용자가 작성한 레시피 목록 조회 (최신순 정렬)
     * 마이페이지에서 "작성중/공개중" 탭에 사용됩니다.
     */
    List<Recipe> findAllByUserIdOrderByFirstRegDtDesc(Long userId);

    /**
     * 사용자가 작성한 레시피 조회 (간단한 버전)
     * MyPageService에서 사용됩니다.
     */
    List<Recipe> findByUserId(Long userId);  // 👈 이 메서드 추가!

    /**
     * 사용자가 작성한 레시피 일괄 삭제
     * 회원 탈퇴 시 호출됩니다.
     */
    void deleteByUserId(Long userId);
}