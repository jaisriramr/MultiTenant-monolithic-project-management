package com.projectmanagement.multitenantprojectmanagement.users.embeddable;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class About {
    private String jobTitle;
    private String department;
    private String companyName;
    private String location;
}
