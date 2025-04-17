package com.projectmanagement.multitenantprojectmanagement.helper.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UtilPaginatedResponse<T> {
    private List<T> data;
    private int page;
    private int size;
}
