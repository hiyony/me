package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OmikujiDB {
    public static void main(String [] args){
        Connection con = null;

        String server = "localhost"; //MySQL 서버주소 サーバーのアドレス
        String database = ""; //MySQL DB명　サーバーの名
        String user_name = "root"; //MySQL 서버ID　サーバーのID
        String password = "1005"; //MySQL 서버PW　サーバーのPW

        //1. 드라이버 로딩 ドライバーのロディング
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e){
            System.err.println("<JDBC エラー> ドライバーロディングのエラー: " + e.getMessage());
            e.printStackTrace();
        }

        //2. 접속 接続
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database, user_name, password);
            System.out.println("正常に接続されました。");
        } catch (SQLException e){
            System.out.println("con エラー: "+ e.getMessage());
            e.printStackTrace();
        }

        //3. 해제 解除
        try{
            if(con != null){
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
