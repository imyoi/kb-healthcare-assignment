package com.kb.healthcare.myohui.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kb.healthcare.myohui.global.BaseResponse;
import com.kb.healthcare.myohui.global.enums.ErrorCode;
import com.kb.healthcare.myohui.global.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final RedisService redisService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);
        try {
            if (!tokenProvider.validateToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            Long memberId = tokenProvider.getMemberIdFromToken(token);

            // 토큰 재발급 요청인 경우
            if (request.getRequestURI().contains("/auth/refresh")) {
                String refreshToken = redisService.getRefreshToken(memberId);
                if (refreshToken == null) {
                    throw new CustomException(ErrorCode.AUTH_INVALID_TOKEN);
                }
            }

            String email = tokenProvider.getEmailFromToken(token);

            CustomUserDetails userDetails = new CustomUserDetails(memberId, email, null);
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (CustomException e) {
            log.warn("JWT 인증 실패: {}", e.getErrorCode().getMessage());
            failedAuthentication(response, e.getErrorCode());
            return;
        } catch (Exception e) {
            log.error("JWT 필터 처리 중 예외 발생", e);
            failedAuthentication(response, ErrorCode.AUTH_INVALID_TOKEN);
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * JWT 인증 실패 시 BaseResponse 응답 반환
     */
    private void failedAuthentication(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        BaseResponse<?> errorResponse = BaseResponse.error(
            new BaseResponse.Error(errorCode.getCode(), errorCode.getMessage())
        );

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}