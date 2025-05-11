package com.webapp.springBoot.cache;

import com.webapp.springBoot.DTO.Users.UserRequestDTO;

public record CacheSaveVerifyRecord(UserRequestDTO userRequestDTO, String code) {
}
