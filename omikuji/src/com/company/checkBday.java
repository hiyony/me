package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

public class checkBday {
    public static Boolean checkBirthday(String birthday) {

        while (true) {
            SimpleDateFormat dateFP;

            try {
                dateFP = new SimpleDateFormat("yyyyMMdd");
                dateFP.setLenient(false);
                dateFP.parse(birthday);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }
}