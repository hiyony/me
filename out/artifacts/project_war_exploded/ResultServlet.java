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
    private static final String path = "omikuji/csvomkj.csv";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");

        //InputServletから誕生日パラメーターを読み込んでcheckBirthdayで文字数チェックする
        //InputServlet에서 생일 파라미터를 받아와서 checkBirthday에서 문자수 체크

        String birthday = (String) request.getAttribute("birthday");

        String checkmsg = "入力された形式が正しくありません。yyyyMMdd形式の８文字でお願いします。";
        request.setAttribute("checkmessage", checkmsg);
        request.getRequestDispatcher("inputservlet").forward(request, response);

//        String birthday = request.getParameter("birthday");
//        Boolean checkbday = checkBday.checkBirthday(birthday);
//
//        //入力された誕生日形式を検査してfalseをリターンする場合
//        //생일을 검사하고 false를 리턴할 경우(yyyyMMdd의 형태가 아닐 때)
//        if(!checkbday){
//
//            //RequestDispatcher : servlet, JSPが他のコンポーネントで移動する場合
//            //RequestDispatcher : servlet, JSP가 다른 컴포넌트로 수행을 옮기는 경우 사용
//            //forwardメソッドはservletが他のコンポーネントで移動するために使う
//            //forward 메소드가 servlet이 다른 컴포넌트에게 수행을 넘기는 작업을 함
//            request.setAttribute("checkmessage", "入力された形式が正しくありません。yyyyMMdd形式の８文字でお願いします。");
//            request.getRequestDispatcher("inputservlet").forward(request, response);
//        }

//        while(checkbday == false){
//            PrintWriter out = response.getWriter();
//            out.print("<!DOCTYPE html>");
//            out.print("<html><head>");
//            out.print("<title>Omikuji Web Service</title>");
//            out.print("<style>");
//            out.print("#input-form input{ padding: 5px 10px; text-align: center;}");
//            out.print("</style></head>");
//            out.print("<body>");
//            out.print("<h1>Omikuji Web Service</h1>");
//            out.print("<span>入力された形式が正しくありません。</span>");
//            out.print("<br><span>yyyyMMdd形式の８文字でお願いします。</span>");
//            out.print("<br><a href = 'javascript:history.go(-1)'>戻る");
//            out.print("</a></body></html>");
//            out.close();
//        }


        LocalDate today = LocalDate.now();
        DateTimeFormatter datetimeFP = DateTimeFormatter.ofPattern("yyyyMMdd");
        String todayString = today.format(datetimeFP);

        Connection conn = null;
        BufferedReader br = null;

        try {
            //DriverManager는 JDBC드라이버를 통해 Connection을 만드는 역할
            //DriverManagerはJDBCドライバーを通じてConnectionを作る役割
            conn = DBUtil.getConnection();

            String fortunemaster_selectsql = "SELECT unseicode, unseiname FROM fortunemaster";
            PreparedStatement pstmt1 = conn.prepareStatement(fortunemaster_selectsql);
            ResultSet rs1 = pstmt1.executeQuery();
            Map<String, String> unseiMap = new HashMap<String, String>();
            while (rs1.next()) {
                unseiMap.put(rs1.getString("unseicode"), rs1.getString("unseiname"));
            }

            if (unseiMap.isEmpty()){
                String line;
                br = new BufferedReader(new FileReader(path));

                br.readLine();

                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");

                    if (!unseiMap.keySet().contains(values[0])) {
                        unseiMap.put(values[1], values[0]);
                    }
                }

                String fortunemaster_sql = "INSERT INTO fortunemaster(unseicode, unseiname, renewalwriter, renewaldate, "
                        + "unseiwriter, unseiwritedate)"
                        + "VALUES(?, ?, ?, ?, ?, ?)";

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


            //3. fortunemaster 테이블과 omikujii테이블이 비어있는지 확인
            //3. omikujiiテーブルが空いているかを確認
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

            //4. 생일이 같고 점친 날이 같은 경우, omikujiID를 받아옴
            //4. 同じ誕生日で同じ占い日付だった場合、omikujiIDを受け入れる
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

            //5. omikujiID를 받아오지 못했을 경우, 랜덤 omikujiID를 받아옴
            //5. omikujiIDを受け入れない場合、ランダムomikujiIDを受け入れる
            if(omikujiID.isEmpty()){
                int rannum = new Random().nextInt(cnt) + 1; //アドバイスのこと
                omikujiID = String.valueOf(rannum);
            }

            //값을 저장해줌
            //値をセットする
            //Unsei unsei = null;
            Unsei unsei = new Unsei() {
                @Override
                public void setUnsei() {

                }
            };

            //6. omikujiID를 받아와서 오미쿠지 값을 받아옴
            //6. omikujiIDを受け入れておみくじ値を受け入れる
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

            while(rs6.next()) {
                unsei = selectUnsei(rs6.getString("unseiname"));
                unsei.setOmikujicode(omikujiID);
                unsei.setUnsei();
                unsei.setNegaigoto(rs6.getString("negaigoto"));
                unsei.setAkinai(rs6.getString("akinai"));
                unsei.setGakumon(rs6.getString("gakumon"));


                //7. result 테이블에 결과값을 넣어줌
                //7. result テーベルに結果値を入れる
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

            JspBeans jspbeans = new JspBeans();
            jspbeans.setUnsei();
            jspbeans.setNegaigoto(unsei.getNegaigoto());
            jspbeans.setAkinai(unsei.getAkinai());
            jspbeans.setGakumon(unsei.getGakumon());

            request.setAttribute("JspBeans", jspbeans);
            request.getRequestDispatcher("OmikujiJSP.jsp").forward(request, response);

        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            //해제 解除
            try{
                if(br!=null) {
                    br.close();
                }
                if(conn != null){
                    conn.close();
                }
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
}
