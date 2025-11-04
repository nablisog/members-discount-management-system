package com.springboot.membersdiscount.unitTest;


import com.springboot.membersdiscount.controller.DiscountController;
import com.springboot.membersdiscount.model.Member;
import com.springboot.membersdiscount.service.DiscountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DiscountController.class)
public class DiscountControllerTest {

    @Autowired
    private MockMvc mockMvc;

     @MockitoBean
    private DiscountService discountService;

    private Member member1, member2;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    @DisplayName("GET /members should return list of members")
    void testGetAllMembers() throws Exception {
        when(discountService.fetchAllMembers()).thenReturn(List.of(member1, member2));

        mockMvc.perform(get("/members")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Ola"))
                .andExpect(jsonPath("$[1].name").value("Anne"));

        verify(discountService, times(1)).fetchAllMembers();
    }

    @Test
    @DisplayName("GET /members/fetchAndSave should fetch and save members")
    void testFetchAndSaveMembers() throws Exception {
        doNothing().when(discountService).fetchAndSaveAllMembers();

        mockMvc.perform(get("/members/fetchAndSave"))
                .andExpect(status().isOk())
                .andExpect(content().string("Fetched and saved members from external API"));

        verify(discountService, times(1)).fetchAndSaveAllMembers();
    }

    @Test
    @DisplayName("POST /members/eligibleFordiscount should send discounted members")
    void testSendDiscount() throws Exception {
        when(discountService.sendDiscountedMembers()).thenReturn(List.of(member1));

        mockMvc.perform(post("/members/eligibleFordiscount"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Ola"));

        verify(discountService, times(1)).sendDiscountedMembers();
    }

    @Test
    @DisplayName("GET /members/unpaidMembers should return unpaid members")
    void testGetMembersWhoHaveNotPaid() throws Exception {
        when(discountService.getMembersWhoHaveNotPaid()).thenReturn(List.of(member2));

        mockMvc.perform(get("/members/unpaidMembers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Anne"));

        verify(discountService, times(1)).getMembersWhoHaveNotPaid();
    }

    @Test
    @DisplayName("POST /members/reminders should send reminders")
    void testSendReminders() throws Exception {
        doNothing().when(discountService).sendReminders();

        mockMvc.perform(post("/members/reminders"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reminders sent successfully!"));

        verify(discountService, times(1)).sendReminders();
    }

    @Test
    @DisplayName("POST /members/sendDiscountToMembers should send discount emails")
    void testSendDiscountToMembers() throws Exception {
        doNothing().when(discountService).sendDiscountToMembers();

        mockMvc.perform(post("/members/sendDiscountToMembers"))
                .andExpect(status().isOk())
                .andExpect(content().string("Emails have been successfully sent to eligible members!"));

        verify(discountService, times(1)).sendDiscountToMembers();
    }
}

