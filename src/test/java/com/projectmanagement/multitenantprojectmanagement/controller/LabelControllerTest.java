package com.projectmanagement.multitenantprojectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.multitenantprojectmanagement.core.label.Label;
import com.projectmanagement.multitenantprojectmanagement.core.label.LabelController;
import com.projectmanagement.multitenantprojectmanagement.core.label.LabelService;
import com.projectmanagement.multitenantprojectmanagement.core.label.dto.request.CreateLabelRequest;
import com.projectmanagement.multitenantprojectmanagement.core.label.dto.response.LabelResponse;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LabelController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LabelService labelService;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private Jwt jwt;

    private UUID labelId;
    private UUID projectId;
    private Label label;
    private LabelResponse labelResponse;
    private PaginatedResponseDto<LabelResponse> paginatedResponseDto;
    private List<LabelResponse> labelResponseList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        labelId = UUID.randomUUID();
        projectId = UUID.randomUUID();

        label = new Label();
        label.setId(labelId);
        label.setName("name");

        labelResponse = new LabelResponse();
        labelResponse.setId(labelId);
        labelResponse.setName("name");
        labelResponseList = Collections.singletonList(labelResponse);
        paginatedResponseDto = PaginatedResponseDto.<LabelResponse>builder()
                .data(labelResponseList)
                .totalElements(1L)
                .totalPages(1)
                .size(10)
                .build();

    }

    @Test
    void testGetLabelById_Success() throws Exception {
        LabelResponse response = new LabelResponse();
        response.setId(labelId);

        when(labelService.getLabelById(labelId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/label/{id}", labelId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(labelId.toString()));

        verify(labelService, times(1)).getLabelById(labelId);
    }

    @Test
    void testGetLabelsByProjectId_Success() throws Exception {

        when(labelService.getLabelsByProjectId(eq(projectId), any())).thenReturn(paginatedResponseDto);

        mockMvc.perform(get("/api/v1/label/{id}/project", projectId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(labelService, times(1)).getLabelsByProjectId(eq(projectId), any());
    }

    @Test
    void testCreateLabel_Success() throws Exception {
        CreateLabelRequest request = new CreateLabelRequest();
        LabelResponse response = new LabelResponse();
        response.setId(labelId);

        when(labelService.createLabel(any(CreateLabelRequest.class))).thenReturn(label);
        when(labelService.getLabelById(labelId)).thenReturn(response);

        mockMvc.perform(post("/api/v1/label")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(labelId.toString()));

        verify(labelService, times(1)).createLabel(any(CreateLabelRequest.class));
    }

    @Test
    void testDeleteLabelById_Success() throws Exception {
        LabelResponse response = new LabelResponse();
        response.setId(labelId);

        when(labelService.deleteLabelById(labelId)).thenReturn(response);

        mockMvc.perform(delete("/api/v1/label/{id}", labelId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(labelId.toString()));

        verify(labelService, times(1)).deleteLabelById(labelId);
    }
}
