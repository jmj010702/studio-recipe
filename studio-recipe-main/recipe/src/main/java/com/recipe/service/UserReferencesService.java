package com.recipe.service;

import com.recipe.domain.entity.Recipe;
import com.recipe.domain.entity.User;
import com.recipe.domain.entity.UserReferences;
import com.recipe.domain.entity.enums.PreferenceType;
import com.recipe.exceptions.user.UserExceptions;
import com.recipe.repository.UserReferencesRepository;
import com.recipe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserReferencesService {

    private final UserReferencesRepository referenceRepository;
    private final UserService userService;

    @Transactional
    public void userRecipeView(Recipe recipe, Long userId) {
        User findUser = userService.findByUser(userId);

        Optional<UserReferences> findRef =
                referenceRepository.findByUser_UserIdAndRecipe_RcpSno(userId, recipe.getRcpSno());

        if(findRef.isPresent()) {
            //VIEW라면 날짜 업데이트 or LIKE라면 X(LIKE가 더 높은 레벨이라 생각하여 바꾸지 않는다.)
            if(findRef.get().getPreference() ==  PreferenceType.VIEW) {
                //Modified는 JpaAuditing이 있지만, DirtyChecking에 해당되지 않아 직접 바꿨습니다.
                findRef.get().updateModifiedDate();
            }
        }else{
            UserReferences references = UserReferences.builder()
                    .user(findUser)
                    .recipe(recipe)
                    .preference(PreferenceType.VIEW)
                    .build();
            log.info("세이브 됨!!!!!!!!!!");
            referenceRepository.save(references);
        }
    }

    // Like 좋아요는 중복으로 눌리지 않고 true, false로 구분 되기 때문에 update가 필요 없음
    // 또한 LikeService에서 좋아요를 눌렀는지 검증하기에 검증 로직 따로 필요X
    @Transactional
    public void userLikeToRecipe(Recipe recipe, User user) {
            UserReferences references = UserReferences.builder()
                    .user(user)
                    .recipe(recipe)
                    .preference(PreferenceType.LIKE)
                    .build();
            referenceRepository.save(references);
    } //리팩터링 고려 사항(View, Like Enum 타입을 넘겨주고 Enum Type를 가지고 판단 즉 두 개의 로직을 하나로

    public void deleteByReference(Recipe recipe, User user) {
        Optional<UserReferences> userReferences =
                referenceRepository.findByUserAndRecipeAndPreference(user, recipe, PreferenceType.LIKE);
        userReferences.ifPresent(referenceRepository::delete);
    }
}
