/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package siinqee.net.admin;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author samimersha
 */
public class SiinqeeNetAdmin {

    static String ad_username = "<unknown>", ad_fullname = "<unknown>";
    static int day, month, year;

    public static String setDateNow() {
        String date = java.time.LocalDate.now().toString();
        String[] dateParts = date.split("-");
        day = Integer.parseInt(dateParts[2]);
        month = Integer.parseInt(dateParts[1]);
        year = Integer.parseInt(dateParts[0]);
        String dateNow = year + "-" + month + "-" + day;
        System.out.println("Required Date: Day : " + day + " Month : " + month + " Year: " + year);
        return dateNow;
    }

    public static void createLocalDB() {
        try {
            File myObj = new File("localdb.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static boolean checkIfActive() {
        boolean isActive = false, isRegistered = false;
        String content = getLocalDbContent();
        //Retriving local datas separately
        Scanner sc = new Scanner(content);
        String localUsername = sc.nextLine();
        String localSession = sc.nextLine();
        sc.close();
        //finding whether the client is registered
        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();
            String cmd = "SELECT * FROM siinqee.admin";
            ResultSet set = st.executeQuery(cmd);
            while (set.next()) {
                if (localUsername.equals(set.getString("ad_username"))) {
                    isRegistered = true;
                    if (localSession.equals(set.getString("ad_session"))) {
                        isActive = true;
                        ad_username = localUsername;
                        String firstname = set.getString("ad_firstname");
                        String lastname = set.getString("ad_lastname");
                        ad_fullname = firstname + " " + lastname;}}}         
            if (!isRegistered) {
                System.out.println("Account is not registered");
                JOptionPane.showMessageDialog(new JFrame(), "Account is not registered!\nPlease contact Network Administrators!");
            } else if (!isActive) {
                System.out.println("Account is not active!");
            }
        } catch (Exception e) {
            System.out.println("Error Occured");
            e.printStackTrace();
        }

        return isActive;
    }

    public static String getLocalDbContent() {
        String content = null;

        //reading file to get admin info saved locally
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
        Splash s = new Splash();
        s.setVisible(true);

        try {
            boolean isActive = false;
            for (int i = 0; i <= 100; i++) {
                Thread.sleep(5);
                s.loadingValue.setText(Integer.toString(i) + "%");
                s.progressBar.setValue(i);
                if (i == 10) {
                    createLocalDB();
                }
                if (i == 20) {
                    s.loadingLabel.setText("Checking Database");
                    isActive = checkIfActive();
                }
                if (i == 60) {
                    s.loadingLabel.setText("Almost there!");
                    Thread.sleep(10);
                } else if (i == 100) {
                    s.setVisible(false);
                    if (isActive) {
                        Main m = new Main(ad_fullname);
                        m.setVisible(true);
                        m.username = ad_username;
                        m.startUpdatingClients();
                    } else {
                        new Login().setVisible(true);
                        new Main(ad_username).startUpdatingClients();
                    }

                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
