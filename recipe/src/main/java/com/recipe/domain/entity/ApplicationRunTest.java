package com.recipe.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_table")
@Builder
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplicationRunTest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
