package com.project.code.Controller;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Service.ServiceClass;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ServiceClass serviceClass;

    // 1️⃣ Update Inventory
    @PutMapping("/update")
    public Map<String, String> updateInventory(@RequestBody CombinedRequest combinedRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            Product product = combinedRequest.getProduct();
            Inventory inventory = combinedRequest.getInventory();

            // Validate product ID
            if (!serviceClass.validateProductId(product.getId())) {
                response.put("message", "Product ID is invalid");
                return response;
            }

            // Update product and inventory
            Inventory existingInventory = serviceClass.getInventoryId(inventory);
            if (existingInventory != null) {
                // Update inventory fields
                existingInventory.setStockLevel(inventory.getStockLevel());
                inventoryRepository.save(existingInventory);
                response.put("message", "Successfully updated product");
            } else {
                response.put("message", "No data available");
            }
        } catch (DataIntegrityViolationException e) {
            response.put("message", "Data integrity error: " + e.getMessage());
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
        }
        return response;
    }

    // 2️⃣ Save Inventory
    @PostMapping("/save")
    public Map<String, String> saveInventory(@RequestBody Inventory inventory) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!serviceClass.validateInventory(inventory)) {
                response.put("message", "Inventory data already exists for this product-store pair");
            } else {
                inventoryRepository.save(inventory);
                response.put("message", "Inventory saved successfully");
            }
        } catch (DataIntegrityViolationException e) {
            response.put("message", "Data integrity error: " + e.getMessage());
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
        }
        return response;
    }

    // 3️⃣ Get all products for a store
    @GetMapping("/{storeId}")
    public Map<String, Object> getAllProducts(@PathVariable("storeId") Long storeId) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findProductsByStoreId(storeId);
        response.put("products", products);
        return response;
    }

    // 4️⃣ Filter products by category and name
    @GetMapping("/filter/{category}/{name}/{storeId}")
    public Map<String, Object> getProductName(@PathVariable("category") String category,
                                              @PathVariable("name") String name,
                                              @PathVariable("storeId") Long storeId) {
        Map<String, Object> response = new HashMap<>();
        List<Product> filteredProducts;

        if (category.equals("null") && !name.equals("null")) {
            filteredProducts = productRepository.findByNameLike(storeId, name);
        } else if (!category.equals("null") && name.equals("null")) {
            filteredProducts = productRepository.findByCategoryAndStoreId(storeId, category);
        } else {
            filteredProducts = productRepository.findByNameAndCategory(storeId, name, category);
        }

        response.put("product", filteredProducts);
        return response;
    }

    // 5️⃣ Search products by name in a store
    @GetMapping("/search/{name}/{storeId}")
    public Map<String, Object> searchProduct(@PathVariable("name") String name,
                                             @PathVariable("storeId") Long storeId) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findByNameLike(storeId, name);
        response.put("product", products);
        return response;
    }

    // 6️⃣ Remove a product
    @DeleteMapping("/{id}")
    public Map<String, String> removeProduct(@PathVariable("id") Long id) {
        Map<String, String> response = new HashMap<>();

        if (!serviceClass.validateProductId(id)) {
            response.put("message", "Product not present in database");
        } else {
            inventoryRepository.deleteByProductId(id);
            response.put("message", "Product deleted successfully");
        }

        return response;
    }

    // 7️⃣ Validate quantity of a product in a store
    @GetMapping("/validate/{quantity}/{storeId}/{productId}")
    public boolean validateQuantity(@PathVariable("quantity") Integer quantity,
                                    @PathVariable("storeId") Long storeId,
                                    @PathVariable("productId") Long productId) {

        Inventory inventory = inventoryRepository.findByProductIdAndStoreId(productId, storeId);
        if (inventory == null) return false;

        return inventory.getStockLevel() >= quantity;
    }
}