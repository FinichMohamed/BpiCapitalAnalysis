package com.bpifrance.capitaldetection.controller;

import com.bpifrance.capitaldetection.dto.BeneficiaryDto;
import com.bpifrance.capitaldetection.dto.CompanyDto;
import com.bpifrance.capitaldetection.entity.Company;
import com.bpifrance.capitaldetection.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // POST /api/companies : Créer une nouvelle entreprise
    @PostMapping
    public ResponseEntity<Void> createCompany(@Valid @RequestBody CompanyDto companyDto) {
        Company company = companyService.createCompany(companyDto);
        return ResponseEntity.created(URI.create("/api/companies/" + company
                .getId())).build();
    }

    // GET /api/companies/{id}/beneficiaries : Récupérer les bénéficiaires
    @GetMapping("/{id}/beneficiaries")
    public ResponseEntity<List<Map<String, Object>>> getBeneficiaries(
            @PathVariable Long id,
            @RequestParam(defaultValue = "ALL") String type) {
            List<Map<String, Object>> beneficiaries = companyService.getEffectiveBeneficiaries(id,type);
            if (beneficiaries.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(beneficiaries);
    }

    // POST /api/companies/{id}/beneficiaries : Ajouter un bénéficiaire
    @PostMapping("/{id}/beneficiaries")
    public ResponseEntity<Void> addBeneficiary(
            @PathVariable Long id,
            @Valid @RequestBody BeneficiaryDto beneficiaryDto) {
        companyService.addBeneficiary(id, beneficiaryDto);
        return ResponseEntity.status(201).build();
    }

    // GET /api/companies/{id} : Récupérer une entreprise
    @GetMapping("/{id}")
    public ResponseEntity<Company> getCompany(@PathVariable Long id) {
        Company company = companyService.getCompanyById(id);
        return ResponseEntity.ok(company);
    }
}
