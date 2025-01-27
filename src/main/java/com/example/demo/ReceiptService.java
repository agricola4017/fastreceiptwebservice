package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ReceiptService {

    private final ConcurrentHashMap<Integer, Points> idToReceiptMap;
    private final AtomicInteger count;

    @Autowired
    public ReceiptService() {
        this.idToReceiptMap = new ConcurrentHashMap<>();
        this.count = new AtomicInteger(0);
    }

    public ID saveReceipt(Receipt receipt) {
        int id = count.getAndIncrement();
        idToReceiptMap.put(id, new Points(calculateReceipt(receipt)));
        return new ID(id);
    }

    public int calculateReceipt(Receipt receipt) {
        int sum = 0;

        // Retailer name points
        String retailer = receipt.getRetailer();
        sum += retailer.chars().filter(Character::isLetterOrDigit).count();

        // Total points
        try {
            float total = Float.parseFloat(receipt.getTotal());
            if (total == (int)total) {
                sum += 75;
            } else if (Math.abs(total % 0.25) < 0.001) { // Use small epsilon for float comparison
                sum += 25;
            }
        } catch (NumberFormatException e) {
            // Handle invalid total gracefully
        }

        // Items points
        Item[] items = receipt.getItems();
        sum += 5 * (items.length / 2);

        // Description points 
        for (Item item : items) {
            String desc = item.getShortDescription().trim();
            if (desc.length() % 3 == 0) {
                try {
                    double price = Double.parseDouble(item.getPrice());
                    sum += (int)Math.ceil(price * 0.2);
                } catch (NumberFormatException e) {
                    // Handle invalid price gracefully
                }
            }
        }

        // Purchase date points 
        try {
            LocalDate purchaseDate = LocalDate.parse(receipt.getPurchaseDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (purchaseDate.getDayOfMonth() % 2 == 1) {
                sum += 6;
            }
        } catch (DateTimeParseException e) {
            // Handle invalid date gracefully
        }

        // Time points - using LocalTime
        try {
            LocalTime purchaseTime = LocalTime.parse(receipt.getPurchaseTime(), DateTimeFormatter.ofPattern("HH:mm"));
            if (purchaseTime.getHour() >= 14 && purchaseTime.getHour() <= 16) {
                sum += 10;
            }
        } catch (DateTimeParseException e) {
            // Handle invalid time gracefully
        }

        return sum;
    }

    public Points getPoints(String id) {
        try {
            return idToReceiptMap.get(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
