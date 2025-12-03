package com.recipe.repository;

import com.recipe.domain.entity.Recipe;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class RecipeSpecification {

    public static Specification<Recipe> hasIngredients(List<String> ingredients) {
        return (root, query, criteriaBuilder) -> {
            // 검색할 재료가 없으면 아무것도 필터링하지 않음 (전체 조회 방지용으로 null 리턴하거나, 빈 리스트 처리)
            if (ingredients == null || ingredients.isEmpty()) {
                return null; 
            }

            // 동적 쿼리 생성
            List<Predicate> predicates = new ArrayList<>();
            for (String ingredient : ingredients) {
                // ckgMtrlCn (재료 내용) 컬럼에 해당 재료가 포함되어 있는지 확인 (LIKE %재료%)
                predicates.add(criteriaBuilder.like(root.get("ckgMtrlCn"), "%" + ingredient + "%"));
            }

            // 모든 조건을 AND로 연결 (재료1 AND 재료2 AND 재료3 ...)
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}