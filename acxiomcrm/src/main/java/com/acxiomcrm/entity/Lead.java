package com.acxiomcrm.entity;

import com.acxiomcrm.enums.LeadPriority;
import com.acxiomcrm.enums.LeadStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "leads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String leadCode;

    @Column(nullable = false)
    private String leadName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    private String companyName;

    private String source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadPriority priority;

    private BigDecimal expectedValue;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private AppUser assignedTo;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }
}