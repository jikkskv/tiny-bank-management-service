package com.tinybank.management.model.account;

public record CreateAccountRequestModel(String name, String userName, String password, String role) {
}
