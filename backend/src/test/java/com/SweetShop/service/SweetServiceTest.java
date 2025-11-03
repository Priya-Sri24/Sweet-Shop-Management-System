package com.sweetshop.service;

import com.sweetshop.model.Sweet;
import com.sweetshop.repository.SweetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SweetServiceTest {

    @Mock
    private SweetRepository sweetRepository;

    @InjectMocks
    private SweetService sweetService;

    private Sweet testSweet;

    @BeforeEach
    void setUp() {
        testSweet = new Sweet(1L, "Chocolate Cake", "Cakes", new BigDecimal("15.99"), 10, "Delicious chocolate cake");
    }

    @Test
    void testGetAllSweets() {
        // Given
        List<Sweet> expectedSweets = Arrays.asList(testSweet);
        when(sweetRepository.findAll()).thenReturn(expectedSweets);

        // When
        List<Sweet> actualSweets = sweetService.getAllSweets();

        // Then
        assertEquals(expectedSweets, actualSweets);
        verify(sweetRepository).findAll();
    }

    @Test
    void testGetSweetById_Found() {
        // Given
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));

        // When
        Optional<Sweet> result = sweetService.getSweetById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testSweet, result.get());
        verify(sweetRepository).findById(1L);
    }

    @Test
    void testGetSweetById_NotFound() {
        // Given
        when(sweetRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<Sweet> result = sweetService.getSweetById(1L);

        // Then
        assertFalse(result.isPresent());
        verify(sweetRepository).findById(1L);
    }

    @Test
    void testCreateSweet() {
        // Given
        when(sweetRepository.save(any(Sweet.class))).thenReturn(testSweet);

        // When
        Sweet result = sweetService.createSweet(testSweet);

        // Then
        assertEquals(testSweet, result);
        verify(sweetRepository).save(testSweet);
    }

    @Test
    void testUpdateSweet() {
        // Given
        when(sweetRepository.save(any(Sweet.class))).thenReturn(testSweet);

        // When
        Sweet result = sweetService.updateSweet(testSweet);

        // Then
        assertEquals(testSweet, result);
        verify(sweetRepository).save(testSweet);
    }

    @Test
    void testDeleteSweet() {
        // When
        sweetService.deleteSweet(1L);

        // Then
        verify(sweetRepository).deleteById(1L);
    }

    @Test
    void testSearchSweets() {
        // Given
        List<Sweet> expectedSweets = Arrays.asList(testSweet);
        when(sweetRepository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(anyString(), anyString()))
                .thenReturn(expectedSweets);

        // When
        List<Sweet> result = sweetService.searchSweets("chocolate");

        // Then
        assertEquals(expectedSweets, result);
        verify(sweetRepository).findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase("chocolate", "chocolate");
    }

    @Test
    void testPurchaseSweet_Success() {
        // Given
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));
        when(sweetRepository.save(any(Sweet.class))).thenReturn(testSweet);

        // When
        boolean result = sweetService.purchaseSweet(1L, 2);

        // Then
        assertTrue(result);
        assertEquals(8, testSweet.getQuantity()); // 10 - 2 = 8
        verify(sweetRepository).findById(1L);
        verify(sweetRepository).save(testSweet);
    }

    @Test
    void testPurchaseSweet_InsufficientQuantity() {
        // Given
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));

        // When
        boolean result = sweetService.purchaseSweet(1L, 15); // More than available

        // Then
        assertFalse(result);
        assertEquals(10, testSweet.getQuantity()); // Quantity unchanged
        verify(sweetRepository).findById(1L);
        verify(sweetRepository, never()).save(any());
    }

    @Test
    void testRestockSweet() {
        // Given
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));
        when(sweetRepository.save(any(Sweet.class))).thenReturn(testSweet);

        // When
        boolean result = sweetService.restockSweet(1L, 5);

        // Then
        assertTrue(result);
        assertEquals(15, testSweet.getQuantity()); // 10 + 5 = 15
        verify(sweetRepository).findById(1L);
        verify(sweetRepository).save(testSweet);
    }
}