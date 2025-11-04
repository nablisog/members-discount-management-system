package com.springboot.membersdiscount.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String location;
    private Double price;
    private String memberSince;
    @Column(name = "has_paid", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @JsonProperty("hasPaid")
    private Boolean hasPaid;
    private LocalDate deadline;
}
