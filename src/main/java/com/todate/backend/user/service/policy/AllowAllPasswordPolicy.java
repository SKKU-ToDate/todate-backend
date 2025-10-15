package com.todate.backend.user.service.policy;

import org.springframework.stereotype.Component;

@Component
public class AllowAllPasswordPolicy implements PasswordPolicy {
    @Override
    public boolean validate(String rawPassword) {
        return true;
    }
}
