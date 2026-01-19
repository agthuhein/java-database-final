package com.project.code.Controller;


import com.project.code.Model.Review;
import com.project.code.Model.Customer;
import com.project.code.Repo.ReviewRepository;
import com.project.code.Repo.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // 1️⃣ Get reviews for a specific store and product
    @GetMapping("/{storeId}/{productId}")
    public Map<String, Object> getReviews(@PathVariable("storeId") Long storeId,
                                          @PathVariable("productId") Long productId) {
        Map<String, Object> response = new HashMap<>();

        // Fetch reviews from MongoDB
        List<Review> reviews = reviewRepository.findByStoreIdAndProductId(storeId, productId);

        // Prepare response list with comment, rating, and customer name
        List<Map<String, Object>> reviewList = new ArrayList<>();

        for (Review review : reviews) {
            Map<String, Object> reviewData = new HashMap<>();
            reviewData.put("comment", review.getComment());
            reviewData.put("rating", review.getRating());

            // Get customer name
            Optional<Customer> customer = customerRepository.findById(review.getCustomerId());
            if (customer != null) {
                reviewData.put("customerName", customer.get().getName());
            } else {
                reviewData.put("customerName", "Unknown");
            }

            reviewList.add(reviewData);
        }

        response.put("reviews", reviewList);
        return response;
    }
}