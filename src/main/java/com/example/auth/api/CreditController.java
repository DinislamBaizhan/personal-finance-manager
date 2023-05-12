package com.example.auth.api;

import com.example.auth.data.entity.Debt;
import com.example.auth.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vi/credit")
public class CreditController {

    private final CreditService creditService;

    @PostMapping
    public Debt save(@RequestBody Debt debt) {
        return creditService.save(debt);
    }

    @GetMapping
    public List<Debt> getAll() {
        return creditService.getAll();
    }

    @GetMapping("/{creditId}")
    public Debt getById(@PathVariable Long creditId) {
        return creditService.getById(creditId);
    }


    @GetMapping("/true")
    public List<Debt> getAllIsActiveTrue() {
        return creditService.getAllActive();
    }

    @GetMapping("/false")
    public List<Debt> getAllIsActiveFalse() {
        return creditService.getAllNotActive();
    }

//    @PostMapping("/{creditId}/repay")
//    public Debt repay(@RequestBody Expense expense, @PathVariable Long creditId) {
//
//    }
}