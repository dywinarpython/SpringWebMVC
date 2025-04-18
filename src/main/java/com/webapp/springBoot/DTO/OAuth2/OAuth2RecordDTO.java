package com.webapp.springBoot.DTO.OAuth2;

import jakarta.validation.constraints.NotNull;

public record OAuth2RecordDTO(@NotNull String code, String code_verifier) {
}
