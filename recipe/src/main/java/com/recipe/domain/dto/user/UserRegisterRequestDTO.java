package com.recipe.domain.dto.user;

import com.recipe.domain.entity.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class UserRegisterRequestDTO {
    @NotBlank(message = "아이디를 입력하세요")
    @Size(min = 8, max = 16, message = "아이디는 최소 8자 최대 16자입니다.")
    private String id;

    @NotBlank(message = "비밀번호를 입력하세요")
    @Size(min = 8, max = 32, message = "비밀번호는 최소 8자 최대 32자입니다.")
//    @Pattern(regexp = "(?=.*[A-Z])(?=.*[a-z](?=.*)")
    private String password;

    @NotBlank(message = "이름을 입력하세요")
//    @Size
    private String name;

    @NotBlank(message = "닉네임을 입력하세요")
//    @Size
    private String nickname;

    @NotBlank(message = "이메일을 입력하세요")
    @Email(message = "옳바른 이메일 형식이 아닙니다.")
    private String email;

    @NotNull(message = "생년월일은 필수 입니다.")
    @Past(message = "생년월일은 현재를 넘길 수 없습니다.")
    private LocalDate birth;

    @NotNull(message = "성별은 필수 값입니다.")
    private Gender gender;
}
