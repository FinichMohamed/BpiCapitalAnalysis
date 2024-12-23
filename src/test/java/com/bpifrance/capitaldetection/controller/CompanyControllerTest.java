package com.bpifrance.capitaldetection.controller;

import com.bpifrance.capitaldetection.dto.BeneficiaryDto;
import com.bpifrance.capitaldetection.dto.CompanyDto;
import com.bpifrance.capitaldetection.entity.Company;
import com.bpifrance.capitaldetection.exception.ResourceNotFoundException;
import com.bpifrance.capitaldetection.service.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyController.class)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService companyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateCompany() throws Exception {
        CompanyDto dto = new CompanyDto("Test Company");
        Company savedCompany = new Company();
        savedCompany.setId(1L);
        savedCompany.setName("Test Company");

        when(companyService.createCompany(any(CompanyDto.class))).thenReturn(savedCompany);

        mockMvc.perform(post("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/companies/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Company"));
    }

    @Test
    void testCreateCompany_InvalidInput() throws Exception {
        CompanyDto dto = new CompanyDto(""); // Invalid name

        mockMvc.perform(post("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBeneficiaries_NoContent() throws Exception {
        when(companyService.getEffectiveBeneficiaries(1L, "ALL")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/companies/1/beneficiaries")
                        .param("type", "ALL"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetBeneficiaries_Success() throws Exception {
        List<Map<String, Object>> beneficiaries = List.of(
                Map.of("id", 1L, "firstName", "Alice", "lastName", "Smith", "sharePercentage", 30.0),
                Map.of("id", 2L, "firstName", "Bob", "lastName", "Jones", "sharePercentage", 40.0)
        );

        when(companyService.getEffectiveBeneficiaries(1L, "ALL")).thenReturn(beneficiaries);

        mockMvc.perform(get("/api/companies/1/beneficiaries")
                        .param("type", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[1].sharePercentage").value(40.0));
    }

    @Test
    void testGetBeneficiaries_CompanyNotFound() throws Exception {
        when(companyService.getEffectiveBeneficiaries(999L, "ALL"))
                .thenThrow(new ResourceNotFoundException("Company not found"));

        mockMvc.perform(get("/api/companies/999/beneficiaries")
                        .param("type", "ALL"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Company not found"));
    }

    @Test
    void testAddBeneficiary() throws Exception {
        BeneficiaryDto dto = new BeneficiaryDto(2L, "PERSON", 30.0);

        doNothing().when(companyService).addBeneficiary(1L, dto);

        mockMvc.perform(post("/api/companies/1/beneficiaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void testAddBeneficiary_InvalidInput() throws Exception {
        BeneficiaryDto dto = new BeneficiaryDto(2L, "INVALID_TYPE", 30.0);

        mockMvc.perform(post("/api/companies/1/beneficiaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddBeneficiary_CompanyNotFound() throws Exception {
        BeneficiaryDto dto = new BeneficiaryDto(2L, "PERSON", 30.0);

        doThrow(new ResourceNotFoundException("Company not found"))
                .when(companyService).addBeneficiary(999L, dto);

        mockMvc.perform(post("/api/companies/999/beneficiaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Company not found"));
    }
}
