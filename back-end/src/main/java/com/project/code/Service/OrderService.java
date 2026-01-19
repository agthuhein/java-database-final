package com.project.code.Service;

import com.project.code.Model.*;
import com.project.code.Repo.*;
import jakarta.persistence.criteria.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;


    public void saveOrder(PlaceOrderRequestDTO placeOrderRequestDTO) {
        //1. Retrieve or create the customer
        Customer customer = customerRepository.findByEmail(placeOrderRequestDTO.getCustomerEmail());
        if(customer == null) {
            customer = new Customer();
            customer.setName(placeOrderRequestDTO.getCustomerName());
            customer.setEmail(placeOrderRequestDTO.getCustomerEmail());
            customer = customerRepository.save(customer);
        }

        // 2. Retrieve the store
        Optional<Store> storeOptional = storeRepository.findById(placeOrderRequestDTO.getStoreId());
        if(storeOptional.isEmpty()){
            throw new RuntimeException("Store not found with ID: " + placeOrderRequestDTO.getStoreId());
        }

        Store store = storeOptional.get();

        // 3. Create OrderDetails
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCustomer(customer);
        orderDetails.setStore(store);
        orderDetails.setTotalPrice(placeOrderRequestDTO.getTotalPrice());
        orderDetails.setDate(LocalDateTime.now());
        orderDetails = orderDetailsRepository.save(orderDetails);

        //4. Process each product in the order
        for (PurchaseProductDTO productRequest : placeOrderRequestDTO.getPurchaseProduct()) {

            // Retrieve inventory for product and store
            Inventory inventory = inventoryRepository.findByProductIdAndStoreId(
                    productRequest.getId(), store.getId()
            );

            if (inventory == null) {
                throw new RuntimeException("Product with ID " + productRequest.getId() +
                        " is not available in store " + store.getId());
            }

            // Update stock level
            int newStock = inventory.getStockLevel() - productRequest.getQuantity();
            if (newStock < 0) {
                throw new RuntimeException("Insufficient stock for product ID " + productRequest.getId());
            }
            inventory.setStockLevel(newStock);
            inventoryRepository.save(inventory);

            // Create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(orderDetails);
            orderItem.setProduct(inventory.getProduct());
            orderItem.setQuantity(productRequest.getQuantity());
            orderItem.setPrice(productRequest.getPrice()); // price per unit
            orderItemRepository.save(orderItem);
        }
    }


}
