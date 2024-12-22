package com.bpifrance.capitaldetection.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "beneficiaries")
@Data
public class Beneficiary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiary_person_id")
    private Person beneficiaryPerson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiary_company_id")
    private Company beneficiaryCompany;

    @Column(nullable = false)
    private Double sharePercentage;



    public boolean isPerson() {
        return beneficiaryPerson != null;
    }


}
