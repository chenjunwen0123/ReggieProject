package com.example.reggie;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.ResourceBundle;

public class MyTest {
    @Test
    public void testSMS(){
        int n = 11;
        int x = n%3;
        int y = (int)Math.pow(10,2) + n/3;

        System.out.println((int)String.valueOf(y).charAt(x-1));
    }
}
