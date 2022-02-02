package com.company;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Omikujiprogram {

    @SuppressWarnings("null")
    public void omikujii() throws Exception {

        //1. 생일 입력 받기 誕生日入力を求める
        //Birthday 変数にnullを設定して新しい変数を初期化します。
        //BufferedReaderはBufferを使って読むことを数行するfunctionで、 Scannerよりもっと効率的です。
        //InputStreamReaderは２byteを処理するStreamです。
        //System.inで入力させる値をInputStreamReaderで処理して、BufferedReaderで読みます。
        String birthday = null;
        BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            System.out.println("お誕生日はいつですか？(YYYYMMDD 形式)　");
            birthday = br2.readLine();

            //+) 잘못된 형식으로 입력 받았을 시 에러 메세지 他の形式で入力された場合のエラーメッセージ
            //YYYYMMDD形式ではない場合(8文字より多かったり少なかったりした場合)
            if (birthday.length() != 8) {
                System.out.println("YYYYMMDDの8個の数字でお願いします。");
                continue;
            }
            //날짜 포멧에 맞는지 검증하는 로직(Validation Date) 日付のフォーマットに合っているかを検証するロジック
            //YYYYMMDD形式ではない場合(最初から違う形式の場合)

            //→ try-catch文を利用して例外処理をします。決まった日付の形式と違う形式を
            //入力した場合、setLenient()を使って検証します。setLenient()がfalseの場合、
            //処理する際に入力された値が異なる形式の場合にエラーが発生します。
            //→ エラーが発生して、catch文を使って「正しい形式でお願いします。」と出力します。
            try {
                SimpleDateFormat dateFormatParser = new SimpleDateFormat("yyyyMMdd");
                dateFormatParser.setLenient(false);
                dateFormatParser.parse(birthday);
                break;
            } catch (Exception e){
                System.out.println("入力された誕生日の日付形式が正しくありません。");
                System.out.println("YYYYMMDDの形式でお願いします。");
                continue;
            }
        }

        //2. 오늘 날짜 가져오기 今日の日付
        //LocalDateで今の日付を取得します。
        //DateTimeFormatterでyyyyMMddの形式を決まります。
        //今日の日付を決まった形式で設定します。

        LocalDate today = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String todayString = today.format(dateTimeFormatter);


        //3. omkj.csv 파일을 읽어서 unsei가 null일 경우 넣어줌
        // OMKJ.CSV ファイルを読み込んでUNSEIに入れる(UNSEIがNULLではない場合)

        //Unseiは抽象クラスで、親クラスであるUnseiでオブジェクトArraylistを作ります。
        //Unseiで他のオブジェクト変数を作って、初期化します。
        //全ての結果が入れるresult.csvを読み込みます。
        List<Unsei> omikuji = new ArrayList<>(); //부모클래스 Unsei로 생성한 객체 어레이리스트
            Unsei unsei = null;
            File result = new File("omikuji/result.csv");

        //Unseiのオブジェクト:unseiが空いている場合
        //unseiが空いている場合、結果をセットするためにif文を使います。
        if(unsei == null) {
                try {
                    String path = "omikuji/omkj.csv";
                    String line = "";
                    BufferedReader br = new BufferedReader(new FileReader(path));

                    //omkj.csvから値を読み込みます。omkj.csvファイルは大吉、中吉、小吉、…、凶の結果が入っています。
                    //この値を入れるString [] valuesを作ります。
                    //readLineを使って１行ずつ読みます。値の区分は”,”で切って読み込みます。

                    String[] values; //1줄씩 받아서 저장할 어레이
                    br.readLine();
                    //1줄씩 csv파일 읽어오기 １行ずつcsvファイルから読み込む
                    while ((line = br.readLine()) != null) {
                        values = line.split(",");

                        //子クラス型の変数に入れる
                        //switch문으로 운세 지정해서 넣어주기
                        //Switch文を使って、作った抽象クラスを受け継いだ子クラス(Daikichi.java, Cyuukichi.java, …, Kyou.java)に
                        //それぞれの値をnewします。
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

                        //Unseiのmethodにそれぞれの値をセットします。
                        //それぞれの値をセットして、作ったArrayListである omikujiに入れます。
                        //最後、BufferedReaderを閉めて、例外処理をします。
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
            }

            //4. CSV파일에 결과 입력(이미 저장된 결과가 있을 경우) CSVファ우ルに結果をセットする(UNSEIにもう結果がある場合)
            else if(unsei != null){
                try {
                    FileReader reader = new FileReader(result);
                    BufferedReader br3 = new BufferedReader(reader);
                    String date;

                    //それぞれの変数に読み込んだ結果をセットします。
                    //でも、同じ誕生日と今日の日付の場合、同じ結果が出なければならないので、if文を使ってセットします。
                    while ((date = br3.readLine()) != null) {
                        String[] values2 = date.split(",");
                        String resbirthday = values2[0];
                        String restoday = values2[1];

                        if (resbirthday.equals(birthday) && restoday.equals(todayString)) {
                            String resunsei = values2[2];
                            String resnegaigoto = values2[3];
                            String resakinai = values2[4];
                            String resgakumon = values2[5];
                            String ls = System.lineSeparator();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        //5. 랜덤으로 출력하기 ランダム結果を出力する
        int rannum = new Random().nextInt(omikuji.size());
        unsei = omikuji.get(rannum);

        System.out.println(unsei.disp());

        //6. result.csv 파일에 입력 RESULT.CSVファイルに結果を書く
        try {

            //StringBuilderを使ってそれぞれの値を保存します。
            //fw.write(sb.toString())で保存した値を書いて、fw.flush()で全部放出します。
            //fw.close()でFileWriterを終了します。
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

    //7. main 클래스로 프로그램 실행 MAINクラスでプログラムを実行する
    public static void main(String[] args) throws Exception {
        Omikujiprogram omikujiprogram = new Omikujiprogram();
        omikujiprogram.omikujii();
    }
}

