package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReceiptService {

    private Map<String, Points> idToReceiptMap;
    int count;
    @Autowired
    public ReceiptService() {
        this.idToReceiptMap = new HashMap<>();
        count = 0;
    }

    public ID saveReceipt(Receipt receipt) {
        idToReceiptMap.put(Integer.toString(count),new Points(calculateReceipt(receipt)));
        count++;
        return new ID(count-1);
    }

    public int calculateReceipt(Receipt receipt) {
        int sum = 0;

        //alphanumeric
        for (int i = 0; i < receipt.getRetailer().length(); i++) {
            if (Character.isLetterOrDigit(receipt.getRetailer().charAt(i))) {
                sum+=1;
            }
        }

        //total
        float total = Float.parseFloat(receipt.getTotal());
        if (total == (int)total) {
            sum +=75;
        }
        else if (total % 0.25 == 0) {
            sum +=25;
        }

        //items
        sum  = sum + 5 * (receipt.getItems().length/2);

        //description
        for (Item item: receipt.getItems()) {
            if (item.getShortDescription().trim().length()%3 == 0) {
                sum += Math.ceil(Double.parseDouble(item.getPrice())*0.2);
            }
        }

        //llm
        sum+=0;

        //purchase date is odd
        if (Integer.parseInt(receipt.getPurchaseDate().substring(receipt.getPurchaseDate().length()-2))%2 == 1) {
            sum+=6;
        }

        //time of purchase between 2 and 4 pm
        int hour = Integer.parseInt(receipt.getPurchaseTime().substring(0,2));
        if (hour >= 14 && hour <= 16) {
            sum+=10;
        }

        return sum;
    }

    public Points getPoints(String id) {
        return idToReceiptMap.get(id);
    }
}
