package com.recipe.domain.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor // 혹시 모를 JSON 변환 오류 방지용으로 추가하면 좋습니다.
public class ResetProcessResponse {
    private String message;
    
    // ▼▼▼ [수정] token -> resetToken 으로 변경 ▼▼▼
    private String resetToken; 
}