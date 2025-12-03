package com.recipe.domain.dto;

import com.recipe.algorithm.RecommendationResult;
import com.recipe.domain.entity.Recipe; // ⚠️ 패키지 경로 확인 (entity 패키지)
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 하나의 레시피 정보를 담는 DTO
 * (프론트엔드 호환성을 위해 필드명을 DB 컬럼명과 일치시킴: rcpTtl, rcpSno 등)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RecipeResponseDTO {

    private Long rcpSno;       // id -> rcpSno 변경
    private Long userId;       // 작성자 ID
    private String rcpTtl;     // title -> rcpTtl 변경
    private String ckgNm;      // chefName -> ckgNm 변경
    private String name;       // 닉네임
    private Integer rcmmCnt;   // likeCount -> rcmmCnt 변경
    private Integer inqCnt;    // 조회수 (추가됨)
    private String rcpImgUrl;  // imageUrl -> rcpImgUrl 변경
    private double score;      // 추천 점수
    private LocalDateTime firstRegDt; // 등록일 (추가됨)

    /* * 1. 추천 결과(RecommendationResult) -> DTO 변환 
     */
    public static RecipeResponseDTO from(RecommendationResult result) {
        Recipe recipe = result.getRecipe();
        return RecipeResponseDTO.builder()
                .rcpSno(recipe.getRcpSno())
                .rcpTtl(recipe.getRcpTtl())
                .ckgNm(recipe.getCkgNm())
                .rcmmCnt(recipe.getRcmmCnt())
                .inqCnt(recipe.getInqCnt())
                .rcpImgUrl(recipe.getRcpImgUrl())
                .score(result.getScore())
                .userId(recipe.getUserId())
                .firstRegDt(recipe.getFirstRegDt())
                .build();
    }

    /* * 2. 엔티티(Recipe) -> DTO 변환
     */
    public static RecipeResponseDTO fromEntity(Recipe recipe) {
        return RecipeResponseDTO.builder()
                .rcpSno(recipe.getRcpSno())
                .rcpTtl(recipe.getRcpTtl())
                .ckgNm(recipe.getCkgNm())
                .rcmmCnt(recipe.getRcmmCnt())
                .inqCnt(recipe.getInqCnt())
                .rcpImgUrl(recipe.getRcpImgUrl())
                .userId(recipe.getUserId())
                .firstRegDt(recipe.getFirstRegDt())
                .build();
    }
}