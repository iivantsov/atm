package ru.yandex.atm;

import ru.yandex.atm.exceptions.DepositException;
import ru.yandex.atm.exceptions.WithdrawalException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RubAtm implements Atm {
    private final Logger logger;
    private final Map<Banknotes, Integer> deposit;
    private final int minBanknoteDenomination;

    public RubAtm() {
        logger = Logger.getLogger(RubAtm.class.getSimpleName());
        logger.setLevel(Level.OFF);
        deposit = new EnumMap<>(Banknotes.class);
        minBanknoteDenomination = Banknotes.minDenomination();
    }

    @Override
    public String getDepositInfo() {
        String deposit = "atm deposit: " + this.deposit;
        logger.info(deposit);
        return deposit;
    }

    @Override
    public void deposit(Map<Banknotes, Integer> banknotesToAmounts) {
        for (Map.Entry<Banknotes, Integer> entry : banknotesToAmounts.entrySet()) {
            Banknotes banknote = entry.getKey();
            int amount = entry.getValue();
            if (amount <= 0) {
                throw new DepositException(Banknotes.class, amount);
            }
            this.deposit.merge(banknote, amount, Integer::sum);
        }
    }

    @Override
    public List<Banknotes> withdrawal(int amount) {
        logger.info("withdrawal request: " + amount);
        if (amount <= 0 || (amount % minBanknoteDenomination) != 0) {
            throw new WithdrawalException();
        }
        Map<Banknotes, Integer> requiredDeposit = new EnumMap<>(Banknotes.class);
        for (Banknotes banknote : Banknotes.values()) {
            logger.fine("denomination: " + banknote.denomination());
            int count = amount / banknote.denomination();
            if (count != 0) {
                requiredDeposit.put(banknote, count);
                amount -= count * banknote.denomination();
            }
        }
        logger.fine("required: " + requiredDeposit);
        List<Banknotes> banknotes = new ArrayList<>();
        for (Map.Entry<Banknotes, Integer> entry : requiredDeposit.entrySet()) {
            Banknotes banknote = entry.getKey();
            int requiredAmount = entry.getValue();
            Integer depositAmount = deposit.get(banknote);
            if (depositAmount == null || depositAmount - requiredAmount < 0) {
                throw new WithdrawalException(banknote);
            }
            int remainingAmount = depositAmount - requiredAmount;
            deposit.put(banknote, remainingAmount);
            banknotes.addAll(Collections.nCopies(requiredAmount, banknote));
        }
        logger.info("banknotes: " + banknotes);
        return banknotes;
    }
}