package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class OmikujiDB2 {
    private static final String path = "omikuji/csvomkj.csv";

    public static void main(String[] args) throws Exception {
        OmikujiDB2 omikujiDB2 = new OmikujiDB2();
        omikujiDB2.omikuji_db2();
    }

    public void omikuji_db2() throws Exception {

        //2. 생일 입력 요구 誕生日の入力を求める
        DateTimeFormatter datetimeFP;

        System.out.print("お誕生日はいつですか？(yyyyMMdd 形式)　");

        BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
        String birthday = br2.readLine();

        Boolean checkbday = checkBday.checkBirthday(birthday);

        while (checkbday == false) {
            System.out.print("yyyyMMdd形式の8文字列でお願いします。");
            br2 = new BufferedReader(new InputStreamReader(System.in));
            birthday = br2.readLine();
            checkbday = checkBday.checkBirthday(birthday);
        }

        LocalDate today = LocalDate.now();
        datetimeFP = DateTimeFormatter.ofPattern("yyyyMMdd");
        String todayString = today.format(datetimeFP);

        //Connection: 데이터베이스와 연결하는 객체　データベースと連結するオブジェクト
        //Statement: 정적 Query를 처리할 때 유리하도록 만드는 객체 >> SELECT query에 이용
        //静的クエリを処理する場合使うオブジェクト >> SELECT Queryで使う
        //PreparedStatement: 동적 Query를 처리할 때 유리하도록 만드는 객체  >> INSERT query에 이용
        //動的クエリを処理する場合使うオブジェクト >> INSERT Queryで使う
        Connection conn = null;
        BufferedReader br = null;

        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;
        PreparedStatement pstmt4 = null;
        PreparedStatement pstmt5 = null;

        ResultSet rs;
        ResultSet rs2;
        ResultSet rs3;

        String line;
        String [] values;
        Unsei unsei = null;


        try {
            conn = DBUtil.getConnection();

            //COUNT() function: 행의 개수 출력, null은 포함안됨
            String count_sql = "SELECT COUNT(*) AS CNT FROM omikujii";
            String count_sql2 = "SELECT COUNT(*) AS CNT2 FROM fortunemaster";
            pstmt1 = conn.prepareStatement(count_sql);
            pstmt2 = conn.prepareStatement(count_sql2);
            rs = pstmt1.executeQuery();
            rs2 = pstmt2.executeQuery();
            rs.next();
            rs2.next();
            int cnt = rs.getInt("CNT");
            int cnt2 = rs2.getInt("CNT2");

            String omikujiID = "";

            //3. fortunemaster 테이블과 omikujii테이블이 비어있는지 확인
            //3. fortunemaster テーブルとomikujiiテーブルが空いているかを確認
            //cnt == 0: 테이블 안의 결과 수를 count해서 데이터베이스가 비어있을 경우
            //        : テーブルの中の結果数をcountしてデータベースが空いている場合
            if (cnt == 0 && cnt2 == 0) {
                int count = 0;
                String omkjtbl_sql = "INSERT INTO fortunemaster(unseicode, unseiname, renewalwriter, " +
                        "renewaldate, unseiwriter, unseiwritedate)" +
                        "VALUES(?, ?, ?, ?, ?, ?)";
                pstmt1 = conn.prepareStatement(omkjtbl_sql);

                String unsei_sql = "INSERT INTO omikujii(omikujicode, unseicode, negaigoto, akinai, gakumon, " +
                        "renewalwriter, renewaldate, unseiwriter, unseiwritedate) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)"; //쿼리문　クエリ文
                pstmt2 = conn.prepareStatement(unsei_sql); //쿼리를 실행할 객체를 생성(객체 생성시에 쿼리를 준다)
                //クエリを実装するオブジェクトを作る(オブジェクトを作るとクエリが与えられる）
                //파일에서 오미쿠지 가져오기 ファイルからおみくじを読み込む
                br = new BufferedReader(new FileReader(path));

                //오미쿠지를 데이터베이스에 저장 おみくじをDB格納する
                br.readLine();
                while ((line = br.readLine()) != null) {
                    values = line.split(",");
                    pstmt1.setString(2, values[0]);
                    pstmt1.setString(3, "ヒヨ");
                    pstmt1.setString(4, todayString);
                    pstmt1.setString(5, "ヒヨ");
                    pstmt1.setString(6, todayString);

                    pstmt2.setString(3, values[1]);
                    pstmt2.setString(4, values[2]);
                    pstmt2.setString(5, values[3]);
                    pstmt2.setString(6, "ヒヨ");
                    pstmt2.setString(7, todayString);
                    pstmt2.setString(8, "ヒヨ");
                    pstmt2.setString(9, todayString);
                    count++;

                    pstmt1.setInt(1, count);

                    String omikujicode = String.valueOf(count);
                    pstmt2.setString(1, omikujicode);
                    pstmt2.setInt(2, count);

                    //특정 내용을 데이터베이스에 적용시켜야 할 경우 executeUpdate() 메서드를 사용
                    //特定内容をデータベースに適用される場合、executeUpdate()　メソッドを使う
                    pstmt1.executeUpdate();
                    pstmt2.executeUpdate();
                }
                pstmt1 = conn.prepareStatement(count_sql);
                rs = pstmt1.executeQuery();
                rs.next();
                cnt = rs.getInt("CNT");
            }

            //4. 생일이 같고 점친 날이 같은 경우, omikujiID를 받아옴
            //4. 同じ誕生日で同じ占い日付だった場合、omikujiIDを受け入れる
            String compare_sql = "SELECT omikujicode, uranaidate, birthday " +
                    "FROM unseiresult " +
                    "WHERE uranaidate = ? AND birthday = ?";
            pstmt3 = conn.prepareStatement(compare_sql);
            pstmt3.setString(1, todayString); //uranaidate = ?
            pstmt3.setString(2, birthday); //birthday = ?
            rs3 = pstmt3.executeQuery();

            while (rs3.next()) {
                omikujiID = rs3.getString("omikujicode");
            }

            //5. omikujiID를 받아오지 못했을 경우, 랜덤 omikujiID를 받아옴
            //5. omikujiIDを受け入れない場合、ランダムomikujiIDを受け入れる
            if (omikujiID.isEmpty()) {
                int rannum = new Random().nextInt(cnt + 1);
                omikujiID = String.valueOf(rannum);
            }

            //6. omikujiID를 받아와서 오미쿠지 값을 받아옴
            //6. omikujiIDを受け入れておみくじ値を受け入れる
            String result_sql = "SELECT f.unseiname as unseiname, " +
                    "o.negaigoto as negaigoto, " +
                    "o.akinai as akinai, " +
                    "o.gakumon as gakumon " +
                    "FROM omikujii o " +
                    "JOIN fortunemaster f " +
                    "ON f.unseicode = o.unseicode " +
                    "WHERE o.omikujicode = ?";
            pstmt4 = conn.prepareStatement(result_sql);
            pstmt4.setString(1, omikujiID); //o.omikujicode = ?
            ResultSet rs4 = pstmt4.executeQuery();


            //값을 저장해줌
            //値をセットする
            while (rs4.next()) {

                unsei.setOmikujicode(omikujiID);
                unsei = selectUnsei(rs4.getString("unseiname"));
                unsei.setUnsei();
                unsei.setNegaigoto(rs4.getString("negaigoto"));
                unsei.setAkinai(rs4.getString("akinai"));
                unsei.setGakumon(rs4.getString("gakumon"));

                //7. result 테이블에 결과값을 넣어줌
                //7. result テーベルに結果値を入れる
                String insertresult_sql = "INSERT INTO unseiresult(uranaidate, birthday, omikujicode, renewalwriter, " +
                        "renewaldate, unseiwriter, unseiwritedate) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?)";
                pstmt5 = conn.prepareStatement(insertresult_sql);

                pstmt5.setString(1, todayString);
                pstmt5.setString(2, birthday);
                pstmt5.setString(3, omikujiID);
                pstmt5.setString(4, "ヒヨ");
                pstmt5.setString(5, todayString);
                pstmt5.setString(6, "ヒヨ");
                pstmt5.setString(7, todayString);

                pstmt5.executeUpdate();
            }

            //8. 콘솔로 결과 출력
            //8. コンソールで結果出力する
            System.out.println(unsei.disp());


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //해제 解除
            try {
                if (br != null) {
                    br.close();
                }
                if (pstmt1 != null) {
                    pstmt1.close();
                }
                if (pstmt2 != null) {
                    pstmt2.close();
                }
                if (pstmt3 != null) {
                    pstmt3.close();
                }
                if (pstmt4 != null) {
                    pstmt4.close();
                }
                if (pstmt5 != null) {
                    pstmt5.close();
                }
                if (conn != null) {
                    conn.close();
                }
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
}


