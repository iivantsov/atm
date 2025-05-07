package ru.yandex.atm.exceptions;

import ru.yandex.atm.Banknotes;

public class DepositException extends RuntimeException {

    public DepositException(Class<? extends Banknotes> banknoteClass, int amount) {
        super("Invalid amount of " + banknoteClass.getSimpleName() + "banknote to deposit: " + amount);
    }
}