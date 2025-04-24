package com.webapp.springBoot.util;

import com.webapp.springBoot.DTO.Users.UserRequestDTO;

public record CacheSaveVerify(UserRequestDTO userRequestDTO, String code) {
}
