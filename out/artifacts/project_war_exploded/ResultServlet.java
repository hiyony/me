package com.company;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ResultServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String path = "/Users/yeoni/Documents/GitHub/project/omikuji/csvomkj.csv";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");

        //Inputservlet으로부터 생일 파라미터를 받아와서 yyyyMMdd 형식으로 바꾸어줌
        //Inputservletから誕生日パラメーターを受け入れてyyyyMMddの形式に変える

        String birthday = (String) request.getAttribute("birthday");

        LocalDate today = LocalDate.now();
        DateTimeFormatter datetimeFP = DateTimeFormatter.ofPattern("yyyyMMdd");
        String todayString = today.format(datetimeFP);

        Connection conn;
        BufferedReader br = null;

        try {
            //DB와 연결
            //DBと連結

            conn = DBUtil.getConnection();

            //fortunemaster 테이블에서 운세코드와 운세명 select문으로 받아오기
            //fortunemasterテーブルから運勢コードと運勢名をselect文で読み込む

            String fortunemaster_selectsql = "SELECT unseicode, unseiname FROM fortunemaster";
            PreparedStatement pstmt1 = conn.prepareStatement(fortunemaster_selectsql);
            ResultSet rs1 = pstmt1.executeQuery();

            //Hashmap ・ key-value의 구조로 함께 관리할 수 있는 메서드를 구현
            //        ・ 검색을 위한 자료구조
            //        ・ key를 이용해서 값을 저장하고 key를 이용해서 값을 꺼내오는 방식
            //        ・ 중복을 허용하지 않고, 속도가 빠르다는 장점이 있음

            //hashmap 생성, 운세명과 운세코드를 함께 받아와 hashmap에 넣어줌
            //hashmapを作って、運勢名と運勢コードを一緒に受け入れてhashmapに受け入れる

            Map<String, String> unseiMap = new HashMap<>();
            while (rs1.next()) {
                unseiMap.put(rs1.getString("unseiname"), rs1.getString("unseicode"));
            }

            //hashmap이 비어있을 경우, CSV 파일로부터 데이터를 가져와서 key-value 형식으로 hashmap에 넣어줌
            //hashmapが空いている場合、CSVファイルからデータを読み込んでkey-value形式でhashmapに受け入れる

            if (unseiMap.isEmpty()){
                String line;
                br = new BufferedReader(new FileReader(path));

                br.readLine();

                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");

                    //key값에 운세코드가 포함되어 있지 않을 경우 key와 value를 세팅
                    //keyで運勢コードが含まれない場合、keyとvalueをセット

                    if (!unseiMap.keySet().contains(values[1])) {
                        unseiMap.put(values[1], values[0]);
                    }
                }

                //fortunemaster 테이블에 운세명, 운세코드, 작성자, 작성일 등 값을 넣어줌
                //fortunemasterテーブルに運勢名、運勢コード、作成者、作成日などの値を受け入れる

                String fortunemaster_sql = "INSERT INTO fortunemaster(unseiname, unseicode, renewalwriter, renewaldate, "
                        + "unseiwriter, unseiwritedate)"
                        + "VALUES(?, ?, ?, ?, ?, ?)";

                //Entryset에 담긴 모든 key-value 값을 얻어서 운세명과 운세 코드에 값을 넣어줌
                //Entrysetに含まれている全てのkeyーvalueを読み込んで運勢名と運勢コードにセット

                for (Map.Entry<String, String> entry : unseiMap.entrySet()) {
                    PreparedStatement pstmt2 = conn.prepareStatement(fortunemaster_sql);
                    pstmt2.setString(1, entry.getKey());
                    pstmt2.setString(2, entry.getValue());
                    pstmt2.setString(3, "ヒヨ");
                    pstmt2.setString(4, todayString);
                    pstmt2.setString(5, "ヒヨ");
                    pstmt2.setString(6, todayString);
                    pstmt2.executeUpdate();
                }
            }

            //COUNT() function: 행의 개수 출력, null은 포함안됨
            String count_sql = "SELECT COUNT(*) AS CNT FROM omikujii";
            PreparedStatement pstmt3 = conn.prepareStatement(count_sql);
            ResultSet rs3 = pstmt3.executeQuery();
            rs3.next();
            int cnt = rs3.getInt("CNT");


            //fortunemaster 테이블과 omikujii테이블이 비어있는지 확인
            //omikujiiテーブルが空いているかを確認
            //cnt == 0: 테이블 안의 결과 수를 count해서 데이터베이스가 비어있을 경우
            //        : テーブルの中の結果数をcountしてデータベースが空いている場合
            if (cnt == 0 ) {
                String line;
                //파일에서 오미쿠지 가져오기 ファイルからおみくじを読み込む
                br = new BufferedReader(new FileReader(path));
                br.readLine();

                String omikuji_sql = "INSERT INTO omikujii(omikujicode, unseicode, negaigoto, akinai, gakumon, " +
                        "renewalwriter, renewaldate, unseiwriter, unseiwritedate) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)"; //쿼리문　クエリ文

                while ((line = br.readLine()) != null) {
                    PreparedStatement pstmt4 = conn.prepareStatement(omikuji_sql);
                    //쿼리를 실행할 객체를 생성(객체 생성시에 쿼리를 준다)
                    //クエリを実装するオブジェクトを作る(オブジェクトを作るとクエリが与えられる）
                    pstmt4.setString(1, Integer.toString(cnt + 1));
                    String[] values = line.split(",");
                    pstmt4.setString(2, unseiMap.get(values[1]));
                    pstmt4.setString(3, values[2]);
                    pstmt4.setString(4, values[3]);
                    pstmt4.setString(5, values[4]);
                    pstmt4.setString(6, "ヒヨ");
                    pstmt4.setString(7, todayString);
                    pstmt4.setString(8, "ヒヨ");
                    pstmt4.setString(9, todayString);
                    cnt++;

                    //특정 내용을 데이터베이스에 적용시켜야 할 경우 executeUpdate() 메서드를 사용
                    //特定内容をデータベースに適用される場合、executeUpdate()　メソッドを使う
                    pstmt4.executeUpdate();
                }
            }

            //생일이 같고 점친 날이 같은 경우, omikujiID를 받아옴
            //同じ誕生日で同じ占い日付だった場合、omikujiIDを受け入れる
            String compare_sql = "SELECT omikujicode, uranaidate, birthday " +
                    "FROM unseiresult " +
                    "WHERE uranaidate = ? AND birthday = ?";
            PreparedStatement pstmt5 = conn.prepareStatement(compare_sql);
            pstmt5.setString(1, todayString); //uranaidate = ?
            pstmt5.setString(2, birthday); //birthday = ?
            ResultSet rs5 = pstmt5.executeQuery();

            String omikujiID = "";
            if (rs5.next()) {
                omikujiID = rs5.getString("omikujicode");
            }

            //omikujiID를 받아오지 못했을 경우, 랜덤 omikujiID를 받아옴
            //omikujiIDを受け入れない場合、ランダムomikujiIDを受け入れる
            if(omikujiID.isEmpty()){
                int rannum = new Random().nextInt(cnt) + 1; //アドバイスのこと
                omikujiID = String.valueOf(rannum);
            }

            //값을 저장해줌
            //値をセットする
            //omikujiID를 받아와서 오미쿠지 값을 받아옴
            //omikujiIDを受け入れておみくじ値を受け入れる

            String result_sql  = "SELECT f.unseiname as unseiname, " +
                    "o.negaigoto as negaigoto, " +
                    "o.akinai as akinai, " +
                    "o.gakumon as gakumon " +
                    "FROM omikujii o " +
                    "JOIN fortunemaster f " +
                    "ON f.unseicode = o.unseicode " +
                    "WHERE o.omikujicode = ?";
            PreparedStatement pstmt6 = conn.prepareStatement(result_sql);
            pstmt6.setString(1, omikujiID); //o.omikujicode = ?
            ResultSet rs6 = pstmt6.executeQuery();

            Unsei unsei = null;
            while(rs6.next()) {
                unsei = selectUnsei(rs6.getString("unseiname"));
                unsei.setOmikujicode(omikujiID);
                unsei.setUnsei();
                unsei.setNegaigoto(rs6.getString("negaigoto"));
                unsei.setAkinai(rs6.getString("akinai"));
                unsei.setGakumon(rs6.getString("gakumon"));


                //result 테이블에 결과값을 넣어줌
                //result テーベルに結果値を入れる

                String insertresult_sql = "INSERT INTO unseiresult(uranaidate, birthday, omikujicode, renewalwriter, " +
                        "renewaldate, unseiwriter, unseiwritedate) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt7 = conn.prepareStatement(insertresult_sql);

                pstmt7.setString(1, todayString);
                pstmt7.setString(2, birthday);
                pstmt7.setString(3, omikujiID);
                pstmt7.setString(4, "ヒヨ");
                pstmt7.setString(5, todayString);
                pstmt7.setString(6, "ヒヨ");
                pstmt7.setString(7, todayString);

                pstmt7.executeUpdate();

            }

            //Bean : 웹에서 사용할 데이터를 java 클래스로 정의해놓은 후 데이터 전송에 사용되는 클래스
            //     : ウェーブで使うデータをjavaクラスで正義した後、データ配信に使うクラス

            JspBeans jspbeans = new JspBeans();
            jspbeans.setUnsei(unsei.getUnsei());
            jspbeans.setNegaigoto(unsei.getNegaigoto());
            jspbeans.setAkinai(unsei.getAkinai());
            jspbeans.setGakumon(unsei.getGakumon());

            request.setAttribute("JspBeans", jspbeans);
            request.getRequestDispatcher("OmikujiJSP.jsp").forward(request, response);



        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            //해제 解除
            if(br!=null) {
                br.close();
            }
        }
    }

    public static Unsei selectUnsei(String unseistr) {
        Unsei fortune = null;

        switch (unseistr) {
            case "大吉":
                fortune = new Daikichi();
                break;
            case "中吉":
                fortune = new Cyuukichi();
                break;
            case "小吉":
                fortune = new Syoukichi();
                break;
            case "吉":
                fortune = new Kichi();
                break;
            case "末吉":
                fortune = new Sueyosi();
                break;
            case "凶":
                fortune = new Kyou();
                break;
            default:
                break;
        }
        return fortune;
    }
}

