package com.recipe.domain.dto.user;

import com.recipe.domain.entity.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class UserLoginRequestDTO {

    @NotBlank(message = "아이디를 입력하세요")
//    @Size()
    private String id;

    @NotBlank(message = "비밀번호를 입력하세요")
//    @Size()
//    @Pattern()
    private String password;

}
