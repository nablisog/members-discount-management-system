package com.springboot.membersdiscount.service;

import com.springboot.membersdiscount.exception.MembersNotFoundException;
import com.springboot.membersdiscount.model.Member;
import com.springboot.membersdiscount.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static com.springboot.membersdiscount.email.EmailTemplet.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class DiscountService {
    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final EmailService emailService;


    @Value("${external.api.get.url}")
    private String externalApiGetUrl;

    @Value("${external.api.post.url}")
    private String externalApiPostUrl;


    //Scheduled to fetch members from external API on 25th of every month at 3 AM & saves to DB
    @Scheduled(cron = "0 0 3 25 * ?")
    public void fetchAndSaveAllMembers() {
        log.info("Fetching and saving all members from external API...");

        Member[] members = null;
        try {
            members = restTemplate.getForObject(externalApiGetUrl, Member[].class);
        } catch (RestClientException e) {
            log.error("Failed to fetch members from external API", e);
            return; // stop further execution, but don’t crash the scheduler
        }

        if (members == null || members.length == 0) {
            log.warn("No members found from external API");
            return;
        }

        for (Member member : members) {
            try {
                updateOrSaveMember(member);
            } catch (Exception e) {
                log.error("Failed to save member with ID {}: {}", member.getId(), e.getMessage(), e);
            }
        }

        log.info("Finished fetching and saving members. Total processed: {}", members.length);
    }


    //Getting all members from DB
    public List<Member> fetchAllMembers() {
        var members = memberRepository.findAll();
        if (members.isEmpty()) {
            throw new MembersNotFoundException("No members found from external API");
        }
        return members;
    }

    //Retrieves members eligible for a discount:
    public List<Member>getEligibleDiscountedMembers(){
        return fetchAllMembers().stream()
                .filter(this::hasPaid)
                .filter(this::memberMoreThan3Years)
                .map(member -> {
                    val discount = calculateDiscount(member);
                    member.setPrice(member.getPrice() - discount);
                    return member;
                }).toList();
    }


    //Checks if a member has paid their dues
    public boolean hasPaid(Member member) {
        return Boolean.TRUE.equals(member.getHasPaid());
    }

    //Checks if a member has been registered for more than 3 years
    public boolean memberMoreThan3Years(Member member) {
        try {
            LocalDate memberSince = LocalDate.parse(member.getMemberSince());
            return memberSince.isBefore(LocalDate.now().minusYears(3));
        } catch (Exception e) {
            log.warn("Failed to parse memberSince date for member ID {}: {}", member.getId(), member.getMemberSince());
        }
        return false;
    }

    //Calculates a 15% discount on the member's current price
    public double calculateDiscount(Member member) {
        return member == null || member.getPrice() == null ? 0.0 : member.getPrice() * 0.15;
    }

    //Retrieves all members who have not paid their dues
    public List<Member> getMembersWhoHaveNotPaid(){
        return memberRepository.findByHasPaid(false);
    }
    //Retrieves all members who have paid their dues
    public List<Member> getMembersWhoHavePaid(){
        return memberRepository.findByHasPaid(true);
    }

    //Sends the eligible discounted members to an external API
    public List<Member> sendDiscountedMembers(){
        val discountedMembers = getEligibleDiscountedMembers();
        log.info("Sending eligible members to external API...");
        restTemplate.postForEntity(externalApiPostUrl, discountedMembers, Member[].class);
        return discountedMembers;
    }

    //Updates an existing member if found by ID, otherwise creates a new one.
    public void updateOrSaveMember(Member incomingMember) {
        if (incomingMember.getHasPaid() == null) {
            incomingMember.setHasPaid(false);
        }
        memberRepository.findById(incomingMember.getId())
                .map(existingMember -> {
                    existingMember.setName(incomingMember.getName());
                    existingMember.setEmail(incomingMember.getEmail());
                    existingMember.setLocation(incomingMember.getLocation());
                    existingMember.setPrice(incomingMember.getPrice());
                    existingMember.setHasPaid(incomingMember.getHasPaid());
                    existingMember.setMemberSince(incomingMember.getMemberSince());
                    existingMember.setDeadline(incomingMember.getDeadline());
                    //exception
                    return memberRepository.save(existingMember);
                })
                .orElseGet(() -> {
                    log.warn("Member with ID {} not found. Creating new member.", incomingMember.getId());
                    incomingMember.setId(null);
                    return memberRepository.save(incomingMember);
                });

    }

    //Finds members who have not paid and whose deadline is within the next 3 days.
    public List<Member> getMembersWithUpcomingDeadline(){
        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);
        return fetchAllMembers().stream()
                .filter(m -> !hasPaid(m))
                .filter(m -> m.getDeadline() != null)
                .filter(m -> !m.getDeadline().isBefore(today))
                .filter(m -> !m.getDeadline().isAfter(threeDaysLater)).toList();
    }

    //Sends email reminders to members whose deadline is within 3 days and who haven't paid
    public void sendReminders(){
        var reminder =getMembersWithUpcomingDeadline();
        reminder.forEach(member -> {
            String body = String.format(REMINDER_BODY,member.getName(),member.getDeadline());
            emailService.sendEmail(member.getEmail(), REMINDER_SUBJECT, body);
        });
    }


    @Scheduled(cron = "0 0 9 * * ?")
    public void checkDeadlines() {
        sendReminders();
    }

    //Sends an email to all eligible members (paid & 3+ years) informing them of their discount.
    public void sendDiscountToMembers(){
        var eligibleMembers = getEligibleDiscountedMembers();
        log.info("Sending Email to eligible members with 15% Discount...");
        eligibleMembers.forEach(member -> {
            double originalPrice = member.getPrice() / 0.85;
            String body = String.format(DISCOUNT_BODY,member.getName(), originalPrice,member.getPrice());
            emailService.sendEmail(member.getEmail(), DISCOUNT_SUBJECT, body);
        });
    }
















}
