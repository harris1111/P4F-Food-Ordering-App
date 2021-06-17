package com.p2p.p4f.server;

import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class Main {
    public void testJDBC() {
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
    
    public void testServerReactor(int nThreads, int port, String ip) {
        try {
            final ServerReactor sv = new ServerReactor(nThreads, port, ip, "E:/logs.txt");
            Thread t1 = new Thread(
                    () -> {
                        try {
                            sv.start();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
            Thread t2 = new Thread(
                () -> {
                    try (Scanner sc = new Scanner(System.in)) {
                        int input = sc.nextInt();
                        if (input == 0)
                            sv.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            );
            t1.start();
            t2.start();
        }
        catch (Exception e) {
            System.out.println("Invalid bind address or logging path.");
        }
    }
    
    public static void main(String[] args) {
        /*try {
            ClientTest.run(null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }*/
        Main m = new Main();
        m.testServerReactor(5, 1080, "192.168.1.4");
    }
}
