package com.bpifrance.capitaldetection.service;

import com.bpifrance.capitaldetection.dto.BeneficiaryDto;
import com.bpifrance.capitaldetection.dto.CompanyDto;
import com.bpifrance.capitaldetection.entity.Beneficiary;
import com.bpifrance.capitaldetection.entity.Company;
import com.bpifrance.capitaldetection.entity.Person;
import com.bpifrance.capitaldetection.exception.ResourceNotFoundException;
import com.bpifrance.capitaldetection.repository.BeneficiaryRepository;
import com.bpifrance.capitaldetection.repository.CompanyRepository;
import com.bpifrance.capitaldetection.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final PersonRepository personRepository;
    private final BeneficiaryRepository beneficiaryRepository;

    public CompanyService(CompanyRepository companyRepository, PersonRepository personRepository, BeneficiaryRepository beneficiaryRepository) {
        this.companyRepository = companyRepository;
        this.personRepository = personRepository;
        this.beneficiaryRepository = beneficiaryRepository;
    }

    public Company createCompany(CompanyDto companyDto) {
        Company company = new Company();
        company.setName(companyDto.name());
        return companyRepository.save(company);
    }

    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
    }

    @Transactional
    public void addBeneficiary(Long companyId, BeneficiaryDto beneficiaryDto) {
        Company company = getCompanyById(companyId);
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setCompany(company);
        if ("PERSON".equalsIgnoreCase(beneficiaryDto.beneficiaryType())) {
            Person person = personRepository.findById(beneficiaryDto.beneficiaryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + beneficiaryDto.beneficiaryId()));
            beneficiary.setBeneficiaryPerson(person);
        } else if ("COMPANY".equalsIgnoreCase(beneficiaryDto.beneficiaryType())) {
            Company beneficiaryCompany = companyRepository.findById(beneficiaryDto.beneficiaryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + beneficiaryDto.beneficiaryId()));
            beneficiary.setBeneficiaryCompany(beneficiaryCompany);
        } else {
            throw new IllegalArgumentException("Invalid beneficiary type: " + beneficiaryDto.beneficiaryType());
        }
        beneficiary.setSharePercentage(beneficiaryDto.sharePercentage());

        // Vérifier que le total des parts ne dépasse pas 100%
        double totalShares = company.getBeneficiaries().stream()
                .mapToDouble(Beneficiary::getSharePercentage)
                .sum();
        if (totalShares + beneficiaryDto.sharePercentage() > 100.0) {
            throw new IllegalArgumentException("Total share percentage exceeds 100%");
        }

        beneficiaryRepository.save(beneficiary);
    }

    public List<Map<String, Object>> getEffectiveBeneficiaries(Long companyId, String type) {
        Company company = getCompanyById(companyId);
        Map<Long, Double> ownershipMap = new HashMap<>();
        Set<Long> visitedCompanies = new HashSet<>();

        calculateOwnership(company, 100.0, ownershipMap, visitedCompanies);

        List<Map<String, Object>> effectiveBeneficiaries = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : ownershipMap.entrySet()) {
            if ("PHYSIQUE".equalsIgnoreCase(type) && entry.getValue() <= 25.0) {
                continue; // Exclure les bénéficiaires avec moins de 25% pour les personnes physiques
            }
            if ("EFFECTIF".equalsIgnoreCase(type) && entry.getValue() <= 25.0) {
                continue; // Exclure les bénéficiaires effectifs (seuil > 25%)
            }

                Person person = personRepository.findById(entry.getKey())
                        .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + entry.getKey()));
                Map<String, Object> beneficiaryInfo = new HashMap<>();
                beneficiaryInfo.put("id", person.getId());
                beneficiaryInfo.put("firstName", person.getFirstName());
                beneficiaryInfo.put("lastName", person.getLastName());
                beneficiaryInfo.put("sharePercentage", entry.getValue());
                effectiveBeneficiaries.add(beneficiaryInfo);

        }

        return effectiveBeneficiaries;
    }

    private void calculateOwnership(Company company, double parentShare, Map<Long, Double> ownershipMap, Set<Long> visitedCompanies) {
        if (visitedCompanies.contains(company.getId())) {
            return;
        }
        visitedCompanies.add(company.getId());

        for (Beneficiary beneficiary : company.getBeneficiaries()) {
            double currentShare = parentShare * (beneficiary.getSharePercentage() / 100.0);
            if (beneficiary.isPerson()) {
                ownershipMap.merge(beneficiary.getBeneficiaryPerson().getId(), currentShare, Double::sum);
            } else {
                Company beneficiaryCompany = beneficiary.getBeneficiaryCompany();
                calculateOwnership(beneficiaryCompany, currentShare, ownershipMap, visitedCompanies);
            }
        }
    }
}
