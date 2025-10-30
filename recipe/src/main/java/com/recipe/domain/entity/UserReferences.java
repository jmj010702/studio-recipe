package com.recipe.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.recipe.domain.entity.enums.PreferenceType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "USER_REFERENCE")
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
@ToString
public class UserReferences extends BaseEntityTime{
    @Id
    @GeneratedValue
    @Column(name = "PREFERENCE_ID")
    private Long preferenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RCP_SNO")
    private Recipe recipe;


// VIEW, SAVE, SHARE, SEARCH 고려
@Enumerated(EnumType.STRING)
@Column(name = "PREFERENCE_TYPE", nullable = false,
        columnDefinition = "ENUM ('VIEW', 'LIKE')")
private PreferenceType preference;

//RATING

public void changePreference(PreferenceType preference) {
    this.preference = preference;
}
}
