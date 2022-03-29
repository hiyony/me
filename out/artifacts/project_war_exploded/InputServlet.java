package com.company;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;


public class InputServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        view(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        String birthday = request.getParameter("birthday");
        request.setAttribute("birthday", birthday);
        Boolean checkday = checkBday.checkBirthday(birthday);

        if(checkday.equals(true)){
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("rsservlet");
            requestDispatcher.forward(request, response);
        } else if(checkday.equals(false)){
            out.print("<p>入力された形式が正しくありません。yyyyMMdd形式の８文字でお願いします。</p>");
        }
        view(request, response);
    }

    protected void view(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");

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
        out.print("</form></body></html>");
        out.close();

    }
}
