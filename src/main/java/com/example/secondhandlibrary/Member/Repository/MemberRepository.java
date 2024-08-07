package com.example.secondhandlibrary.Member.Repository;

import com.example.secondhandlibrary.Member.Entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

}
