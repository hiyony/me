package com.company;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;


public class InputServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");

        PrintWriter out = response.getWriter();
        out.print("<!DOCTYPE html>");
        out.print("<html><head>");
        out.print("<title>Omikuji Web Service</title>");
        out.print("<style>");
        out.print("#input-form input{ padding: 5px 10px; text-align: center;}");
        out.print("</style></head>");
        out.print("<body>");
        out.print("<h1>Omikuji Web Service</h1>");
        out.print("<form action=\"rsservlet\" method=\"GET\" id=\"input-form\">");
        out.print("<span>お誕生日を入力してください！</span>");
        out.print("<input type=\"text\" name=\"birthday\" placeholder=\"yyyyMMddの形式\">");
        out.print("<input type=\"submit\" value=\"確認\">");

        String birthday = request.getParameter("birthday");
        Boolean checkbday = checkBday.checkBirthday(birthday);
        String checkmsg = "入力された形式が正しくありません。yyyyMMdd形式の８文字でお願いします。";

        System.out.println(checkbday);

        if(checkbday.equals(false)){
            out.print("<br><br><span>" + checkmsg + "</span");
        } else{
            request.setAttribute("birthday", birthday);
            RequestDispatcher rd = request.getRequestDispatcher("rsservlet");
            rd.forward(request, response);
        }


        //request.getAttributeはObject型なのでString型で変換する
        //request.getAttribute는 Object형이기 때문에 String으로 형변환
//        String checkmsg = (String) request.getAttribute("checkmessage");
//
//        //nullではない場合だけエラーメッセージを表示する
//        //이 처리를 해주지 않으면 null인 상태가 화면에 표시됨
//        //따라서 null이 아닐 경우에만 에러메세지가 뜰 수 있도록 표시해줌
//        if(checkmsg != null) {
//            out.print("<br><br><span>" + checkmsg + "</span>");
//        }

        out.print("</form></body></html>");
        out.close();

    }
}
