package com.dzarembo.mexcservice.client;

import com.dzarembo.mexcservice.model.FundingRate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class MexcApiClient {

    private final WebClient webClient = WebClient.create("https://contract.mexc.com");

    public Collection<FundingRate> fetchFundingRates() {
        try {
            MexcResponse response = webClient.get()
                    .uri("/api/v1/contract/funding_rate")
                    .retrieve()
                    .bodyToMono(MexcResponse.class)
                    .block();

            if (response == null || !response.isSuccess() || response.getData() == null) {
                log.warn("Empty response from MEXC");
                return List.of();
            }

            return response.getData().stream()
                    // 1️⃣ оставляем только пары с USDT
                    .filter(item -> item.getSymbol() != null && item.getSymbol().contains("USDT"))
                    // 2️⃣ исключаем USD-пары без T
                    .filter(item -> !item.getSymbol().contains("USD_") || item.getSymbol().contains("USDT"))
                    // 3️⃣ мапим в FundingRate
                    .map(this::mapToFundingRate)
                    .filter(Objects::nonNull)
                    .toList();

        } catch (Exception e) {
            log.error("Failed to fetch funding rates from MEXC", e);
            return List.of();
        }
    }

    private FundingRate mapToFundingRate(MexcResponse.Item item) {
        try {
            double rate = item.getFundingRate();
            int intervalHours = item.getCollectCycle();
            long nextFundingTimeUtc = item.getNextSettleTime();
            String normalizedSymbol = normalizeSymbol(item.getSymbol());

            log.debug("MEXC: {} rate={}, nextFundingTime(UTC)={}, interval={}h",
                    normalizedSymbol,
                    rate,
                    Instant.ofEpochMilli(nextFundingTimeUtc),
                    intervalHours
            );

            return new FundingRate(
                    normalizedSymbol,
                    rate,
                    nextFundingTimeUtc,
                    intervalHours
            );
        } catch (Exception e) {
            log.error("Failed to parse MEXC item: {}", item, e);
            return null;
        }
    }

    private String normalizeSymbol(String symbol) {
        // "BTC_USDT" → "BTCUSDT"
        return symbol.replace("_", "");
    }

    @Data
    public static class MexcResponse {
        private boolean success;
        private int code;
        private List<Item> data;

        public boolean isSuccess() {
            return success && code == 0;
        }

        @Data
        public static class Item {
            private String symbol;          // e.g. "BTC_USDT"
            private double fundingRate;     // e.g. 0.000007
            private double maxFundingRate;
            private double minFundingRate;
            private int collectCycle;       // e.g. 8
            private long nextSettleTime;    // e.g. 1761667200000
            private long timestamp;
        }
    }
}
