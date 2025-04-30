package com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnBoardRequest {
    
    private String userName;
    private String userEmail;
    private String userAuth0Id;

    private String orgName;
    private String orgDisplayName;
    private String orgDomain;
}
