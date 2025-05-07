package ru.yandex.atm.exceptions;

import ru.yandex.atm.Banknotes;

import java.util.Arrays;

public class WithdrawalException extends RuntimeException {
    private static final String PREAMBLE = "requested amount cannot be withdrawn, ";

    public WithdrawalException() {
        super(PREAMBLE + "only the following denominations are available: " + Arrays.toString(Banknotes.values()));
    }

    public WithdrawalException(Banknotes banknote) {
        super(PREAMBLE + "there are not enough banknotes of the denomination: " + banknote.denomination());
    }
}