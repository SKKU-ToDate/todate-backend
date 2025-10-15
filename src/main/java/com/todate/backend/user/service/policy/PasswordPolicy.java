package com.todate.backend.user.service.policy;

public interface PasswordPolicy {
    boolean validate(String rawPassword);
}
