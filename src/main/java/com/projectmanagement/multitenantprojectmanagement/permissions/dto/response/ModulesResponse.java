package com.projectmanagement.multitenantprojectmanagement.permissions.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModulesResponse {
    private List<String> modules;
    private Integer size;
}
