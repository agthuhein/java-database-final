package com.project.code.Controller;
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
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ServiceClass serviceClass;

    @Autowired
    private InventoryRepository inventoryRepository;

    // 1️⃣ Add Product
    @PostMapping("/add")
    public Map<String, String> addProduct(@RequestBody Product product) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!serviceClass.validateProduct(product)) {
                response.put("message", "Product already exists");
                return response;
            }
            productRepository.save(product);
            response.put("message", "Product saved successfully");
        } catch (DataIntegrityViolationException e) {
            response.put("message", "Data integrity error: " + e.getMessage());
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
        }
        return response;
    }

    // 2️⃣ Get product by ID
    @GetMapping("/product/{id}")
    public Map<String, Object> getProductbyId(@PathVariable("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Product> product = productRepository.findById(id);
        response.put("products", product);
        return response;
    }

    // 3️⃣ Update Product
    @PutMapping("/update")
    public Map<String, String> updateProduct(@RequestBody Product product) {
        Map<String, String> response = new HashMap<>();
        try {
            productRepository.save(product);
            response.put("message", "Product updated successfully");
        } catch (DataIntegrityViolationException e) {
            response.put("message", "Data integrity error: " + e.getMessage());
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
        }
        return response;
    }

    // 4️⃣ Filter by Name and Category
    @GetMapping("/category/{name}/{category}")
    public Map<String, Object> filterbyCategoryProduct(@PathVariable("name") String name,
                                                       @PathVariable("category") String category) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products;

        if (name.equals("null") && !category.equals("null")) {
            products = productRepository.findByCategory(category);
        } else if (!name.equals("null") && category.equals("null")) {
            products = productRepository.findProductBySubName(name);
        } else {
            products = productRepository.findProductBySubNameAndCategory(name, category);
        }

        response.put("products", products);
        return response;
    }

    // 5️⃣ List all products
    @GetMapping("/all")
    public Map<String, Object> listProduct() {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findAll();
        response.put("products", products);
        return response;
    }

    // 6️⃣ Get products by Category and Store ID
    @GetMapping("/filter/{category}/{storeId}")
    public Map<String, Object> getProductbyCategoryAndStoreId(@PathVariable("category") String category,
                                                              @PathVariable("storeId") Long storeId) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findProductByCategory(category, storeId);
        response.put("product", products);
        return response;
    }

    // 7️⃣ Delete product
    @DeleteMapping("/{id}")
    public Map<String, String> deleteProduct(@PathVariable("id") Long id) {
        Map<String, String> response = new HashMap<>();
        if (!serviceClass.validateProductId(id)) {
            response.put("message", "Product not present in database");
            return response;
        }

        // Delete inventory first
        inventoryRepository.deleteByProductId(id);
        // Delete product
        productRepository.deleteById(id);
        response.put("message", "Product deleted successfully");
        return response;
    }

    // 8️⃣ Search product by name
    @GetMapping("/searchProduct/{name}")
    public Map<String, Object> searchProduct(@PathVariable("name") String name) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findProductBySubName(name);
        response.put("products", products);
        return response;
    }
}