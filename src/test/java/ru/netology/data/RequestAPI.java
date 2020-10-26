package ru.netology.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import lombok.Value;

import java.sql.DriverManager;
import java.sql.SQLException;

import static io.restassured.RestAssured.given;

public class RequestAPI {

    public static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static void getRequest() {
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(DataHelper.getAuthInfo()) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/auth") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }

    public static String getVerificationCode () throws SQLException {
        Object code = null;
        val codeSQL = "SELECT code FROM auth_codes WHERE created = (SELECT max(created) FROM auth_codes);";
        val runner = new QueryRunner();

        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://192.168.99.100:3306/app", "app", "pass"
                );
        ) {
            code = runner.query(conn, codeSQL, new ScalarHandler<>());
        }
        return (String) code;
    }

    public static String getToken(RequestSpecification requestSpec) throws SQLException {
        String token = given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(DataHelper.getVerificationInfoFor(DataHelper.getAuthInfo(),getVerificationCode())) // передаём в теле объект, который будет преобразован в JSON
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

