package com.kb.healthcare.myohui.service;

import com.kb.healthcare.myohui.domain.dto.HealthDataRequest;
import com.kb.healthcare.myohui.domain.entity.HealthDataRaw;
import com.kb.healthcare.myohui.domain.entity.Member;
import com.kb.healthcare.myohui.domain.enums.HealthProduct;
import com.kb.healthcare.myohui.domain.enums.HealthSource;
import com.kb.healthcare.myohui.global.enums.ErrorCode;
import com.kb.healthcare.myohui.global.exception.CustomException;
import com.kb.healthcare.myohui.repository.HealthDataRepository;
import com.kb.healthcare.myohui.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthDataService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MemberRepository memberRepository;
    private final HealthDataRepository healthDataRepository;

    /**
     * 건강 데이터 저장
     * */
    @Transactional
    public void saveHealthData(Long memberId, HealthDataRequest request) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 데이터 정제 및 저장
        String recordKey = request.getRecordKey();
        HealthSource source = HealthSource.from(request.getData().getSource().getName());
        HealthProduct product = HealthProduct.from(request.getData().getSource().getProduct().getName());

        List<HealthDataRaw> raws = new ArrayList<>();
        for (HealthDataRequest.Entry entry : request.getData().getEntries()) {
            raws.add(new HealthDataRaw(
                member,
                recordKey,
                entry.getSteps(),
                entry.getDistance().getValue(),
                entry.getDistance().getUnit(),
                entry.getCalories().getValue(),
                entry.getCalories().getUnit(),
                LocalDateTime.parse(entry.getPeriod().getFrom(), FORMATTER),
                LocalDateTime.parse(entry.getPeriod().getTo(), FORMATTER),
                source,
                product
            ));
        }

        healthDataRepository.saveAll(raws);
    }
}