package com.webapp.springBoot.util;

import com.webapp.springBoot.DTO.Users.UserRequestDTO;

public record CacheSaveVerifyRecord(UserRequestDTO userRequestDTO, String code) {
}
