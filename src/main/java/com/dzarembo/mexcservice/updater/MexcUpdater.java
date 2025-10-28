package com.dzarembo.mexcservice.updater;

import com.dzarembo.mexcservice.cache.FundingCache;
import com.dzarembo.mexcservice.client.MexcApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MexcUpdater {
    private final FundingCache cache;
    private final MexcApiClient apiClient;

    @Scheduled(fixedRate = 1 * 60 * 1000) // обновление каждые 1 минут
    public void updateFundingRates() {
        log.info("Updating Mexc funding cache...");
        cache.putAll(apiClient.fetchFundingRates());
        log.info("Mexc funding cache updated: {} entries", cache.getAll().size());
    }
}
