package com.p2p.p4f.server;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        try {
            String connString = "jdbc:sqlserver://localhost\\MSSQLSERVER;databaseName=QuanLyDeAn;integratedSecurity=true";
            Connection conn = DriverManager.getConnection(connString);
            PreparedStatement stmt = conn.prepareStatement("SELECT HONV + ' ' + TENLOT + ' ' + TENNV FROM NHANVIEN");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}
