package com.projectmanagement.multitenantprojectmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MultiTenantProjectManagementApplication {

	public static void main(String[] args) {

		SpringApplication.run(MultiTenantProjectManagementApplication.class, args);
	}
}
