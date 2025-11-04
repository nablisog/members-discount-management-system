package com.springboot.membersdiscount.controller;

import com.springboot.membersdiscount.model.Member;
import com.springboot.membersdiscount.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class DiscountController {
    private final DiscountService discountService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Member> getAllMembers() {
        return discountService.fetchAllMembers();
    }

    @GetMapping("/fetchAndSave")
    @ResponseStatus(HttpStatus.OK)
    public String fetchAndSaveMembers() {
        discountService.fetchAndSaveAllMembers();
        return "Fetched and saved members from external API";
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/eligibleFordiscount")
    public List<Member> sendDiscount() {
        return discountService.sendDiscountedMembers();
    }

    @GetMapping("/unpaidMembers")
    @ResponseStatus(HttpStatus.OK)
    public List<Member> MembersWhoHaveNotPaid() {
        return discountService.getMembersWhoHaveNotPaid();
    }

    @GetMapping("/paidMembers")
    @ResponseStatus(HttpStatus.OK)
    public List<Member> getMembersWhoHavePaid() {
        return discountService.getMembersWhoHavePaid();
    }

    @PostMapping("/reminders")
    @ResponseStatus(HttpStatus.OK)
    public String sendReminders() {
        discountService.sendReminders();
        return "Reminders sent successfully!";
    }

    @PostMapping("/sendDiscountToMembers")
    @ResponseStatus(HttpStatus.OK)
    public String sendDiscountToMembers() {
        discountService.sendDiscountToMembers();
        return "Emails have been successfully sent to eligible members!";
    }




}
