package ru.netology.test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;

import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class APITest {
    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    int begBalance1;
    int begBalance2;
    int sum = 5000;

    @Test
    void shouldMakeTransfer() throws SQLException {
        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(DataHelper.getAuthInfo()) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/auth") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
        String token = DataHelper.getToken(requestSpec);
        begBalance1 = DataHelper.getFirstBalanceCard(requestSpec, token);
        begBalance2 = DataHelper.getSecondBalanceCard(requestSpec, token);
        DataHelper.makeTransferFromSecondToFirst(requestSpec, token, sum);
        int endBalance1 = DataHelper.getFirstBalanceCard(requestSpec, token);
        int endBalance2 = DataHelper.getSecondBalanceCard(requestSpec, token);
        assertEquals(begBalance1 - sum, endBalance1);
        assertEquals(begBalance2 + sum, endBalance2);
    }
}
