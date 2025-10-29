package com.kb.healthcare.myohui.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kb.healthcare.myohui.domain.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberResponse {

    private Long id;
    private String email;
    private String name;
    private String nickname;

    public MemberResponse(Long id, String email, String name, String nickname) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
    }

    public static MemberResponse from(Member member) {
        if (member == null) return null;
        return new MemberResponse(
            member.getId(),
            member.getEmail(),
            member.getName(),
            member.getNickname()
        );
    }
}