package com.webapp.springBoot.security.JWTConfig;

import com.webapp.springBoot.entity.Roles;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RecordToken(UUID id, String nickname, List<String> authorities, Instant createAt, Instant expiresAt ) {
}
