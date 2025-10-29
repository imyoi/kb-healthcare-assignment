package com.kb.healthcare.myohui.repository;


import com.kb.healthcare.myohui.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);
}