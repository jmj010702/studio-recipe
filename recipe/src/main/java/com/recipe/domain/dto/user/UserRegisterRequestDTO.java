package com.recipe.domain.dto.user;

import com.recipe.domain.entity.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class UserRegisterRequestDTO {
    @NotBlank(message = "아이디를 입력하세요")
//    @Size()
    private String id;

    @NotBlank(message = "비밀번호를 입력하세요")
//    @Size()
//    @Pattern()
    private String password;

    @NotBlank(message = "이름을 입력하세요")
//    @Size
    private String name;

    //닉네임 중복 확인 구현
    @NotBlank(message = "닉네임을 입력하세요")
//    @Size
    private String nickname;

    @NotBlank(message = "이메일을 입력하세요")
    @Email(message = "옳바른 이메일 형식이 아닙니다.")
    private String email;

    //생년월일을 받는 것이 좋을 것 같다 인증 정보등..
    @NotNull(message = "생년월일은 필수 입니다.")
    @Past(message = "생년월일은 현재를 넘길 수 없습니다.")
    private LocalDate birth;

    @NotNull(message = "성별은 필수 값입니다.")
    private Gender gender;
}
