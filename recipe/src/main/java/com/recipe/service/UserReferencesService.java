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
@Transactional(readOnly = true)
public class UserReferencesService {

    private final UserReferencesRepository referenceRepository;
    private final UserRepository userRepository;

    @Transactional
    public void userRecipeView(Recipe recipe, Long userId) {
        User findUser = userRepository.findById(userId).orElseThrow(
                () -> UserExceptions.NOT_FOUND.getUserException()
        );

        Optional<UserReferences> findRef =
                referenceRepository.findByUser_UserIdAndRecipe_RcpSno(userId, recipe.getRcpSno());

        if(findRef.isPresent()) {
            log.info("끄아아아아아아아아!");
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
}
