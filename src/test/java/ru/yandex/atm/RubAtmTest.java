package ru.yandex.atm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.atm.exceptions.DepositException;
import ru.yandex.atm.exceptions.WithdrawalException;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RubAtmTest {
    private static final int ZERO_AMOUNT = 0;
    private static final int AMOUNT_NOT_MULT_OF_MIN_DENOMINATION = Banknotes.minDenomination() / 2;
    private final Map<Banknotes, Integer> BANKNOTES_FOR_DEPOSIT = Map.of(
            Banknotes.FIVE_THOUSAND, 2,
            Banknotes.THOUSAND, 1,
            Banknotes.FIVE_HUNDRED, 1,
            Banknotes.HUNDRED, 1,
            Banknotes.FIFTY, 1
    );
    private Atm atm;        // Given

    @BeforeEach
    public void init() {
        atm = new RubAtm();
    }

    @Test
    public void testDepositBanknotesWithZeroAmountThrowsDepositException() {
        final Map<Banknotes, Integer> zeroAmountOfThousandBanknotes = Map.of(Banknotes.THOUSAND, ZERO_AMOUNT);
        assertThrows(DepositException.class, () -> atm.deposit(zeroAmountOfThousandBanknotes));
    }

    @Test
    public void testDepositSameAmountOfBanknotesTwiceReflectsDoublingInDepositInfo() {
        // Given
        Banknotes banknote = Banknotes.FIVE_THOUSAND;
        int amount = 10;
        Map<Banknotes, Integer> banknotesToAmounts = Map.of(banknote, amount);
        String expectedDepositInfo = "atm deposit: " + Map.of(banknote, amount * 2);
        // When
        atm.deposit(banknotesToAmounts);
        atm.deposit(banknotesToAmounts);
        // Then
        assertEquals(expectedDepositInfo, atm.getDepositInfo());
    }

    @Test
    public void testWithdrawalZeroAmountThrowsWithdrawalException() {
        assertThrows(WithdrawalException.class, () -> atm.withdrawal(ZERO_AMOUNT));
    }

    @Test
    public void testWithdrawalAmountNotMultipleOfMinDenominationThrowsWithdrawalException() {
        assertThrows(WithdrawalException.class, () -> atm.withdrawal(AMOUNT_NOT_MULT_OF_MIN_DENOMINATION));
    }

    @Test
    public void testWithdrawalAmountExceedsDepositThrowsWithdrawalException() {
        // Given
        atm.deposit(BANKNOTES_FOR_DEPOSIT);
        int amountOfDeposit = BANKNOTES_FOR_DEPOSIT.entrySet().stream()
                .mapToInt(entry -> entry.getKey().denomination() * entry.getValue())
                .sum();
        int amountExceedsDeposit = amountOfDeposit + Banknotes.minDenomination();
        // When, then
        assertThrows(WithdrawalException.class, () -> atm.withdrawal(amountExceedsDeposit));
    }

    @Test
    public void testWithdrawalOutputsExpectedBanknotes() {
        // Given
        atm.deposit(BANKNOTES_FOR_DEPOSIT);
        List<Banknotes> banknotesExpected = List.of(Banknotes.FIVE_THOUSAND, Banknotes.FIVE_THOUSAND,
                Banknotes.THOUSAND,
                Banknotes.FIVE_HUNDRED,
                Banknotes.HUNDRED,
                Banknotes.FIFTY
        );
        int withdrawalAmount = banknotesExpected.stream()
                .mapToInt(Banknotes::denomination)
                .sum();
        // When
        List<Banknotes> banknotesActual = atm.withdrawal(withdrawalAmount);
        // Then
        assertEquals(banknotesExpected.size(), banknotesActual.size());
        assertTrue(banknotesExpected.containsAll(banknotesActual));
        assertTrue(banknotesActual.containsAll(banknotesExpected));
    }

    @Test
    public void testGetDepositInfoShowZerosIfEntireDepositIsWithdrawn() {
        // Given
        atm.deposit(BANKNOTES_FOR_DEPOSIT);
        int amountOfDeposit = BANKNOTES_FOR_DEPOSIT.entrySet().stream()
                .mapToInt(entry -> entry.getKey().denomination() * entry.getValue())
                .sum();
        String expectedDepositInfo = "atm deposit: " + new EnumMap<>(
                Map.of(
                        Banknotes.FIVE_THOUSAND, ZERO_AMOUNT,
                        Banknotes.THOUSAND, ZERO_AMOUNT,
                        Banknotes.FIVE_HUNDRED, ZERO_AMOUNT,
                        Banknotes.HUNDRED, ZERO_AMOUNT,
                        Banknotes.FIFTY, ZERO_AMOUNT
                )
        );
        // When
        atm.withdrawal(amountOfDeposit);
        String actualDepositInfo = atm.getDepositInfo();
        // Then
        assertEquals(expectedDepositInfo, actualDepositInfo);
    }
}