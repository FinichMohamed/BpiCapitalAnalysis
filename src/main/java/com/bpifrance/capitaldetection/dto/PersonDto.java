package com.bpifrance.capitaldetection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PersonDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull LocalDate birthDate
) {}