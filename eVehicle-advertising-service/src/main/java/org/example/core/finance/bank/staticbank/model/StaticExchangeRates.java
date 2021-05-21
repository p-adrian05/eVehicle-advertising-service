package org.example.core.finance.bank.staticbank.model;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

@Component
public class StaticExchangeRates {

    private final Map<CurrencyPair, Double> exchangeRatesMap;

    StaticExchangeRates(Map<CurrencyPair, Double> exchangeRatesMap) {
        this.exchangeRatesMap = exchangeRatesMap;
    }

    @Generated
    public Double get(Currency from, Currency to) {
        return exchangeRatesMap.get(CurrencyPair.of(from, to));
    }

    @Generated
    public static class Builder {

        private final Map<CurrencyPair, Double> exchangeRatesMap = new HashMap<>();

        public Builder addRate(String from, String to, double rate) {
            return addRate(Currency.getInstance(from), Currency.getInstance(to), rate);
        }

        public Builder addRate(Currency from, Currency to, double rate) {
            exchangeRatesMap.put(CurrencyPair.of(from, to), rate);
            return this;
        }

        public Builder addRate(String from, String to, double rate, double reverseRate) {
            return addRate(Currency.getInstance(from), Currency.getInstance(to), rate, reverseRate);
        }

        public Builder addRate(Currency from, Currency to, double rate, double reverseRate) {
            exchangeRatesMap.put(CurrencyPair.of(to, from), reverseRate);
            return addRate(from, to, rate);
        }

        public StaticExchangeRates build() {
            if (exchangeRatesMap.isEmpty()) {
                throw new IllegalArgumentException("No exchange rate has been added");
            }
            return new StaticExchangeRates(exchangeRatesMap);
        }

    }

    @EqualsAndHashCode
    @ToString
    private static class CurrencyPair {

        private final Currency from;
        private final Currency to;

        public static CurrencyPair of(Currency from, Currency to) {
            return new CurrencyPair(from, to);
        }

        private CurrencyPair(Currency from, Currency to) {
            this.from = from;
            this.to = to;
        }


    }

}
