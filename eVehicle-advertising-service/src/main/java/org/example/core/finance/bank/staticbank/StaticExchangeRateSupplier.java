package org.example.core.finance.bank.staticbank;


import org.example.core.finance.bank.staticbank.model.StaticExchangeRates;

import java.util.function.Supplier;

@FunctionalInterface
public interface StaticExchangeRateSupplier extends Supplier<StaticExchangeRates> {

}
