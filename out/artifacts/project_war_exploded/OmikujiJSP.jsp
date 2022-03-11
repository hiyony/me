<%@ page import="com.company.JspBeans" %><%--
  Created by IntelliJ IDEA.
  User: yeoni
  Date: 2022/03/03
  Time: 7:52 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Omikuji Web Service</title>
</head>
<body>
    <h1>今日の運勢はどうですか？</h1>
    <p>
        今日の運勢は<%=JspBeans.getUnsei()%>です。
        <br>願い事 : <%=JspBeans.getNegaigoto()%>
        <br>商い : <%=JspBeans.getAkinai()%>
        <br>学問 : <%=JspBeans.getGakumon()%>
    </p>

</body>
</html>
