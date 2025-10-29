package com.kb.healthcare.myohui.domain.dto;


import com.kb.healthcare.myohui.domain.entity.Member;
import com.kb.healthcare.myohui.global.enums.ErrorCode;
import com.kb.healthcare.myohui.global.exception.CustomException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSignupRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String nickname;

    public MemberSignupRequest(String email,
                               String password,
                               String name,
                               String nickname) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
    }

    public void validate() {
        if (!isValidPassword(password)) {
            throw new CustomException(ErrorCode.MEMBER_INVALID_PASSWORD_RULE);
        }
    }

    /**
     * 비밀번호 유효성 검증
     * - 영문 + 숫자 + 특수문자 조합, 8~20자 이내
     */
    private boolean isValidPassword(String password) {
        if (password == null || password.isBlank()) {
            return false;
        }
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$";
        return password.matches(regex);
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public Member toEntity() {
        return new Member(email, password, name, nickname);
    }
}