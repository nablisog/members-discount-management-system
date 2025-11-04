package com.springboot.membersdiscount.repository;

import com.springboot.membersdiscount.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByHasPaid(boolean hasPaid);

}
