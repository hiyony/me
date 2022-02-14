package com.company;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OmikujiDB {

    public void omikuji_db() throws IOException {
        String birthday;
        BufferedReader br3;
        DateTimeFormatter datetimeFP;

        br3 = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("お誕生日はいつですか？(yyyyMMdd 形式)　");
        birthday = br3.readLine();
        if (birthday.length() != 8) {
            System.out.println("yyyyMMddの8個の数字でお願いします。");
        }
        new Birthday(birthday);

        LocalDate today = LocalDate.now();
        datetimeFP = DateTimeFormatter.ofPattern("yyyyMMdd");
        String todayString = today.format(datetimeFP);

        //Connection: 데이터베이스와 연결하는 객체　データベースと連結するオブジェクト
        //Statement: 정적 Query를 처리할 때 유리하도록 만드는 객체 >> SELECT query에 이용
        //静的クエリを処理する場合使うオブジェクト >> SELECT Queryで使う
        //PreparedStatement: 동적 Query를 처리할 때 유리하도록 만드는 객체  >> INSERT query에 이용
        //動的クエリを処理する場合使うオブジェクト >> INSERT Queryで使う
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2; PreparedStatement pstmt3;
        PreparedStatement pstmt4; PreparedStatement pstmt5;
        PreparedStatement pstmt6;
        String unsei_sql; String count_sql; String count_sql2;
        String insertresult_sql; String omkjtbl_sql; String select_sql;
        ResultSet rs; ResultSet rs2; ResultSet rs3;
        String server = "localhost"; //MySQL 서버주소 サーバーのアドレス
        String database = "omikuji"; //MySQL DB명　サーバーの名
        String user_name = "root"; //MySQL 서버ID　サーバーのID
        String password = "1005"; //MySQL 서버PW　サーバーのPW

        //Unsei unsei = null;
        Unsei unsei = new Unsei() {
            @Override
            public void setUnsei() {

            }
        };
        List<Unsei> omikuji = new ArrayList<>();
        String path = "omikuji/omkj.csv";
        String line;
        String [] values;
        int count = 0;
        BufferedReader br = null;
        String omikujicode = null;


        // 접속 接続
        try {
            //DriverManager는 JDBC드라이버를 통해 Connection을 만드는 역할
            //DriverManagerはJDBCドライバーを通じてConnectionを作る役割
            conn = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database, user_name, password);

            //COUNT() function: 행의 개수 출력, null은 포함안됨
            count_sql = "SELECT COUNT(*) AS CNT FROM omikujii";
            count_sql2 = "SELECT COUNT(*) AS CNT2 FROM fortunemaster";
            pstmt6 = conn.prepareStatement(count_sql2);
            pstmt3 = conn.prepareStatement(count_sql);
            rs = pstmt3.executeQuery();
            rs3 = pstmt6.executeQuery();
            rs.next();
            rs3.next();
            int cnt = rs.getInt("CNT");
            int cnt2 = rs3.getInt("CNT2");

            //cnt == 0: 데이터베이스가 비어있을 경우
            if (cnt == 0 && cnt2 == 0) {
                omkjtbl_sql = "INSERT INTO fortunemaster(unseicode, unseiname, renewalwriter, renewaldate)" +
                        "VALUES(?, ?, ?, ?)";
                pstmt5 = conn.prepareStatement(omkjtbl_sql);

                unsei_sql = "INSERT INTO omikujii(omikujicode, unseicode, negaigoto, akinai, gakumon, " +
                                                 "renewalwriter, renewaldate) " +
                                 "VALUES(?, ?, ?, ?, ?, ?, ?)"; //쿼리문　クエリ文
                pstmt2 = conn.prepareStatement(unsei_sql); //쿼리를 실행할 객체를 생성(객체 생성시에 쿼리를 준다)
                //クエリを実装するオブジェクトを作る(オブジェクトを作るとクエリが与えられる）

                br = new BufferedReader(new FileReader(path));

                //오미쿠지를 데이터베이스에 저장 おみくじをDB格納する
                br.readLine();
                while ((line = br.readLine()) != null) {
                    values = line.split(",");
                    pstmt5.setString(2, values[0]);
                    pstmt5.setString(3, "ヒヨ");
                    pstmt5.setString(4, todayString);
                    pstmt2.setString(3, values[1]);
                    pstmt2.setString(4, values[2]);
                    pstmt2.setString(5, values[3]);
                    pstmt2.setString(6, "ヒヨ");
                    pstmt2.setString(7, todayString);
                    count++;

                    pstmt5.setInt(1, count);

                    omikujicode = String.valueOf(count);
                    pstmt2.setString(1, omikujicode);
                    pstmt2.setInt(2, count);
                    //특정 내용을 데이터베이스에 적용시켜야 할 경우 executeUpdate() 메서드를 사용
                    //特定内容をデータベースに適用される場合、executeUpdate()　メソッドを使う
                    pstmt5.executeUpdate();
                    pstmt2.executeUpdate();
                }
            }

            if(omikuji.size() != 0){
                int rannum = new Random().nextInt(count+1);
                unsei = omikuji.get(rannum);
            }

            select_sql = "SELECT omikujicode, unseicode, negaigoto, akinai, gakumon FROM omikujii";
            pstmt4 = conn.prepareStatement(select_sql);
            rs2 = pstmt4.executeQuery();

            while (rs2.next()) {
                unsei.setOmikujicode(omikujicode);
                unsei = selectUnsei(rs2.getString("unseicode"));
                unsei.setUnsei();
                unsei.setNegaigoto(rs2.getString("negaigoto"));
                unsei.setAkinai(rs2.getString("akinai"));
                unsei.setGakumon(rs2.getString("gakumon"));
            }

            insertresult_sql = "INSERT INTO result(uranaidate, birthday, omikujicode, renewalwriter, renewaldate) " +
                    "VALUES(?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertresult_sql);

            pstmt.setString(1, todayString);
            pstmt.setString(2, birthday);
            pstmt.setString(3, unsei.getOmikujicode());
            pstmt.setString(4, "ヒヨ");
            pstmt.setString(5, todayString);

            pstmt.executeUpdate();
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
        //Unsei unsei = null;
        Unsei unsei = new Unsei() {
            @Override
            public void setUnsei() {

            }
        };
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
