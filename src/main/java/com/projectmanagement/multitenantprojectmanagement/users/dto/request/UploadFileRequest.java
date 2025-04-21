package com.projectmanagement.multitenantprojectmanagement.users.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadFileRequest {
    @NotBlank(message = "Type must be passed")
    private String type;
    @NotBlank(message = "File must be passed")
    private MultipartFile file;
}
