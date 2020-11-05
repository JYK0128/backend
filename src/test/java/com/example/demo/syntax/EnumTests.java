package com.example.demo.syntax;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class EnumTests {
    enum Expression {
        PLUS("+"), MINUS("-"), TIMES("*"), DIVIDE("/");

        private String expression;

        Expression(String expression) {
            this.expression = expression;
        }

        static Expression of(String expression) {
            return Arrays.stream(values())
                    .filter(v -> expression.equals(v.expression))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("%s는 사칙연산에 해당하지 않는 표현식입니다.", expression)));
        }

        static Expression get(int idx) {
            return values()[idx];
        }
    }

    @Test
    void enumTest(){
        System.out.println("ordinal() = " + Expression.DIVIDE.ordinal());
        System.out.println("valueOf(\"DIVIDE\") = " + Expression.valueOf("DIVIDE"));
        System.out.println("values()[0] = " + Expression.values()[0]);
        System.out.println("toString() = " + Expression.DIVIDE.toString());
    }
}
