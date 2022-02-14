package com.company;

import java.text.SimpleDateFormat;

public class Birthday {
    public Birthday(String bday) {
        SimpleDateFormat dateFP;
        try {
            dateFP = new SimpleDateFormat("yyyyMMdd");
            dateFP.setLenient(false);
            dateFP.parse(bday);
        } catch (Exception e) {
            System.out.println("入力された誕生日の日付形式が正しくありません。");
            System.out.println("yyyyMMddの形式でお願いします。");
        }
    }
}
