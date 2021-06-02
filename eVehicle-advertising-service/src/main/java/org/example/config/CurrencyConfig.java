package org.example.config;

import org.example.core.finance.bank.Bank;
import org.example.core.finance.bank.staticbank.impl.StaticBank;
import org.example.core.finance.bank.staticbank.model.StaticExchangeRates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CurrencyConfig {

    @Bean
    public Bank bank(){
        return StaticBank.of(() -> new StaticExchangeRates.Builder()
            .addRate("HUF", "EUR", 0.0029, 346)
            .addRate("HUF", "USD", 0.0035, 283)
            .addRate("EUR", "USD", 1.22, 0.82)
            .build());
    }
}
