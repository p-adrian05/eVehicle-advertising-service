package org.example.core.finance.money.comparator;



import org.example.core.finance.bank.Bank;
import org.example.core.finance.money.Money;

import java.util.Comparator;
import java.util.Objects;

public class MoneyComparator implements Comparator<Money> {

    private final Bank bank;

    public MoneyComparator(Bank bank) {
        this.bank = bank;
    }

    @Override
    public int compare(Money o1, Money o2) {
        Objects.requireNonNull(o1, "o1 parameter cannot be null");
        Objects.requireNonNull(o2, "o2 parameter cannot be null");
        return Double.compare(o1.getAmount(), o2.to(o1.getCurrency(), bank).getAmount());
    }
}
