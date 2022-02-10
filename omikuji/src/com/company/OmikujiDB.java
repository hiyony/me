package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
            } catch (Exception e) {
                e.printStackTrace();
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
        Statement stmt = null;
        PreparedStatement pstmt = null;
        String unsei_sql;
        String result_sql = null;
        String server = "localhost"; //MySQL 서버주소 サーバーのアドレス
        String database = ""; //MySQL DB명　サーバーの名
        String user_name = "root"; //MySQL 서버ID　サーバーのID
        String password = "1005"; //MySQL 서버PW　サーバーのPW

        Unsei unsei = null;
        List<Unsei> omikuji = new ArrayList<>();
        String path = "omikuji/omkj.csv";
        String line;
        String [] values;
        BufferedReader br = null;


        //1. 드라이버 로딩 ドライバーのロディング
        try{
            //MySQL의 JDBC Driver Class를 로딩　MySQLのJDBCドライバーのクラスをロディング
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e){
            System.err.println("<JDBC エラー> ドライバーロディングのエラー: " + e.getMessage());
            e.printStackTrace();
        }

        //2. 접속 接続
        try {
            //DriverManager는 JDBC드라이버를 통해 Connection을 만드는 역할
            //DriverManagerはJDBCドライバーを通じてConnectionを作る役割
            conn = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database, user_name, password);
            unsei_sql = "INSERT INTO omikujii(運勢コード, 願い事, 商い, 学問) VALUES(?, ?, ?, ?)"; //쿼리문　クエリ文
            pstmt = conn.prepareStatement(unsei_sql); //쿼리를 실행할 객체를 생성(객체 생성시에 쿼리를 준다)
                                                      //クエリを実装するオブジェクトを作る(オブジェクトを作るとクエリが与えられる）
            br = new BufferedReader(new FileReader(path));

            br.readLine();
            while((line = br.readLine()) != null){
                values = line.split(",");
                unsei = selectUnsei(values[0]);
                unsei.setUnsei();
                unsei.setNegaigoto(values[1]);
                unsei.setAkinai(values[2]);
                unsei.setGakumon(values[3]);
                omikuji.add(unsei);
            }

            //DB에 ArrayList 내용 넣기 DBにArrayListの内容を入れる
            int size = omikuji.size();
            for (int i = 0; i < size; i++){
                Unsei fortune = omikuji.get(i);
                pstmt.setString(1, fortune.getUnsei());
                pstmt.setString(2, fortune.getNegaigoto());
                pstmt.setString(3, fortune.getAkinai());
                pstmt.setString(4, fortune.getGakumon());

                //특정 내용을 데이터베이스에 적용시켜야 할 경우 executeUpdate() 메서드를 사용
                //特定内容をデータベースに適用される場合、executeUpdate()　メソッドを使う
                pstmt.executeUpdate();
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            //3. 해제 解除
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
