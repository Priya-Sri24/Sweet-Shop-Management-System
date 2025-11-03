package com.sweetshop.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

class SweetTest {

    private Sweet sweet;

    @BeforeEach
    void setUp() {
        sweet = new Sweet();
    }

    @Test
    void testSweetCreation() {
        // Given
        String name = "Chocolate Cake";
        String category = "Cakes";
        BigDecimal price = new BigDecimal("15.99");
        Integer quantity = 10;
        String description = "Delicious chocolate cake";

        // When
        sweet.setName(name);
        sweet.setCategory(category);
        sweet.setPrice(price);
        sweet.setQuantity(quantity);
        sweet.setDescription(description);

        // Then
        assertEquals(name, sweet.getName());
        assertEquals(category, sweet.getCategory());
        assertEquals(price, sweet.getPrice());
        assertEquals(quantity, sweet.getQuantity());
        assertEquals(description, sweet.getDescription());
    }

    @Test
    void testSweetConstructorWithAllArgs() {
        // Given
        Long id = 1L;
        String name = "Vanilla Cupcake";
        String category = "Cupcakes";
        BigDecimal price = new BigDecimal("5.99");
        Integer quantity = 20;
        String description = "Sweet vanilla cupcake";

        // When
        Sweet sweetWithArgs = new Sweet(id, name, category, price, quantity, description);

        // Then
        assertEquals(id, sweetWithArgs.getId());
        assertEquals(name, sweetWithArgs.getName());
        assertEquals(category, sweetWithArgs.getCategory());
        assertEquals(price, sweetWithArgs.getPrice());
        assertEquals(quantity, sweetWithArgs.getQuantity());
        assertEquals(description, sweetWithArgs.getDescription());
    }

    @Test
    void testSweetEqualsAndHashCode() {
        // Given
        Sweet sweet1 = new Sweet(1L, "Brownie", "Brownies", new BigDecimal("8.99"), 15, "Fudgy brownie");
        Sweet sweet2 = new Sweet(1L, "Brownie", "Brownies", new BigDecimal("8.99"), 15, "Fudgy brownie");

        // Then
        assertEquals(sweet1, sweet2);
        assertEquals(sweet1.hashCode(), sweet2.hashCode());
    }
}