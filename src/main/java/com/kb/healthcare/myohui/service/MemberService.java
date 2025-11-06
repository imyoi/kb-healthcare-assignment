package com.kb.healthcare.myohui.service;

import com.kb.healthcare.myohui.domain.dto.MemberResponse;
import com.kb.healthcare.myohui.domain.dto.MemberSignupRequest;
import com.kb.healthcare.myohui.domain.entity.Member;
import com.kb.healthcare.myohui.global.enums.ErrorCode;
import com.kb.healthcare.myohui.global.exception.CustomException;
import com.kb.healthcare.myohui.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     * */
    @Transactional
    public MemberResponse signup(MemberSignupRequest request) {
        request.validate();

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        request.updatePassword(encodedPassword);

        Member member = request.toEntity();
        Member savedMember = memberRepository.save(member);
        return MemberResponse.from(savedMember);
    }

    /**
     * 회원 조회
     * */
    @Transactional(readOnly = true)
    public MemberResponse getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberResponse.from(member);
    }
}