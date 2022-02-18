package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

public class checkBday {
    public static String checkBirthday() throws IOException {
        String bday;
        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            bday = br.readLine();

            SimpleDateFormat dateFP;


            if (bday.length() != 8) {
                System.out.print("yyyyMMddの8個の数字でお願いします : ");
                continue;
            }
            try {
                dateFP = new SimpleDateFormat("yyyyMMdd");
                dateFP.setLenient(false);
                dateFP.parse(bday);
                break;
            } catch (Exception e) {
                System.out.println("入力された誕生日の日付形式が正しくありません。");
                System.out.println("yyyyMMddの形式でお願いします。");
            }
            break;
        }
        return bday;
    }
}
