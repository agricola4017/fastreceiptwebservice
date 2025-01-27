package com.example.demo;

import com.example.demo.exception.ReceiptProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "receipts")
public class ReceiptController {

    private final ReceiptService receiptService;
    
    @Autowired
    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    // Stores receipt from user 
    // @RequestBody Receipt receipt - JSON of receipt
    // validates receipt using prompt 
    // returns ID of receipt
    @PostMapping(path = "process")
    public ResponseEntity<ID> postReceipt(@RequestBody Receipt receipt) {
        validateReceipt(receipt);
        ID id = receiptService.saveReceipt(receipt);
        return ResponseEntity.ok(id);
    }

    // Gets the points for a given receipt ID
    // @PathVariable("id") String id - ID given by post response assoc. with receipt
    // returns Point object
    @GetMapping(path = "{id}/points")
    public ResponseEntity<Points> getPoints(@PathVariable("id") String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new ReceiptProcessingException("Receipt ID cannot be null or empty");
        }

        Points points = receiptService.getPoints(id);
        if (points == null) {
            throw new ReceiptProcessingException("Receipt not found with ID: " + id);
        }

        return ResponseEntity.ok(points);
    }

    // validates simple string input for nulls and empties 
    // needs additional implementation for date-time and price validation 
    private void validateReceipt(Receipt receipt) {
        if (receipt == null) {
            throw new ReceiptProcessingException("Receipt cannot be null");
        }
        if (receipt.getRetailer() == null || receipt.getRetailer().trim().isEmpty()) {
            throw new ReceiptProcessingException("Retailer name is required");
        }
        if (receipt.getPurchaseDate() == null || receipt.getPurchaseDate().trim().isEmpty()) {
            throw new ReceiptProcessingException("Purchase date is required");

            //also needs more complex date error processing
        }
        if (receipt.getPurchaseTime() == null || receipt.getPurchaseTime().trim().isEmpty()) {
            throw new ReceiptProcessingException("Purchase time is required");
        }
        if (receipt.getTotal() == null || receipt.getTotal().trim().isEmpty()) {
            throw new ReceiptProcessingException("Total amount is required");
        }
        if (receipt.getItems() == null || receipt.getItems().length == 0) {
            throw new ReceiptProcessingException("At least one item is required");
        }
        
        // Validate each item
        for (Item item : receipt.getItems()) {
            if (item.getShortDescription() == null || item.getShortDescription().trim().isEmpty()) {
                throw new ReceiptProcessingException("Item description cannot be empty");
            }
            if (item.getPrice() == null || item.getPrice().trim().isEmpty()) {
                throw new ReceiptProcessingException("Item price cannot be empty");
            }
            try {
                Double.parseDouble(item.getPrice());
            } catch (NumberFormatException e) {
                throw new ReceiptProcessingException("Invalid price format for item: " + item.getShortDescription());
            }
        }
        
        // Validate total amount format
        try {
            Double.parseDouble(receipt.getTotal());
        } catch (NumberFormatException e) {
            throw new ReceiptProcessingException("Invalid total amount format");
        }
    }
}