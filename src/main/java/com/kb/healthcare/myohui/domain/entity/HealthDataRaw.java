package com.kb.healthcare.myohui.domain.entity;

import com.kb.healthcare.myohui.domain.enums.HealthProduct;
import com.kb.healthcare.myohui.domain.enums.HealthSource;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HealthDataRaw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String recordKey = UUID.randomUUID().toString();

    @Column(nullable = false)
    private int steps;

    @Column(nullable = false)
    private float distance;

    @Column(nullable = false)
    private String distanceUnit;

    @Column(nullable = false)
    private float calories;

    @Column(nullable = false)
    private String caloriesUnit;

    @Column(nullable = false)
    private LocalDateTime periodFrom;

    @Column(nullable = false)
    private LocalDateTime periodTo;

    @Convert(converter = HealthSource.HealthSourceConverter.class)
    private HealthSource source;

    @Convert(converter = HealthProduct.HealthProductConverter.class)
    private HealthProduct product;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public HealthDataRaw(Member member,
                         String recordKey,
                         int steps,
                         float distance,
                         String distanceUnit,
                         float calories,
                         String caloriesUnit,
                         LocalDateTime periodFrom,
                         LocalDateTime periodTo,
                         HealthSource source,
                         HealthProduct product) {
        this.member = member;
        this.recordKey = recordKey;
        this.steps = steps;
        this.distance = distance;
        this.distanceUnit = distanceUnit;
        this.calories = calories;
        this.caloriesUnit = caloriesUnit;
        this.periodFrom = periodFrom;
        this.periodTo = periodTo;
        this.source = source;
        this.product = product;
    }
}