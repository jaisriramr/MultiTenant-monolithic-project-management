package com.projectmanagement.multitenantprojectmanagement.permissions.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListPermissionByNames {
    private String name;
}
