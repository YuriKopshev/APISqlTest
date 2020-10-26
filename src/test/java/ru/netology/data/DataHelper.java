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


}


