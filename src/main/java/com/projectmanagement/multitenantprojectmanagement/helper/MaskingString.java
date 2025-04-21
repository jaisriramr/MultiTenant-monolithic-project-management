package com.projectmanagement.multitenantprojectmanagement.helper;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MaskingString {

    public String maskSensitive(String value) {
        if (value == null || value.length() <= 4) {
            return "****";
        }
        return value.substring(0, 4) + "****";
    }
}
