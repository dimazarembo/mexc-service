package com.dzarembo.mexcservice.controller;

import com.dzarembo.mexcservice.cache.FundingCache;
import com.dzarembo.mexcservice.model.FundingRate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/mexc")
@RequiredArgsConstructor
public class MexcController {
    private final FundingCache cache;

    @GetMapping("/funding")
    public Collection<FundingRate> getFundingRates() {
        return cache.getAll();
    }
}
