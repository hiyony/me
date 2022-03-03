package com.company;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

public class OmikujiServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        out.print("<form action=\"omkjiservlet\" method=\"POST\" id=\"input-form\">");
        out.print("<span>お誕生日を入力してください！</span>");
        out.print("<input type=\"text\" name=\"birthday\" placeholder=\"yyMMddの形式\">");
        out.print("<input type=\"submit\" value=\"確認\">");
        out.print("</form></body></html>");
        out.close();

    }
}
