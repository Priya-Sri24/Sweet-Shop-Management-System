package com.sweetshop.controller;

import com.sweetshop.dto.ApiResponse;
import com.sweetshop.dto.PurchaseRequest;
import com.sweetshop.dto.SweetRequest;
import com.sweetshop.model.Sweet;
import com.sweetshop.service.SweetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/sweets")
@CrossOrigin(origins = "*")
@Tag(name = "Sweets Management", description = "CRUD operations for sweets inventory")
@SecurityRequirement(name = "bearerAuth")
public class SweetController {

    @Autowired
    private SweetService sweetService;

    @PostMapping
    @Operation(summary = "Create new sweet", description = "Add a new sweet to the inventory")
    public ResponseEntity<?> createSweet(@Valid @RequestBody SweetRequest request) {
        try {
            Sweet sweet = sweetService.createSweet(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Sweet created successfully", sweet));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get all sweets", description = "Retrieve list of all available sweets")
    public ResponseEntity<?> getAllSweets() {
        try {
            List<Sweet> sweets = sweetService.getAllSweets();
            return ResponseEntity.ok(new ApiResponse(true, "Sweets retrieved successfully", sweets));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sweet by ID", description = "Retrieve a specific sweet by its ID")
    public ResponseEntity<?> getSweetById(
        @Parameter(description = "Sweet ID") @PathVariable Long id) {
        try {
            Sweet sweet = sweetService.getSweetById(id);
            return ResponseEntity.ok(new ApiResponse(true, "Sweet retrieved successfully", sweet));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search sweets", description = "Search sweets by name, category, or price range")
    public ResponseEntity<?> searchSweets(
            @Parameter(description = "Sweet name (partial match)") @RequestParam(required = false) String name,
            @Parameter(description = "Sweet category") @RequestParam(required = false) String category,
            @Parameter(description = "Minimum price") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price") @RequestParam(required = false) BigDecimal maxPrice) {
        try {
            List<Sweet> sweets = sweetService.searchSweets(name, category, minPrice, maxPrice);
            return ResponseEntity.ok(new ApiResponse(true, "Search completed successfully", sweets));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update sweet", description = "Update an existing sweet's details")
    public ResponseEntity<?> updateSweet(
        @Parameter(description = "Sweet ID") @PathVariable Long id, 
        @Valid @RequestBody SweetRequest request) {
        try {
            Sweet sweet = sweetService.updateSweet(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Sweet updated successfully", sweet));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete sweet (Admin only)", description = "Remove a sweet from inventory")
    public ResponseEntity<?> deleteSweet(
        @Parameter(description = "Sweet ID") @PathVariable Long id) {
        try {
            sweetService.deleteSweet(id);
            return ResponseEntity.ok(new ApiResponse(true, "Sweet deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/{id}/purchase")
    @Operation(summary = "Purchase sweet", description = "Buy a sweet and decrease its quantity")
    public ResponseEntity<?> purchaseSweet(
        @Parameter(description = "Sweet ID") @PathVariable Long id, 
        @Valid @RequestBody PurchaseRequest request) {
        try {
            Sweet sweet = sweetService.purchaseSweet(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Purchase completed successfully", sweet));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/{id}/restock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Restock sweet (Admin only)", description = "Increase sweet quantity")
    public ResponseEntity<?> restockSweet(
        @Parameter(description = "Sweet ID") @PathVariable Long id, 
        @Valid @RequestBody PurchaseRequest request) {
        try {
            Sweet sweet = sweetService.restockSweet(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Restock completed successfully", sweet));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
}