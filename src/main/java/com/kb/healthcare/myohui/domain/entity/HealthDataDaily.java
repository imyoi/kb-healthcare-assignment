package com.kb.healthcare.myohui.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class HealthDataDaily extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String recordKey;

    @Column(nullable = false)
    private LocalDate recordDate;

    @Column(nullable = false)
    private int totalSteps;

    @Column(nullable = false)
    private float totalCalories;

    @Column(nullable = false)
    private float totalDistance;

    public HealthDataDaily(Member member, String recordKey, LocalDate recordDate,
                           int totalSteps, float totalCalories, float totalDistance) {
        this.member = member;
        this.recordKey = recordKey;
        this.recordDate = recordDate;
        this.totalSteps = totalSteps;
        this.totalCalories = totalCalories;
        this.totalDistance = totalDistance;
    }

    public void update(int steps, float calories, float distance) {
        this.totalSteps += steps;
        this.totalCalories += calories;
        this.totalDistance += distance;
    }
}