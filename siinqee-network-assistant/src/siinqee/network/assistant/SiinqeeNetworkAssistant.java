/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package siinqee.network.assistant;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 *
 * @author samimersha
 */
public class SiinqeeNetworkAssistant {

    public SiinqeeNetworkAssistant() {
        setDateNow();
    }
    static String cl_name = "<unknown>";
    static int cl_code = 0, day, month, year;

    public static String setDateNow() {
        String date = java.time.LocalDate.now().toString();
        String[] dateParts = date.split("-");
        day = Integer.parseInt(dateParts[2]);
        month = Integer.parseInt(dateParts[1]);
        year = Integer.parseInt(dateParts[0]);
        String dateNow = year + "-" + month + "-" + day;
        System.out.println("day : " + day + " month : " + month + " year: " + year);
        return dateNow;
    }

    public static boolean checkIfRegistered() {
        boolean isRegistered = false;
        String content = getLocalDbContent();
        //Retriving local datas separately
        Scanner sc = new Scanner(content);
        int localCode = Integer.parseInt(sc.nextLine());
        String localName = sc.nextLine();
        sc.close();
        //finding whether the client is registered
        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();

            String cmd = "SELECT * FROM siinqee.client";
            ResultSet set = st.executeQuery(cmd);
            while (set.next()) {
                if (localCode == (set.getInt("cl_code"))) {
                    if (localName.equals(set.getString("cl_name"))) {
                        isRegistered = true;
                        cl_name = localName;
                        cl_code = localCode;
                    } else {
                        System.out.println("Client Code" + localCode + "Not Registered");
                    }
                }
            }
        } catch (Exception e) {

            System.out.println(e);
            e.printStackTrace();
        }
        return isRegistered;
    }

    public static String getLocalDbContent() {
        String content = null;

        //reading file to get client name saved locally
        try {
            File myObj = new File("localdb.txt");
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (content != null) {
                    content = content + "\n" + data;
                } else {
                    content = data;
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return content;
    }

    public static Connection getConnection() {
        Connection con = null;

        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/siinqee";
            String username = "root";
            String password = "S#myDB35";

            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);

        } catch (ClassNotFoundException e) {
            System.out.println(e);
        } catch (SQLException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }
        return con;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Splash s = new Splash();
        s.setVisible(true);

        try {
            boolean isRegistered = false;
            for (int i = 0; i <= 100; i++) {
                Thread.sleep(5);
                s.loadingValue.setText(Integer.toString(i) + "%");
                s.progressBar.setValue(i);
                if (i == 10) {
                    s.loadingLabel.setText("Checking Database");
                    isRegistered = checkIfRegistered();
                }
                if (i == 60) {
                    s.loadingLabel.setText("Almost there!");
                    Thread.sleep(5);
                } else if (i == 100) {
                    s.setVisible(false);
                    if (isRegistered) {
                        Main m = new Main(cl_name, cl_code);
                        m.setVisible(true);
                        m.startUpdatingClientSession();
                    } else {
                        new Intro().setVisible(true);
                        
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
