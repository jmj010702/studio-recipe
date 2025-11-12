package com.recipe.domain.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDeleteRequestDto {
    private String id;   // 로그인 아이디
    private String pwd;  // 비밀번호
}
