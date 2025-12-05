package com.recipe.domain.dto;

import com.recipe.domain.entity.Recipe;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RecipeDTO {

    private Long rcpSno;                // 레시피 고유 번호
    private Long userId;                // ✅ 작성자 ID
    private String rcpTtl;              // 레시피 제목
    private String ckgNm;               // 요리명
    private Integer inqCnt;             // 조회수
    private Integer rcmmCnt;            // 추천(좋아요) 수
    private String ckgMthActoNm;        // 조리 방법 대분류
    private String ckgMtrlActoNm;       // 재료 대분류
    private String ckgKndActoNm;        // 음식 종류 대분류
    private String ckgMtrlCn;           // 재료 내용
    private String ckgInbunNm;          // 인분
    private String ckgDodfNm;           // 난이도
    private String ckgTimeNm;           // 조리 시간
    private LocalDateTime firstRegDt;   // 등록일
    private String rcpImgUrl;           // 이미지 URL

    // ✅ 별칭 필드 추가 (MyPageService 호환성을 위해)
    public Long getRecipeId() {
        return rcpSno;
    }

    public String getTitle() {
        return rcpTtl;
    }

    public String getImageUrl() {
        return rcpImgUrl;
    }

    public Integer getViewCount() {
        return inqCnt;
    }

    public Integer getLikeCount() {
        return rcmmCnt;
    }

    // ✅ Entity → DTO 변환
    public static RecipeDTO fromEntity(Recipe recipe) {
        if (recipe == null) return null;
        return RecipeDTO.builder()
                .rcpSno(recipe.getRcpSno())
                .userId(recipe.getUserId())
                .rcpTtl(recipe.getRcpTtl())
                .ckgNm(recipe.getCkgNm())
                .inqCnt(recipe.getInqCnt())
                .rcmmCnt(recipe.getRcmmCnt())
                .ckgMthActoNm(recipe.getCkgMthActoNm())
                .ckgMtrlActoNm(recipe.getCkgMtrlActoNm())
                .ckgKndActoNm(recipe.getCkgKndActoNm())
                .ckgMtrlCn(recipe.getCkgMtrlCn())
                .ckgInbunNm(recipe.getCkgInbunNm())
                .ckgDodfNm(recipe.getCkgDodfNm())
                .ckgTimeNm(recipe.getCkgTimeNm())
                .firstRegDt(recipe.getFirstRegDt())
                .rcpImgUrl(recipe.getRcpImgUrl())
                .build();
    }

    // ✅ DTO → Entity 변환
    public Recipe toEntity() {
        return Recipe.builder()
                .rcpSno(rcpSno)
                .userId(userId)
                .rcpTtl(rcpTtl)
                .ckgNm(ckgNm)
                .inqCnt(inqCnt != null ? inqCnt : 0)
                .rcmmCnt(rcmmCnt != null ? rcmmCnt : 0)
                .ckgMthActoNm(ckgMthActoNm)
                .ckgMtrlActoNm(ckgMtrlActoNm)
                .ckgKndActoNm(ckgKndActoNm)
                .ckgMtrlCn(ckgMtrlCn)
                .ckgInbunNm(ckgInbunNm)
                .ckgDodfNm(ckgDodfNm)
                .ckgTimeNm(ckgTimeNm)
                .firstRegDt(firstRegDt != null ? firstRegDt : LocalDateTime.now())
                .rcpImgUrl(rcpImgUrl)
                .build();
    }
}