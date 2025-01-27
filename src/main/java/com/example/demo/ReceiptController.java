package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "receipts")
public class ReceiptController {

    private final ReceiptService receiptService;
    @Autowired
    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping
    public String abc() {
        return "abc";
    }

    @PostMapping(path = "process")
    public ID postReceipt(@RequestBody Receipt receipt) {
        return receiptService.saveReceipt(receipt);
    }

    @GetMapping(path = "{id}/points")
    public Points getPoints(@PathVariable("id") String id) {
        return receiptService.getPoints(id);
    }
}