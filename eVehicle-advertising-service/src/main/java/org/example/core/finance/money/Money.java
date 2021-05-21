package org.example.core.finance.money;



import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.core.finance.bank.Bank;

import java.util.Currency;
import java.util.Objects;

import static java.lang.String.format;

@ToString
@EqualsAndHashCode
public class Money {

    private final double amount;
    private final Currency currency;

    public Money(double amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public Money add(Money moneyToAdd, Bank bank) {
        Objects.requireNonNull(moneyToAdd, "MoneyToAdd is a mandatory parameter");
        Money convertedMoney = moneyToAdd.to(this.currency, bank);
        return new Money(this.amount + convertedMoney.getAmount(), this.currency);
    }

    public Money subtract(Money moneyToSubtract, Bank bank) {
        Objects.requireNonNull(moneyToSubtract, "MoneyToSubtract is a mandatory parameter");
        Money convertedMoney = moneyToSubtract.to(this.currency, bank);
        return new Money(this.amount - convertedMoney.getAmount(), this.currency);
    }

    public Money multiply(double multiplier) {
        return new Money(this.amount * multiplier, this.currency);
    }

    public Money divide(double divider) {
        return new Money(this.amount / divider, this.currency);
    }

    public Money to(String toCurrency, Bank bank) {
        return to(Currency.getInstance(toCurrency), bank);
    }

    public Money to(Currency toCurrency, Bank bank) {
        Objects.requireNonNull(bank, "Bank is a mandatory parameter");
        Objects.requireNonNull(toCurrency, "Currency is a mandatory parameter");
        Double exchangeRate = bank.getExchangeRate(this.getCurrency(), toCurrency)
            .orElseThrow(() -> handleMissingExchangeRate(this.currency, toCurrency));
        return new Money(this.amount * exchangeRate, toCurrency);
    }

    public double getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }


    private UnsupportedOperationException handleMissingExchangeRate(Currency from, Currency to) {
        return new UnsupportedOperationException(format("Conversion between %s and %s is not supported", from, to));
    }

    @Override
    public String toString() {
        return (int)this.amount + " " + this.currency.toString();
    }
}
