package com.project.code.Service;


import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiceClass {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public ServiceClass(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Validate if an inventory record exists for a product-store pair.
     * @param inventory Inventory object containing product and store
     * @return false if inventory already exists, true otherwise
     */
    public boolean validateInventory(Inventory inventory) {
        Inventory existingInventory = inventoryRepository.findByProductIdAndStoreId(
                inventory.getProduct().getId(),
                inventory.getStore().getId()
        );
        return existingInventory == null; // true if no record exists
    }

    /**
     * Validate if a product exists by its name.
     * @param product Product object to validate
     * @return false if product with same name exists, true otherwise
     */
    public boolean validateProduct(Product product) {
        Product existingProduct = productRepository.findByName(product.getName());
        return existingProduct == null; // true if no product exists with this name
    }

    /**
     * Validate if a product exists by its ID.
     * @param id Product ID
     * @return true if product exists, false if not
     */
    public boolean validateProductId(long id) {
        Optional<Product> product = productRepository.findById(id);
        return product != null;
    }

    /**
     * Get the inventory record for a product and store.
     * @param inventory Inventory object with product and store
     * @return the found Inventory record, or null if not found
     */
    public Inventory getInventoryId(Inventory inventory) {
        return inventoryRepository.findByProductIdAndStoreId(
                inventory.getProduct().getId(),
                inventory.getStore().getId()
        );
    }
}