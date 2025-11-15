package com.recipe.domain.dto.mypage;

import com.recipe.domain.entity.User;
import com.recipe.domain.entity.Recipe;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MyPageResponseDto {

    private UserInfo userInfo;
    private List<SimpleRecipeDto> likedList;
    private List<SimpleRecipeDto> savedList;
    private List<SimpleRecipeDto> authoredList;

    public static MyPageResponseDto of(User user, List<Recipe> likedRecipes, List<Recipe> authoredRecipes) {
        List<SimpleRecipeDto> likedDtoList = likedRecipes != null && !likedRecipes.isEmpty() 
            ? likedRecipes.stream().map(SimpleRecipeDto::new).collect(Collectors.toList())
            : new ArrayList<>();
            
        return MyPageResponseDto.builder()
                .userInfo(new UserInfo(user))
                .likedList(likedDtoList)
                .savedList(likedDtoList)
                .authoredList(authoredRecipes != null && !authoredRecipes.isEmpty()
                    ? authoredRecipes.stream().map(SimpleRecipeDto::new).collect(Collectors.toList())
                    : new ArrayList<>())
                .build();
    }

    @Getter
    public static class UserInfo {
        private Long userId;
        private String email;
        private String name;
        private String nickname;
        private String gender;
        private String birth;

        public UserInfo(User user) {
            this.userId = user.getUserId();
            this.email = user.getEmail();
            this.name = user.getName();
            this.nickname = user.getNickname();
            // ✅ Gender enum을 String으로 변환
            this.gender = user.getGender() != null ? user.getGender().name() : null;
            // ✅ LocalDate를 String으로 변환
            this.birth = user.getBirth() != null ? user.getBirth().toString() : null;
        }
    }

    @Getter
    public static class SimpleRecipeDto {
        private Long recipeId;
        private String title;
        private String imageUrl;
        private Integer viewCount;
        private Integer likeCount;

        public SimpleRecipeDto(Recipe recipe) {
            this.recipeId = recipe.getRcpSno();
            this.title = recipe.getRcpTtl() != null ? recipe.getRcpTtl() : "제목 없음";
            this.imageUrl = recipe.getRcpImgUrl() != null ? recipe.getRcpImgUrl() : "";
            this.viewCount = recipe.getInqCnt() != null ? recipe.getInqCnt() : 0;
            this.likeCount = recipe.getRcmmCnt() != null ? recipe.getRcmmCnt() : 0;
        }
    }
}