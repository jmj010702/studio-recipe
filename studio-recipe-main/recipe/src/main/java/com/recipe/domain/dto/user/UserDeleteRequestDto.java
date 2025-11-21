package com.recipe.domain.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDeleteRequestDto {

    @Schema(description = "비밀번호 확인", example = "password123!")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}