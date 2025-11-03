package com.sweetshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetshop.dto.SweetRequest;
import com.sweetshop.model.Sweet;
import com.sweetshop.service.SweetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SweetController.class)
class SweetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SweetService sweetService;

    @Autowired
    private ObjectMapper objectMapper;

    private Sweet testSweet;
    private SweetRequest sweetRequest;

    @BeforeEach
    void setUp() {
        testSweet = new Sweet(1L, "Chocolate Cake", "Cakes", new BigDecimal("15.99"), 10, "Delicious chocolate cake");
        sweetRequest = new SweetRequest();
        sweetRequest.setName("Chocolate Cake");
        sweetRequest.setCategory("Cakes");
        sweetRequest.setPrice(new BigDecimal("15.99"));
        sweetRequest.setQuantity(10);
        sweetRequest.setDescription("Delicious chocolate cake");
    }

    @Test
    @WithMockUser
    void testGetAllSweets() throws Exception {
        // Given
        List<Sweet> sweets = Arrays.asList(testSweet);
        when(sweetService.getAllSweets()).thenReturn(sweets);

        // When & Then
        mockMvc.perform(get("/api/sweets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Chocolate Cake"))
                .andExpect(jsonPath("$[0].category").value("Cakes"))
                .andExpect(jsonPath("$[0].price").value(15.99));

        verify(sweetService).getAllSweets();
    }

    @Test
    @WithMockUser
    void testGetSweetById_Found() throws Exception {
        // Given
        when(sweetService.getSweetById(1L)).thenReturn(Optional.of(testSweet));

        // When & Then
        mockMvc.perform(get("/api/sweets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Chocolate Cake"))
                .andExpect(jsonPath("$.category").value("Cakes"));

        verify(sweetService).getSweetById(1L);
    }

    @Test
    @WithMockUser
    void testGetSweetById_NotFound() throws Exception {
        // Given
        when(sweetService.getSweetById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/sweets/1"))
                .andExpect(status().isNotFound());

        verify(sweetService).getSweetById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateSweet() throws Exception {
        // Given
        when(sweetService.createSweet(any(Sweet.class))).thenReturn(testSweet);

        // When & Then
        mockMvc.perform(post("/api/sweets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sweetRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Chocolate Cake"));

        verify(sweetService).createSweet(any(Sweet.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateSweet() throws Exception {
        // Given
        when(sweetService.getSweetById(1L)).thenReturn(Optional.of(testSweet));
        when(sweetService.updateSweet(any(Sweet.class))).thenReturn(testSweet);

        // When & Then
        mockMvc.perform(put("/api/sweets/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sweetRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Chocolate Cake"));

        verify(sweetService).getSweetById(1L);
        verify(sweetService).updateSweet(any(Sweet.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteSweet() throws Exception {
        // Given
        when(sweetService.getSweetById(1L)).thenReturn(Optional.of(testSweet));

        // When & Then
        mockMvc.perform(delete("/api/sweets/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(sweetService).getSweetById(1L);
        verify(sweetService).deleteSweet(1L);
    }

    @Test
    @WithMockUser
    void testSearchSweets() throws Exception {
        // Given
        List<Sweet> sweets = Arrays.asList(testSweet);
        when(sweetService.searchSweets("chocolate")).thenReturn(sweets);

        // When & Then
        mockMvc.perform(get("/api/sweets/search")
                .param("query", "chocolate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Chocolate Cake"));

        verify(sweetService).searchSweets("chocolate");
    }

    @Test
    @WithMockUser
    void testPurchaseSweet_Success() throws Exception {
        // Given
        when(sweetService.purchaseSweet(1L, 2)).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/sweets/1/purchase")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantity\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Purchase successful"));

        verify(sweetService).purchaseSweet(1L, 2);
    }

    @Test
    @WithMockUser
    void testPurchaseSweet_Failed() throws Exception {
        // Given
        when(sweetService.purchaseSweet(1L, 15)).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/sweets/1/purchase")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantity\": 15}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient quantity"));

        verify(sweetService).purchaseSweet(1L, 15);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRestockSweet() throws Exception {
        // Given
        when(sweetService.restockSweet(1L, 5)).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/sweets/1/restock")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantity\": 5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Restock successful"));

        verify(sweetService).restockSweet(1L, 5);
    }
}