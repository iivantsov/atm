package ru.yandex.atm;

import java.util.Arrays;

public enum Banknotes {
    FIVE_THOUSAND(5000),
    THOUSAND(1000),
    FIVE_HUNDRED(500),
    HUNDRED(100),
    FIFTY(50);

    Banknotes(int denomination) {
        this.denomination = denomination;
    }

    public int denomination() {
        return denomination;
    }

    public static int minDenomination() {
        return Arrays.stream(Banknotes.values())
                .mapToInt(Banknotes::denomination)
                .min()
                .orElseThrow();
    }

    private final int denomination;
}