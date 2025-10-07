package com.recipe.domain.dto.autho;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenResponseDTO {

    private String accessToken;
    private Long accessTokenExpiresIn;
    private String refreshToken;
    private Long refreshTokenExpiresIn;
}
