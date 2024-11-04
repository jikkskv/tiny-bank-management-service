package com.tinybank.management.model.account;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateAccountRequestModel(String name, String userName, String password,
                                        @Schema(description = "role code", example = "user/admin") String role) {
}
