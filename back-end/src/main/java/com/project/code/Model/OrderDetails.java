package com.project.code.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_details")
public class OrderDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonManagedReference
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "store_id")
    @JsonManagedReference
    private Store store;

    private Double totalPrice;

    private LocalDateTime date;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderItem> orderItems = new ArrayList<>();

    // No-argument constructor required by JPA
    public OrderDetails() {
    }

    // Parameterized constructor
    public OrderDetails(Customer customer, Store store, Double totalPrice, LocalDateTime date) {
        this.customer = customer;
        this.store = store;
        this.totalPrice = totalPrice;
        this.date = date;
    }
}
