package com.kb.healthcare.myohui.service;

import com.kb.healthcare.myohui.domain.dto.MemberLoginRequest;
import com.kb.healthcare.myohui.domain.dto.MemberLoginResponse;
import com.kb.healthcare.myohui.domain.dto.TokenRefreshResponse;
import com.kb.healthcare.myohui.domain.entity.Member;
import com.kb.healthcare.myohui.global.enums.ErrorCode;
import com.kb.healthcare.myohui.global.exception.CustomException;
import com.kb.healthcare.myohui.global.jwt.RedisService;
import com.kb.healthcare.myohui.global.jwt.TokenProvider;
import com.kb.healthcare.myohui.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    /**
     * 로그인
     */
    @Transactional(readOnly = true)
    public MemberLoginResponse login(MemberLoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.MEMBER_PASSWORD_MISMATCH);
        }

        String accessToken = tokenProvider.createAccessToken(member.getId(), member.getEmail());
        String refreshToken = tokenProvider.createRefreshToken(member.getId(), member.getEmail());
        return new MemberLoginResponse(accessToken, refreshToken);
    }

    /**
     * 로그아웃
     * */
    @Transactional
    public void logout(Long memberId) {
        redisService.deleteRefreshToken(memberId);
    }

    /**
     * 토큰 재발급
     * */
    @Transactional(readOnly = true)
    public TokenRefreshResponse refresh(Long memberId, String refreshToken) {
        // 리프레시 토큰 검증
        String storedRefreshToken = redisService.getRefreshToken(memberId);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.AUTH_INVALID_TOKEN);
        }

        // 이메일로 액세스 토큰 발급
        String email = tokenProvider.getEmailFromToken(refreshToken);
        String newAccessToken = tokenProvider.createAccessToken(memberId, email);

        return new TokenRefreshResponse(newAccessToken);
    }
}