package com.recipe.domain.entity; // ⚠️ 중요: 이 파일이 있는 폴더 위치와 패키지명이 일치해야 합니다.

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Table(name = "RECIPES")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@DynamicInsert // INSERT 시 null이 아닌 필드만 포함 (DB Default 값 적용에 유리)
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RCP_SNO")
    private Long rcpSno;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "RCP_TTL", length = 200)
    private String rcpTtl;

    @Column(name = "CKG_NM", length = 100)
    private String ckgNm;

    @Column(name = "INQ_CNT")
    @ColumnDefault("0")
    @Builder.Default
    private Integer inqCnt = 0;

    @Column(name = "RCMM_CNT")
    @ColumnDefault("0")
    @Builder.Default
    private Integer rcmmCnt = 0;

    @Column(name = "CKG_MTH_ACTO_NM", length = 100)
    private String ckgMthActoNm;

    @Column(name = "CKG_MTRL_ACTO_NM", length = 200)
    private String ckgMtrlActoNm;

    @Column(name = "CKG_KND_ACTO_NM", length = 100)
    private String ckgKndActoNm;

    @Lob
    @Column(name = "CKG_MTRL_CN", columnDefinition = "TEXT")
    private String ckgMtrlCn;

    @Column(name = "CKG_INBUN_NM", length = 50)
    private String ckgInbunNm;

    @Column(name = "CKG_DODF_NM", length = 50)
    private String ckgDodfNm;

    @Column(name = "CKG_TIME_NM", length = 50)
    private String ckgTimeNm;

    @Column(name = "FIRST_REG_DT")
    private LocalDateTime firstRegDt;

    @Column(name = "RCP_IMG_URL", length = 500)
    private String rcpImgUrl;

    // --- 비즈니스 로직 메서드 ---

    // 좋아요 수 증가
    public void likeToCountUp() {
        if (this.rcmmCnt == null) {
            this.rcmmCnt = 0;
        }
        this.rcmmCnt++;
    }

    // 좋아요 수 감소
    public void likeToCountDown() {
        if (this.rcmmCnt != null && this.rcmmCnt > 0) {
            this.rcmmCnt--;
        }
    }
    
    // 조회수 증가
    public void increaseViewCount() {
        if (this.inqCnt == null) {
            this.inqCnt = 0;
        }
        this.inqCnt++;
    }
    
    // 엔티티 저장 전 실행 (기본값 설정)
    @PrePersist
    public void prePersist() {
        if (this.firstRegDt == null) {
            this.firstRegDt = LocalDateTime.now();
        }
        if (this.inqCnt == null) {
            this.inqCnt = 0;
        }
        if (this.rcmmCnt == null) {
            this.rcmmCnt = 0;
        }
    }
}