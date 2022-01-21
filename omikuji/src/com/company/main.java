package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class main {

    public static void main(String[] args) {
	    try {
            //생일 입력 받기 誕生日入力を求める
            System.out.println("お誕生日はいつですか？　");
            Scanner scan = new Scanner(System.in);
            String birthday = scan.next();

            //오늘 날짜 가져오기 今日の日付
            LocalDate today = LocalDate.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String todayString = today.format(dateTimeFormatter);

            //생일 받아와서 오늘 날짜와 비교 誕生日と今日の日付を比較
            char [] compare = new char[10];
            birthday.getChars(4, 8, compare, 4);
            char [] compare2 = new char[10];
            todayString.getChars(4, 8, compare2, 4);

            String c1 = new String(compare);
            String c2 = new String(compare2);

            //c1(받아온 생일) 과 c2(오늘 날짜) 가 같으면 같은 운세 내용 출력
            //c1(誕生日)と c2(今日の日付)　が同じであれば同じ運勢内容　出力
            if(c1.equals(c2)){

            }


            // 포함한 결과 파일 만들기 結果ファイルを作る（＋誕生日）


            //파일 읽어와서 unsei에 넣어주기 ファイルを読み込んでUnseiに入れる
            //파일 읽어오기 위한 path, line, BufferedReader
            String path = "src/com/company/omkj.csv";
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(path));
            Unsei unsei = null; //부모클래스 Unsei의 변수 생성 -> switch문에서 각 자식클래스에게 값을 설정해줌

            String [] values; //1줄씩 받아서 저장할 어레이
            String values2 = null; //switch문에 넣어줄 값 변수
            List<Unsei> omikuji = new ArrayList<>(); //부모클래스 Unsei로 생성한 객체 어레이리스트

            //1줄씩 csv파일 읽어오기 １行ずつcsvファイルから読み込む
            while((line = br.readLine()) != null){
                values = line.split(",");
                unsei = divideValues(values[0]); //switch문으로 case 별로 운세를 넣어주기 위함
                for(int i=0; i<values.length; i++) {
                    unsei.setUnsei(values[0]);
                    unsei.setNegaigoto(values[1]);
                    unsei.setAkinai(values[2]);
                    unsei.setGakumon(values[3]);
                    omikuji.add(unsei);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //String 변수를 받고 각 운세 값을 set
    public static Unsei divideValues(String value) {
        Unsei unsei = null;
        switch (value) {
            case "大吉":
                unsei = new Daikichi();
                break;
            case "中吉":
                unsei = new Cyuukichi();
                break;
            case "小吉":
                unsei = new Syoukichi();
                break;
            case "吉":
                unsei = new Kichi();
                break;
            case "末吉":
                unsei = new Sueyosi();
                break;
            case "凶":
                unsei = new Kyou();
                break;
        }
        return unsei;
    }
}
