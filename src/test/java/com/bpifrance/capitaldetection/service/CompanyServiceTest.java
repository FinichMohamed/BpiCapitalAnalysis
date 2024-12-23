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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompanyServiceTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private BeneficiaryRepository beneficiaryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCompany() {
        // Arrange
        CompanyDto dto = new CompanyDto("Test Company");
        Company savedCompany = new Company();
        savedCompany.setId(1L);
        savedCompany.setName("Test Company");

        when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);

        // Act
        Company result = companyService.createCompany(dto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Company", result.getName());
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    void testGetCompanyById_NotFound() {
        // Arrange
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> companyService.getCompanyById(1L));
    }

    @Test
    void testAddBeneficiary_Person() {
        // Arrange
        Company company = new Company();
        company.setId(1L);
        company.setName("Company A");
        company.setBeneficiaries(new ArrayList<>());

        Person person = new Person();
        person.setId(2L);
        person.setFirstName("John");
        person.setLastName("Doe");

        BeneficiaryDto dto = new BeneficiaryDto(2L, "PERSON", 30.0);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(personRepository.findById(2L)).thenReturn(Optional.of(person));
        when(beneficiaryRepository.save(any(Beneficiary.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        companyService.addBeneficiary(1L, dto);

        // Assert
        assertEquals(1, company.getBeneficiaries().size());
        verify(beneficiaryRepository, times(1)).save(any(Beneficiary.class));
    }

    @Test
    void testAddBeneficiary_Exceeds100Percent() {
        // Arrange
        Company company = new Company();
        company.setId(1L);
        company.setName("Company A");

        List<Beneficiary> existingBeneficiaries = new ArrayList<>();
        Beneficiary existing = new Beneficiary();
        existing.setSharePercentage(80.0);
        existingBeneficiaries.add(existing);
        company.setBeneficiaries(existingBeneficiaries);

        BeneficiaryDto dto = new BeneficiaryDto(2L, "PERSON", 30.0);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            companyService.addBeneficiary(1L, dto);
        });

        assertEquals("Total share percentage exceeds 100%", exception.getMessage());
    }

    @Test
    void testGetEffectiveBeneficiaries() {
        // Arrange
        Company companyA = new Company();
        companyA.setId(1L);
        companyA.setName("Company A");

        Company companyB = new Company();
        companyB.setId(2L);
        companyB.setName("Company B");

        Person person = new Person();
        person.setId(3L);
        person.setFirstName("Alice");
        person.setLastName("Smith");

        Beneficiary beneficiaryB = new Beneficiary();
        beneficiaryB.setBeneficiaryCompany(companyB);
        beneficiaryB.setSharePercentage(60.0);

        Beneficiary beneficiaryPerson = new Beneficiary();
        beneficiaryPerson.setBeneficiaryPerson(person);
        beneficiaryPerson.setSharePercentage(50.0);

        companyA.setBeneficiaries(Collections.singletonList(beneficiaryB));
        companyB.setBeneficiaries(Collections.singletonList(beneficiaryPerson));

        when(companyRepository.findById(1L)).thenReturn(Optional.of(companyA));
        when(personRepository.findById(3L)).thenReturn(Optional.of(person));

        // Act
        List<Map<String, Object>> effectiveBeneficiaries = companyService.getEffectiveBeneficiaries(1L, "EFFECTIF");

        // Assert
        assertEquals(1, effectiveBeneficiaries.size());
        Map<String, Object> beneficiaryInfo = effectiveBeneficiaries.get(0);
        assertEquals(3L, beneficiaryInfo.get("id"));
        assertEquals("Alice", beneficiaryInfo.get("firstName"));
        assertEquals("Smith", beneficiaryInfo.get("lastName"));
        assertTrue((Double) beneficiaryInfo.get("sharePercentage") > 25.0);
    }

    @Test
    void testAddBeneficiary_InvalidType() {
        // Arrange
        BeneficiaryDto dto = new BeneficiaryDto(1L, "INVALID_TYPE", 20.0);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            companyService.addBeneficiary(1L, dto);
        });

        assertEquals("Invalid beneficiary type: INVALID_TYPE", exception.getMessage());
    }
}
