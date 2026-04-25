package org.example.smartunipro.controller;

import org.example.smartunipro.entity.Attendance;
import org.example.smartunipro.entity.Material;
import org.example.smartunipro.service.TransactionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    public TransactionController(TransactionService transactionService) { this.transactionService = transactionService; }

    @PostMapping("/attendance")
    public Attendance markAttendance(@RequestBody Attendance attendance) {
        return transactionService.markAttendance(attendance);
    }

    @PostMapping("/material")
    public Material uploadMaterial(@RequestBody Material material) {
        return transactionService.saveMaterial(material);
    }
}