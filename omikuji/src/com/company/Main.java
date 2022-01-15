package com.company;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
	    ArrayList<String> omikuji = new ArrayList<>(Arrays.asList("大吉", "中吉", "小吉", "末吉", "吉", "凶"));
        int unsei = (int)(Math.random() * omikuji.size());
        String result = new StringBuilder()
                .append("今日の運勢は")
                .append(omikuji.get(unsei))
                .append("です。").toString();
        System.out.println(result);
    }
}
