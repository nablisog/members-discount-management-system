package com.springboot.membersdiscount.unitTest;

import com.springboot.membersdiscount.model.Member;
import com.springboot.membersdiscount.repository.MemberRepository;
import com.springboot.membersdiscount.service.DiscountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class DiscountServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private DiscountService discountService;

    private Member member1, member2, member3;
    @BeforeEach
    public void setUp() {
        member1 = new Member();
        member1.setId(1L);
        member1.setName("Ola");
        member1.setPrice(100.0);
        member1.setHasPaid(true);
        member1.setMemberSince(LocalDate.now().minusYears(4).toString());


        member2 = new Member();
        member2.setId(2L);
        member2.setName("Anne");
        member2.setPrice(120.0);
        member2.setHasPaid(false);
        member2.setMemberSince(LocalDate.now().minusYears(5).toString());


        member3 = new Member();
        member3.setId(3L);
        member3.setName("Tor");
        member3.setPrice(200.0);
        member3.setHasPaid(true);
        member3.setMemberSince(LocalDate.now().minusYears(2).toString());
    }

    @DisplayName("should return list of members from the database")
    @Test
    public void fetchMembersFromDBTest(){
        var response = new Member[]{member1, member2, member3};
        when(memberRepository.findAll()).thenReturn(List.of(member1, member2, member3));
        var result = discountService.fetchAllMembers();
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @DisplayName("should return true if member has paid, false otherwise")
    @Test
    public void testHasPaid() {
        assertTrue(discountService.hasPaid(member1));
        assertFalse(discountService.hasPaid(member2));
    }

    @DisplayName("should return true if member has been a member for 3 or more years")
    @Test
    public void testMemberMoreThan3Years() {
        assertTrue(discountService.memberMoreThan3Years(member1));
        assertFalse(discountService.memberMoreThan3Years(member3));
    }

    @DisplayName("should calculate correct discount based on payment and membership duration")
    @Test
    public void testCalculateDiscount() {
        double discount = discountService.calculateDiscount(member1);
        assertEquals(15.0, discount);
    }

    @DisplayName("should return only members eligible for discount")
    @Test
    public void testEligibleDiscountedMembers() {
        when(memberRepository.findAll()).thenReturn(List.of(member1, member2, member3));
        var eligible = discountService.getEligibleDiscountedMembers();

        assertEquals(1, eligible.size());
        assertEquals(member1.getId(), eligible.get(0).getId());
        assertEquals(85.0, eligible.get(0).getPrice());
    }

    @DisplayName("should return members with deadlines within 3 days")
    @Test
    public void testMembersWithUpcomingDeadline() {
        Member m = new Member();
        m.setId(10L);
        m.setHasPaid(false);
        m.setDeadline(LocalDate.now().plusDays(2));

        when(memberRepository.findAll()).thenReturn(List.of(m));

        var upcoming = discountService.getMembersWithUpcomingDeadline();
        assertEquals(1, upcoming.size());
        assertEquals(10L, upcoming.get(0).getId());
    }



}
