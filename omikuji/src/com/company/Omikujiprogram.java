package com.company;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Omikujiprogram {

    @SuppressWarnings("null")
    public void omikujii() throws Exception {
        //1. 생일 입력 받기 誕生日入力を求める
        System.out.println("お誕生日はいつですか？(YYYYMMDD 形式)　");
        BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
        String birthday = br2.readLine();

        //2. 오늘 날짜 가져오기 今日の日付
        LocalDate today = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String todayString = today.format(dateTimeFormatter);

        //3. 포함한 결과 파일 結果ファイル（＋誕生日）
        File result = new File("omikuji/result.csv");
        try {
            FileReader reader = new FileReader(result);
            BufferedReader br3 = new BufferedReader(reader);
            String date;

            while ((date = br3.readLine()) != null) {
                String[] values2 = date.split(",");
                String resbirthday = values2[0];
                String restoday = values2[1];

                if (resbirthday.equals(birthday) && restoday.equals(todayString)) {
                    String resunsei = values2[2];
                    String resnegaigoto = values2[3];
                    String resakinai = values2[4];
                    String resgakumon = values2[5];
                    StringBuilder sb = new StringBuilder();
                    sb.append("今日の運勢は");
                    sb.append(resunsei);
                    sb.append("です。");
                    sb.append("\n");
                    sb.append("願い事：");
                    sb.append(resnegaigoto);
                    sb.append("\n");
                    sb.append("商い：");
                    sb.append(resakinai);
                    sb.append("\n");
                    sb.append("学問：");
                    sb.append(resgakumon);
                    System.out.println(sb.toString());
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //4. 파일 읽어와서 unsei에 넣어주기 ファイルを読み込んでUnseiに入れる
        //파일 읽어오기 위한 path, line, BufferedReader
        //전에 받아온 결과가 비어있을 경우 「結果がnullだ」の場合
        List<Unsei> omikuji = new ArrayList<>(); //부모클래스 Unsei로 생성한 객체 어레이리스트
        Unsei unsei = null;

        try {
            String path = "omikuji/omkj.csv";
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(path));

            String[] values; //1줄씩 받아서 저장할 어레이
            br.readLine();
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

                unsei.setUnsei();
                unsei.setUnsei();
                unsei.setNegaigoto(values[1]);
                unsei.setAkinai(values[2]);
                unsei.setGakumon(values[3]);
                omikuji.add(unsei);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //랜덤으로 출력하기
        int rannum = new Random().nextInt(omikuji.size());
        unsei = omikuji.get(rannum);

        System.out.println(unsei.disp());

        //csv 파일에 입력
        try {
            FileWriter fw = new FileWriter(result, true);
            String ls = System.lineSeparator();
            StringBuilder sb = new StringBuilder();
            sb.append(birthday);
            sb.append(",");
            sb.append(todayString);
            sb.append(",");
            sb.append(unsei.unsei);
            sb.append(",");
            sb.append(unsei.negaigoto);
            sb.append(",");
            sb.append(unsei.akinai);
            sb.append(",");
            sb.append(unsei.gakumon);
            sb.append(ls);

            fw.write(sb.toString());
            fw.flush();

            if(fw != null){
                fw.close();
            }

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws Exception {
        Omikujiprogram omikujiprogram = new Omikujiprogram();
        omikujiprogram.omikujii();
    }
}

