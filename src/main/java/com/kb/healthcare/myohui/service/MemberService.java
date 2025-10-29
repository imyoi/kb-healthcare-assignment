package com.kb.healthcare.myohui.service;

import com.kb.healthcare.myohui.domain.dto.MemberResponse;
import com.kb.healthcare.myohui.domain.dto.MemberSignupRequest;
import com.kb.healthcare.myohui.domain.entity.Member;
import com.kb.healthcare.myohui.global.enums.ErrorCode;
import com.kb.healthcare.myohui.global.exception.CustomException;
import com.kb.healthcare.myohui.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원가입
     * */
    @Transactional
    public MemberResponse signup(MemberSignupRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        Member member = request.toEntity();
        Member saved = memberRepository.save(member);

        return MemberResponse.from(saved);
    }
}