package com.tinybank.management.user;

import java.util.HashMap;
import java.util.Map;

public enum Role {
    USER("user"), ADMIN("admin");

    private final String roleCode;

    Role(String roleCode) {
        this.roleCode = roleCode;
    }

    private static final Map<String, Role> roleCodeMap = new HashMap<>(values().length);

    static {
        for (Role c : values()) roleCodeMap.put(c.roleCode, c);
    }

    public static Role of(String code) {
        Role result = roleCodeMap.get(code);
        if (result == null) {
            throw new IllegalArgumentException("Invalid input code: " + code);
        }
        return result;
    }
}
