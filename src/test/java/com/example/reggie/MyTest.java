package com.example.reggie;

import org.junit.jupiter.api.Test;

import java.util.ResourceBundle;

public class MyTest {
    @Test
    public void testSMS(){
        ResourceBundle resource = ResourceBundle.getBundle("SMSProperties");
        System.out.println(resource.getString("AccessKey.Id"));
    }
}
