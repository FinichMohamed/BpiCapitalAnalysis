package com.bpifrance.capitaldetection.dto;

import jakarta.validation.constraints.NotBlank;

public record CompanyDto(
        @NotBlank String name
) {
}
