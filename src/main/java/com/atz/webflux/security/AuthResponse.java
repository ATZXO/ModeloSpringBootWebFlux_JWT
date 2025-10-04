package com.atz.webflux.security;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(@JsonProperty(value = "access_token") String token) {
}
