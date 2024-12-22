package com.bpifrance.capitaldetection.repository;

import com.bpifrance.capitaldetection.entity.Beneficiary;
import com.bpifrance.capitaldetection.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    List<Beneficiary> findByCompany(Company company);
}