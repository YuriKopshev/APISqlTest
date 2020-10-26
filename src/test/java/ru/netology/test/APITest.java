package ru.netology.test;


import org.junit.jupiter.api.Test;
import ru.netology.data.RequestAPI;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.RequestAPI.requestSpec;

public class APITest {
    int begBalance1;
    int begBalance2;
    int sum = 5000;

    @Test
    void shouldMakeTransfer() throws SQLException {
        RequestAPI.getRequest();
        String token = RequestAPI.getToken(requestSpec);
        begBalance1 = RequestAPI.getFirstBalanceCard(requestSpec,token);
        begBalance2 = RequestAPI.getSecondBalanceCard(requestSpec, token);
        RequestAPI.makeTransferFromSecondToFirst(requestSpec, token, sum);
        int endBalance1 = RequestAPI.getFirstBalanceCard(requestSpec, token);
        int endBalance2 = RequestAPI.getSecondBalanceCard(requestSpec, token);
        assertEquals(begBalance1 - sum, endBalance1);
        assertEquals(begBalance2 + sum, endBalance2);
    }
}
