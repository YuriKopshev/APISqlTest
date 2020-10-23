package ru.netology.data;

import com.mysql.cj.x.protobuf.MysqlxExpr;
import io.restassured.specification.RequestSpecification;
import lombok.Value;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static io.restassured.RestAssured.given;

public class DataHelper {


    private DataHelper() {
    }

    @Value
    public static class AuthInfo {
        private String login;
        private String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    @Value
    public static class VerificationInfo {
        private String login;
        private String code;
    }

    public static VerificationInfo getVerificationInfoFor(AuthInfo authInfo, String code) {
        return new VerificationInfo(authInfo.getLogin(), code);
    }

    @Value
    public static class Transaction {
        private String from;
        private String to;
        private int amount;
    }

    public static Transaction getTransaction(String from, String to, int amount) {

        return new Transaction(from, to, amount);
    }

    public static String getVerificationCode () throws SQLException {
        Object code = null;
        val codeSQL = "SELECT code FROM auth_codes WHERE created = (SELECT max(created) FROM auth_codes);";
        val runner = new QueryRunner();

        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );
        ) {
            code = runner.query(conn, codeSQL, new ScalarHandler<>());
    }
        return (String) code;
    }

    public static String getToken(RequestSpecification requestSpec) throws SQLException {
        String token = given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(DataHelper.getVerificationInfoFor(DataHelper.getAuthInfo(), DataHelper.getVerificationCode())) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/auth/verification") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200) // код 200 OK
                .extract()
                .path("token");
        return token;
    }


    public static int getFirstBalanceCard(RequestSpecification requestSpec, String token) {
        int firstBalance;
        Card[] cards =
                given() // "дано"
                        .spec(requestSpec) // указываем, какую спецификацию используем
                        .header("Authorization", "Bearer " + token)
                        .when() // "когда"
                        .get("/api/cards") // на какой путь, относительно BaseUri отправляем запрос
                        .then() // "тогда ожидаем"
                        .statusCode(200) // код 200 OK
                        .extract()
                        .as(Card[].class);
        return firstBalance = Integer.parseInt(cards[0].getBalance());
    }

    public static int getSecondBalanceCard(RequestSpecification requestSpec, String token) {
        int secondBalance;
        Card[] cards =
                given() // "дано"
                        .spec(requestSpec) // указываем, какую спецификацию используем
                        .header("Authorization", "Bearer " + token)
                        .when() // "когда"
                        .get("/api/cards") // на какой путь, относительно BaseUri отправляем запрос
                        .then() // "тогда ожидаем"
                        .statusCode(200) // код 200 OK
                        .extract()
                        .as(Card[].class);
        return secondBalance = Integer.parseInt(cards[1].getBalance());
    }

    public static void makeTransferFromSecondToFirst(RequestSpecification requestSpec, String token, int sum) {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(DataHelper.getTransaction("5559 0000 0000 0002", "5559 0000 0000 0001", sum))
                .when() // "когда"
                .post("/api/transfer")
                .then() // "тогда ожидаем"
                .statusCode(200);
    }
}


