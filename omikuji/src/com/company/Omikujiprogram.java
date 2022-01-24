package com.company;

import com.sun.rmi.rmid.ExecPermission;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Omikujiprogram {

    public static void main(String[] args) throws Exception {
            //1. 생일 입력 받기 誕生日入力を求める
            System.out.println("お誕生日はいつですか？(YYYYMMDD 形式)　");
            BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
            String birthday = br2.readLine();

            //2. 오늘 날짜 가져오기 今日の日付
            LocalDate today = LocalDate.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String todayString = today.format(dateTimeFormatter);

            //3. 포함한 결과 파일 結果ファイル（＋誕生日）
            File result = new File("src/com/company/result.csv");
            try {
                FileReader reader = new FileReader(result);
                BufferedReader br3 = new BufferedReader(reader);
                String date;
                Unsei unsei = null;  //부모클래스 Unsei의 변수 생성 -> switch문에서 각 자식클래스에게 값을 설정해줌


                while ((date = br3.readLine()) != null) {
                    String[] values2 = date.split(",");
                    String resbirthday = values2[0];
                    String restoday = values2[1];

                    if (resbirthday.equals(birthday) && restoday.equals(todayString)) {
                        for (int i = 0; i < values2.length; i++) {
                            unsei.setUnsei(values2[2]);
                            unsei.setNegaigoto(values2[3]);
                            unsei.setAkinai(values2[4]);
                            unsei.setGakumon(values2[5]);
                        }
                    }
                }

                //4. 파일 읽어와서 unsei에 넣어주기 ファイルを読み込んでUnseiに入れる
                //파일 읽어오기 위한 path, line, BufferedReader
                //전에 받아온 결과가 비어있을 경우 「結果がnullだ」の場合
                if (unsei == null) {

                    String path = "src/com/company/omkj.csv";
                    String line = "";
                    BufferedReader br = new BufferedReader(new FileReader(path));

                    String[] values; //1줄씩 받아서 저장할 어레이
                    String values2 = null; //switch문에 넣어줄 값 변수
                    List<Unsei> omikuji = new ArrayList<>(); //부모클래스 Unsei로 생성한 객체 어레이리스트


                    //1줄씩 csv파일 읽어오기 １行ずつcsvファイルから読み込む
                    while ((line = br.readLine()) != null) {
                        values = line.split(",");

                        //switch문으로 운세 지정해서 넣어주기
                        switch (values[0]) {
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
                            default:
                        }

                        for (int i = 0; i < values.length; i++) {
                            unsei.setUnsei();
                            unsei.setNegaigoto(values[1]);
                            unsei.setAkinai(values[2]);
                            unsei.setGakumon(values[3]);
                            omikuji.add(unsei);
                        }

                        //랜덤으로 출력하기
                        int rannum = (int)(Math.random() * omikuji.size());
                        unsei = omikuji.get(rannum);

                        //csv 파일에 입력
                        FileWriter fw = new FileWriter(result, true);

                        StringBuilder sb = new StringBuilder();
                        sb.append(birthday);
                        sb.append(',');
                        sb.append(todayString);
                        sb.append(',');
                        sb.append(unsei.unsei);
                        sb.append(',');
                        sb.append(unsei.negaigoto);
                        sb.append(',');
                        sb.append(unsei.akinai);
                        sb.append(',');
                        sb.append(unsei.gakumon);
                        sb.append('\n');

                        fw.write(sb.toString());
                        fw.flush();
                    }
                    System.out.println(unsei.disp());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}