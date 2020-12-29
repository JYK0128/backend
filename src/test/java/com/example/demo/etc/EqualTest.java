package com.example.demo.etc;

import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EqualTest {

    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    class Example{
        @EqualsAndHashCode.Include
        int id;
        int test;
    }

    @Test
    public void equalTest(){
        Example o1 = new Example();
        o1.id = 1;
        o1.test = 1;

        Example o2 = new Example();
        o2.id = 1;
        o2.test = 2;

        assertTrue(o1.equals(o2));
    }
}
