package com.project.code.Controller;

import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.Store;
import com.project.code.Repo.StoreRepository;
import com.project.code.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderService orderService;

    // 1️⃣ Add a new store
    @PostMapping("/add")
    public Map<String, String> addStore(@RequestBody Store store) {
        Map<String, String> response = new HashMap<>();
        try {
            Store savedStore = storeRepository.save(store);
            response.put("message", "Store created successfully with ID: " + savedStore.getId());
        } catch (Exception e) {
            response.put("message", "Error creating store: " + e.getMessage());
        }
        return response;
    }

    // 2️⃣ Validate if a store exists
    @GetMapping("/validate/{storeId}")
    public boolean validateStore(@PathVariable("storeId") Long storeId) {
        return storeRepository.findById(storeId).isPresent();
    }

    // 3️⃣ Place an order
    @PostMapping("/placeOrder")
    public Map<String, String> placeOrder(@RequestBody PlaceOrderRequestDTO placeOrderRequestDTO) {
        Map<String, String> response = new HashMap<>();
        try {
            orderService.saveOrder(placeOrderRequestDTO);
            response.put("message", "Order placed successfully");
        } catch (Exception e) {
            response.put("Error", e.getMessage());
        }
        return response;
    }
}