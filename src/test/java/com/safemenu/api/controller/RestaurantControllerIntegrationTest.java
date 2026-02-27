package com.safemenu.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safemenu.api.dto.request.RestaurantRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RestaurantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/restaurants — returns all restaurants")
    void shouldReturnAllRestaurants() throws Exception {
        mockMvc.perform(get("/api/v1/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name").value("The Green Kitchen"));
    }

    @Test
    @DisplayName("POST + PUT + DELETE lifecycle")
    void shouldHandleFullCrudLifecycle() throws Exception {
        // CREATE
        RestaurantRequest createReq = RestaurantRequest.builder()
                .name("Test Bistro")
                .address("1 Test Lane, Dublin 1")
                .email("test@bistro.ie")
                .build();

        String response = mockMvc.perform(post("/api/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Bistro"))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        // UPDATE
        RestaurantRequest updateReq = RestaurantRequest.builder()
                .name("Test Bistro — Updated")
                .address("1 Test Lane, Dublin 1")
                .build();

        mockMvc.perform(put("/api/v1/restaurants/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Bistro — Updated"));

        // DELETE
        mockMvc.perform(delete("/api/v1/restaurants/" + id))
                .andExpect(status().isNoContent());

        // VERIFY GONE
        mockMvc.perform(get("/api/v1/restaurants/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/restaurants — rejects blank name")
    void shouldRejectBlankName() throws Exception {
        RestaurantRequest request = RestaurantRequest.builder()
                .name("")
                .address("Somewhere")
                .build();

        mockMvc.perform(post("/api/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.name").exists());
    }
}
