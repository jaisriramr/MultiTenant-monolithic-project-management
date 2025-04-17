package com.projectmanagement.multitenantprojectmanagement.helper;

import org.springframework.stereotype.Component;

@Component
public class NullChecker<T> {
    public static <T> boolean isNullOrEmpty(T value) {
        if(value == null) {
            return true;
        }

        return false;
    }
}
