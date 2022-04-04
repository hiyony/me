package com.company;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;


public class InputServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    Boolean flag = true;

    //초기 화면은 doGet()으로, 생일을 체크해서 에러메세지를 표시하는 화면은 doPost()로 구현
    //view()를 통해서 초기 화면 Html 부분을 세팅
    //初期画面はdoGet()で、誕生日をチェックしてエラーメッセージを表示する画面はdoPost()で実装
    //view()を通して初期画面のhtml部分をセット

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        view(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");

        //입력받은 생일을 파라미터로 받아와서, 생일을 체크
        //入力された誕生日をパラメーターで受け入れて、誕生日をチェックする

        String birthday = request.getParameter("birthday");
        request.setAttribute("birthday", birthday);
        Boolean checkday = checkBday.checkBirthday(birthday);

        //생일이 제대로 입력되었으면, resultservlet으로 이동
        //생일이 제대로 입력되지 않았으면, 에러메세지 표시
        //誕生日がちゃんと入力されたら、resultservletで移動
        //誕生日がちゃんと入力されなかったら、エラーメッセージを表示

        if(checkday.equals(true)){
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("rsservlet");
            requestDispatcher.forward(request, response);
        } else if(checkday.equals(false)){
            flag = false;
            view(request, response);
        }
    }

    protected void view(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");

        //html 화면을 표시 htmlの画面を表示する

        PrintWriter out = response.getWriter();
        out.print("<!DOCTYPE html>");
        out.print("<html><head>");
        out.print("<title>Omikuji Web Service</title>");
        out.print("<style>");
        out.print("#input-form input{ padding: 5px 10px; text-align: center;}");
        out.print("p{color: red;}");
        out.print("</style></head>");
        out.print("<body>");
        out.print("<h1>Omikuji Web Service</h1>");
        out.print("<form action=\"inputservlet\" method=\"POST\" id=\"input-form\">");
        out.print("<span>お誕生日を入力してください！</span>");
        out.print("<input type=\"text\" name=\"birthday\" placeholder=\"yyyyMMddの形式\">");
        out.print("<input type=\"submit\" value=\"確認\">");
        if(!flag){
            out.print("<p>入力された形式が正しくありません。yyyyMMdd形式の８文字でお願いします。</p>");
            flag = true;
        }
        out.print("</form></body></html>");
    }
}
