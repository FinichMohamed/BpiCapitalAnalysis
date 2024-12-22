package com.bpifrance.capitaldetection.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record BeneficiaryDto(
        @NotNull Long beneficiaryId,
        @NotNull String beneficiaryType, // "PERSON" ou "COMPANY"
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") Double sharePercentage
) {}