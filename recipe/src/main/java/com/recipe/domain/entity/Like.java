package com.recipe.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "LIKES",
        uniqueConstraints = {
        @UniqueConstraint(name = "UQ_RECIPE_LIKE", columnNames = {"USER_ID", "RCE_SNO"})
        })
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
public class Like extends CreateTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LIKE_ID")
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false,  cascade = CascadeType.REMOVE)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false,  cascade = CascadeType.REMOVE)
    @JoinColumn(name = "RCP_SNO")
    private Recipe recipe;
}
