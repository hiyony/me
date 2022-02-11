package com.company;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OmikujiDB {
    public void birthday() throws IOException {
        String birthday;
        BufferedReader br2;
        SimpleDateFormat dateFP;
        DateTimeFormatter datetimeFP;

        LocalDate today = LocalDate.now();
        datetimeFP = DateTimeFormatter.ofPattern("yyyyMMdd");
        String todayString = today.format(datetimeFP);

        br2 = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            System.out.println("お誕生日はいつですか？(yyyyMMdd 形式)　");
            birthday = br2.readLine();
            if (birthday.length() != 8) {
                System.out.println("yyyyMMddの8個の数字でお願いします。");
                continue;
            }
            try {
                dateFP = new SimpleDateFormat("yyyyMMdd");
                dateFP.setLenient(false);
                dateFP.parse(birthday);
                break;
            } catch (Exception e) {
                System.out.println("入力された誕生日の日付形式が正しくありません。");
                System.out.println("yyyyMMddの形式でお願いします。");
            }
        }
    }

    public void omikuji_db() throws IOException {
        birthday();

        //Connection: 데이터베이스와 연결하는 객체　データベースと連結するオブジェクト
        //Statement: 정적 Query를 처리할 때 유리하도록 만드는 객체 >> SELECT query에 이용
        //静的クエリを処理する場合使うオブジェクト >> SELECT Queryで使う
        //PreparedStatement: 동적 Query를 처리할 때 유리하도록 만드는 객체  >> INSERT query에 이용
        //動的クエリを処理する場合使うオブジェクト >> INSERT Queryで使う
        Connection conn = null;
        Statement stmt;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;
        PreparedStatement pstmt4 = null;
        String unsei_sql;
        String count_sql;
        String result_sql;
        String select_sql;
        ResultSet rs;
        ResultSet rs2;
        String server = "localhost"; //MySQL 서버주소 サーバーのアドレス
        String database = "omikuji"; //MySQL DB명　サーバーの名
        String user_name = "root"; //MySQL 서버ID　サーバーのID
        String password = "1005"; //MySQL 서버PW　サーバーのPW

        Unsei unsei = null;
        List<Unsei> omikuji = new ArrayList<>();
        String path = "omikuji/omkj.csv";
        String line;
        String line2;
        String [] values;
        BufferedReader br = null;
        BufferedReader br2 = null;


        // 접속 接続
        try {
            //DriverManager는 JDBC드라이버를 통해 Connection을 만드는 역할
            //DriverManagerはJDBCドライバーを通じてConnectionを作る役割
            conn = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database, user_name, password);

            //COUNT() function: 행의 개수 출력, null은 포함안됨
            count_sql = "SELECT COUNT (*) CNT FROM omikujii";
            pstmt3 = conn.prepareStatement(count_sql);
            rs = pstmt3.executeQuery();
            rs.next();
            int cnt = rs.getInt("CNT");

            //cnt == 0: 데이터베이스가 비어있을 경우
            if(cnt == 0) {
                unsei_sql = "INSERT INTO omikujii(omikujicode, unseicode, negaigoto, akinai, gakumon) VALUES(?, ?, ?, ?, ?)"; //쿼리문　クエリ文
                pstmt2 = conn.prepareStatement(unsei_sql); //쿼리를 실행할 객체를 생성(객체 생성시에 쿼리를 준다)
                //クエリを実装するオブジェクトを作る(オブジェクトを作るとクエリが与えられる）
                br = new BufferedReader(new FileReader(path));
                int count = 0;

                //오미쿠지를 데이터베이스에 저장 おみくじをDB格納する
                br.readLine();
                while ((line = br.readLine()) != null) {
                    values = line.split(",");
                    pstmt2.setString(2, values[0]);
                    pstmt2.setString(3, values[1]);
                    pstmt2.setString(4, values[2]);
                    pstmt2.setString(5, values[3]);
                    count++;

                    pstmt2.setInt(1, count);
                    //특정 내용을 데이터베이스에 적용시켜야 할 경우 executeUpdate() 메서드를 사용
                    //特定内容をデータベースに適用される場合、executeUpdate()　メソッドを使う
                    pstmt2.executeUpdate();
                }

            }

            select_sql = "SELECT unseicode, negaigoto, akinai, gakumon FROM omikujii";
            pstmt4 = conn.prepareStatement(select_sql);
            rs2 = pstmt.executeQuery();

            while (rs2.next()){
                unsei = selectUnsei(rs2.getString("unseicode"));
                unsei.setUnsei();
                unsei.setNegaigoto(rs2.getString("negaigoto"));
                unsei.setAkinai(rs2.getString("akinai"));
                unsei.setGakumon(rs2.getString("gakumon"));
            }

            result_sql = "INSERT INTO result(uranaidate, birthday, omikujicode) VALUES(?, ?, ?)";
            pstmt = conn.prepareStatement(result_sql);

            pstmt.setString(1, todayString);
            pstmt.setString(2, birthday);
            pstmt.setInt(3, );


            System.out.println(unsei.disp());

        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            //해제 解除
            try{
                if(br!=null){br.close();}
                if(pstmt != null){pstmt.close();}
                if(conn != null){conn.close();}
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Unsei selectUnsei(String unseistr) {
        Unsei unsei = null;
        switch (unseistr) {
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
                break;
        }
        return unsei;
    }

    public static void main(String [] args) throws IOException {
        OmikujiDB omikujiDB = new OmikujiDB();
        omikujiDB.omikuji_db();
    }
}
