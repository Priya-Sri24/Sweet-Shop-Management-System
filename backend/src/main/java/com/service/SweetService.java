package com.sweetshop.service;

import com.sweetshop.dto.PurchaseRequest;
import com.sweetshop.dto.SweetRequest;
import com.sweetshop.model.Sweet;
import com.sweetshop.repository.SweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class SweetService {

    @Autowired
    private SweetRepository sweetRepository;

    public Sweet createSweet(Sweet sweet) {
        return sweetRepository.save(sweet);
    }

    public Sweet createSweet(SweetRequest request) {
        Sweet sweet = new Sweet();
        sweet.setName(request.getName());
        sweet.setCategory(request.getCategory());
        sweet.setPrice(request.getPrice());
        sweet.setQuantity(request.getQuantity());
        sweet.setDescription(request.getDescription());
        return sweetRepository.save(sweet);
    }

    public List<Sweet> getAllSweets() {
        return sweetRepository.findAll();
    }

    public Optional<Sweet> getSweetById(Long id) {
        return sweetRepository.findById(id);
    }

    public Sweet getSweetByIdOrThrow(Long id) {
        return sweetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sweet not found with id: " + id));
    }

    public List<Sweet> searchSweets(String query) {
        return sweetRepository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(query, query);
    }

    public List<Sweet> searchSweets(String name, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        return sweetRepository.searchSweets(name, category, minPrice, maxPrice);
    }

    public Sweet updateSweet(Sweet sweet) {
        return sweetRepository.save(sweet);
    }

    public Sweet updateSweet(Long id, SweetRequest request) {
        Sweet sweet = getSweetByIdOrThrow(id);
        sweet.setName(request.getName());
        sweet.setCategory(request.getCategory());
        sweet.setPrice(request.getPrice());
        sweet.setQuantity(request.getQuantity());
        sweet.setDescription(request.getDescription());
        return sweetRepository.save(sweet);
    }

    public void deleteSweet(Long id) {
        sweetRepository.deleteById(id);
    }

    @Transactional
    public boolean purchaseSweet(Long id, int quantity) {
        Optional<Sweet> optionalSweet = sweetRepository.findById(id);
        if (optionalSweet.isEmpty()) {
            return false;
        }
        
        Sweet sweet = optionalSweet.get();
        if (sweet.getQuantity() < quantity) {
            return false;
        }
        
        sweet.setQuantity(sweet.getQuantity() - quantity);
        sweetRepository.save(sweet);
        return true;
    }

    @Transactional
    public Sweet purchaseSweet(Long id, PurchaseRequest request) {
        Sweet sweet = getSweetByIdOrThrow(id);
        
        if (sweet.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Insufficient quantity. Available: " + sweet.getQuantity());
        }
        
        sweet.setQuantity(sweet.getQuantity() - request.getQuantity());
        return sweetRepository.save(sweet);
    }

    @Transactional
    public boolean restockSweet(Long id, int quantity) {
        Optional<Sweet> optionalSweet = sweetRepository.findById(id);
        if (optionalSweet.isEmpty()) {
            return false;
        }
        
        Sweet sweet = optionalSweet.get();
        sweet.setQuantity(sweet.getQuantity() + quantity);
        sweetRepository.save(sweet);
        return true;
    }

    @Transactional
    public Sweet restockSweet(Long id, PurchaseRequest request) {
        Sweet sweet = getSweetByIdOrThrow(id);
        sweet.setQuantity(sweet.getQuantity() + request.getQuantity());
        return sweetRepository.save(sweet);
    }
}