/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package siinqee.net.admin;

import java.awt.Color;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import static siinqee.net.admin.SiinqeeNetAdmin.getConnection;
import static siinqee.net.admin.SiinqeeNetAdmin.setDateNow;

/**
 *
 * @author samimersha
 */
public class Main extends javax.swing.JFrame {

    public static String username = "<unknown>";
    public String dbstatus = "Not Busy";
    boolean informed = false;
    int activenumbers = 0, passivenumbers = 0;

    /**
     * Creates new form Main
     */
    public Main(String ag_fullname) {
        
        initComponents();
        fullNameLabel.setText(ag_fullname);
        setHomeActive();
        
        dbstatus = "Not Busy";
        prepareTable();
    }

    public void DoThis() {
        String cls = "setSessionPassive";

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "";

        } catch (Exception e) {
            System.out.println("Error Occured: " + cls);
            e.printStackTrace();
        }

    }

    public void scanBranchesStatus() {
        String cls = "scanBranchesStatus";
        int activeb = 0, passiveb = 0;
        String bName = "", bStatus = "", bIP = "";
        long ipResult = -1;

        QuickScan q = new QuickScan();
        String content = "Branch Name \t| Branch Status\t| Branch IP"
                + "\n---------------------------------------------";
        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "select * from siinqee.branch";
            ResultSet set = st.executeQuery(cmd);

            while (set.next()) {
                ipResult = PingIP(set.getString("br_ipaddress"));
                if (ipResult >= 0) {
                    activeb++;
                    bName = set.getString("br_name");
                    bStatus = "Active";
                    bIP = set.getString("br_ipaddress");
                    content += ("\n" + bName + " \t| " + bStatus + "\t| " + bIP);
                } else {
                    passiveb++;
                    bName = set.getString("br_name");
                    bStatus = "Passive";
                    bIP = set.getString("br_ipaddress");
                    content += ("\n" + bName + " \t| " + bStatus + "\t| " + bIP);
                }
            }

            System.out.println("Scanning Finished!");

            int result = JOptionPane.showConfirmDialog(new JFrame(), "Found " + activeb + " active branches "
                    + "\nand " + passiveb + " passive branches."
                    + "\nDo you want to print branches detail?", "Scan Result",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                q.textArea.setText(content);
                q.textArea.print();
            } else if (result == JOptionPane.NO_OPTION) {

            } else {

            }

        } catch (Exception e) {
            System.out.println("Error Occured: " + cls);
            e.printStackTrace();
        }

    }

    public void deepScan() {
        String cls = "deepScan";

        //need to borrow some methods
        QuickScan q = new QuickScan();

        //need gui
        DeepScan d = new DeepScan();
        d.setVisible(true);

        //Finding Total Number of Clients
        int clCode = 0, totalClients = countClients(), scannedClients = 0, activeclients = 0, passiveclients = 0, percent = 0;
        String clientName, clientBranchName, clientBranchCity, clientIP;
        long ipResult;

        String activeList = "Deep Scan Result"
                + "\nActive Computers List"
                + "\nName\t| IP Address\t| Branch\t| City";
        String passiveList = "\n----------------------------------"
                + "\nPassive Computers List"
                + "\nName\t| IP Address\t| Branch\t| City";

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "select * from siinqee.client";
            ResultSet set = st.executeQuery(cmd);

            while (set.next()) {
                clientIP = set.getString("cl_ipaddress");
                ipResult = PingIP(clientIP);
                System.out.println("IP Ping Result: " + ipResult);
                if (ipResult >= 0) {
                    activeclients++;
                    clientName = set.getString("cl_name");
                    clCode = set.getInt("cl_code");
                    clientBranchName = q.getClientBranchName(clCode);
                    clientBranchCity = q.getClientBranchCity(clCode);
                    setSessionActive(clCode);
                    activeList += "\n" + clientName + "\t| " + clientIP + "\t| " + clientBranchName + "\t| " + clientBranchCity;
                } else {
                    passiveclients++;
                    clientName = set.getString("cl_name");
                    clCode = set.getInt("cl_code");
                    clientBranchName = q.getClientBranchName(clCode);
                    clientBranchCity = q.getClientBranchCity(clCode);

                    passiveList += "\n" + clientName + "\t| " + clientIP + "\t| " + clientBranchName + "\t| " + clientBranchCity;
                }

                Thread.sleep(500);
                scannedClients++;

                percent = q.getPercent(scannedClients, totalClients);
                System.out.println("Deep scan Progress: " + percent + "%");
                d.progressBar.setValue(percent);
                d.percentLabel.setText(percent + "%");
                d.detailLabel.setText("Scanned Clients: " + scannedClients);
            }

            //after while
            String printable = activeList + passiveList;
            java.util.Date date = new java.util.Date();
            printable += "\nDate: " + date;

            int result = JOptionPane.showConfirmDialog(new JFrame(), "Found " + activeclients + " active clients "
                    + "\nand " + passiveclients + " passive clients."
                    + "\nDo you want to print passive clients detail?", "Deep Scan Result",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                q.textArea.setText(printable);
                q.textArea.print();
                d.dispose();

            } else if (result == JOptionPane.NO_OPTION) {
                d.dispose();
            } else {
                d.dispose();
            }

        } catch (Exception e) {
            System.out.println("Error Occured: " + cls);
            e.printStackTrace();
        }

    }

    public void setSessionActive(int clCode) {
        //to be done
        String cls = "setSessionActive";

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "update siinqee.client set cl_session = 'Active', cl_lastseen = '" + new java.util.Date() + "' where cl_code = " + clCode;
            st.executeUpdate(cmd);
        } catch (Exception e) {
            System.out.println("Error Occured: " + cls);
            e.printStackTrace();
        }
    }

    public int countClients() {
        String cls = "countClients";
        int totalClients = 0;

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            //count number of rows in client
            String query = "select count(*) from siinqee.client";
            //Executing the query
            ResultSet rs = st.executeQuery(query);
            //Retrieving the result
            rs.next();
            totalClients = rs.getInt(1);

        } catch (Exception e) {
            System.out.println("Error Occured: " + cls);
            e.printStackTrace();
        }

        return totalClients;
    }

    public void prepareTable() {
        TableColumnModel col = messageTable.getColumnModel();
        col.getColumn(0).setPreferredWidth(30);
        col.getColumn(1).setPreferredWidth(130);
        col.getColumn(2).setPreferredWidth(250);
        col.getColumn(3).setPreferredWidth(100);
        col.getColumn(4).setPreferredWidth(100);
        fillTheTable();
    }

    public void fillTheTable() {
        int tclCode = 0;
        String tclName = "", tclBranch = "", tdetail = "", tip = "";
        QuickScan q = new QuickScan();

        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();
            String cmd = "select * from siinqee.message";
            ResultSet rSet = st.executeQuery(cmd);
            int rollNo = 1;

            while (rSet.next()) {
                tclCode = rSet.getInt("me_clientcode");
                tclName = q.getClientName(tclCode);
                tclBranch = q.getClientBranchName(tclCode);
                tdetail = rSet.getString("me_description");
                tip = q.getClientIP(tclCode);

                String[] tableData = {rollNo + "", tclName, tdetail, tclBranch, tip};
                DefaultTableModel dtm = (DefaultTableModel) messageTable.getModel();
                dtm.addRow(tableData);
                rollNo++;
            }

            cn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), e);
        }
    }

    public void UpdateNotification() {
        warningNotification.setVisible(false);
        updateClientsActivity();
        if (passivenumbers == 0) {
            warningNotification.setVisible(false);
        } else {
            warningNotification.setVisible(true);
            warningLabel.setText(passivenumbers + " down clients exist.");
        }
    }

    public void updateClientsActivity() {

        activenumbers = 0;
        passivenumbers = 0;
        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "select * from siinqee.client";
            ResultSet set = st.executeQuery(cmd);

            while (set.next()) {
                if ("Active".equals(set.getString("cl_session"))) {
                    activenumbers++;
                } else {
                    passivenumbers++;
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println();
            e.printStackTrace();
        }
    }

    public boolean connected() {
        Socket sock = new Socket();
        InetSocketAddress adress = new InetSocketAddress("www.google.com", 80);
        boolean isConnected = false;
        try {
            sock.connect(adress, 3000);
            isConnected = true;
        } catch (Exception e) {
            System.out.println(e);
            isConnected = false;
        } finally {
            try {
                sock.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        return isConnected;
    }

    public void startUpdatingClients() {
        while (true && "Not Busy".equals(dbstatus)) {
            UpdateNotification();
            updateClients();

            try {
                Thread.sleep(60 * 1000);
            } catch (Exception e) {
                System.out.println("Error Occured");
                e.printStackTrace();
            }
        }
    }

    public void updateClients() {
        try {
            Connection con = getConnection();
            Statement st = con.createStatement();
            String cmd = "select * from siinqee.client";
            ResultSet rSet = st.executeQuery(cmd);
            String clLastSeen;
            java.util.Date lastSeenD;
            int lastSeenYear, lastSeenMonth, lastSeenDate, lastSeenHour, lastSeenMinute;
            int currentYear, currentMonth, currentDate, currentHour, currentMinute;
            while (rSet.next()) {
                //Last Seen Date of Individual Clients
                clLastSeen = rSet.getString("cl_lastseen");
                lastSeenD = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(clLastSeen);
                lastSeenYear = lastSeenD.getYear();
                lastSeenMonth = lastSeenD.getMonth();
                lastSeenDate = lastSeenD.getDate();
                lastSeenHour = lastSeenD.getHours();
                lastSeenMinute = lastSeenD.getMinutes();
                //Current Date
                currentYear = new java.util.Date().getYear();
                currentMonth = new java.util.Date().getMonth();
                currentDate = new java.util.Date().getDate();
                currentHour = new java.util.Date().getHours();
                currentMinute = new java.util.Date().getMinutes();
                //The logic
                if (lastSeenYear != currentYear) {
                    setSessionPassive(rSet.getInt("cl_code"));
                } else if (lastSeenMonth != currentMonth) {
                    setSessionPassive(rSet.getInt("cl_code"));
                } else if (lastSeenDate != currentDate) {
                    setSessionPassive(rSet.getInt("cl_code"));
                } else if (lastSeenHour != currentHour) {
                    setSessionPassive(rSet.getInt("cl_code"));
                } else if ((currentMinute - lastSeenMinute) > 2) {
                    setSessionPassive(rSet.getInt("cl_code"));
                }
            }
        } catch (Exception e) {
            System.out.println("Error occured!");
            e.printStackTrace();
        }
    }

    public void setSessionPassive(int clCode) {
        //to be done
        String cls = "setSessionPassive";

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "update siinqee.client set cl_session = 'Passive' where cl_code = " + clCode;
            st.executeUpdate(cmd);
        } catch (Exception e) {
            System.out.println("Error Occured: " + cls);
            e.printStackTrace();
        }
    }

    private long PingIP(String ipAddress) {
        int maxTime = 3000;
        long finishTime = 0, startTime = 0;

        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            startTime = System.currentTimeMillis();
            if (address.isReachable(maxTime)) {
                finishTime = System.currentTimeMillis();
            }
            return (finishTime - startTime);
        } catch (Exception e) {
            System.out.println(e);
        }
        return 0;
    }

    private void setAboutActive() {
        resetColor(HomeButton);
        resetColor(NetworkButton);
        resetColor(MessagesButton);
        resetColor(AccountsButton);
        setColor(AboutButton);
        HomePanel.setVisible(false);
        NetworkPanel.setVisible(false);
        MessagesPanel.setVisible(false);
        AccountsPanel.setVisible(false);
        AboutPanel.setVisible(true);
        NotificationsPanel.setVisible(true);
    }

    private void setAccountsActive() {
        resetColor(HomeButton);
        resetColor(NetworkButton);
        resetColor(MessagesButton);
        setColor(AccountsButton);
        resetColor(AboutButton);
        HomePanel.setVisible(false);
        NetworkPanel.setVisible(false);
        MessagesPanel.setVisible(false);
        AccountsPanel.setVisible(true);
        AboutPanel.setVisible(false);
        NotificationsPanel.setVisible(true);
    }

    private void setMessagesActive() {
        resetColor(HomeButton);
        resetColor(NetworkButton);
        setColor(MessagesButton);
        resetColor(AccountsButton);
        resetColor(AboutButton);
        HomePanel.setVisible(false);
        NetworkPanel.setVisible(false);
        MessagesPanel.setVisible(true);
        AccountsPanel.setVisible(false);
        AboutPanel.setVisible(false);
        NotificationsPanel.setVisible(false);
    }

    private void setNetworkActive() {
        resetColor(HomeButton);
        setColor(NetworkButton);
        resetColor(MessagesButton);
        resetColor(AccountsButton);
        resetColor(AboutButton);
        HomePanel.setVisible(false);
        NetworkPanel.setVisible(true);
        MessagesPanel.setVisible(false);
        AccountsPanel.setVisible(false);
        AboutPanel.setVisible(false);
        NotificationsPanel.setVisible(true);
    }

    private void setHomeActive() {
        setColor(HomeButton);
        resetColor(NetworkButton);
        resetColor(MessagesButton);
        resetColor(AccountsButton);
        resetColor(AboutButton);
        HomePanel.setVisible(true);
        NetworkPanel.setVisible(false);
        MessagesPanel.setVisible(false);
        AccountsPanel.setVisible(false);
        AboutPanel.setVisible(false);
        NotificationsPanel.setVisible(true);
    }

    private void setColor(JPanel jp) {
        jp.setBackground(new Color(255, 204, 153));
    }

    private void resetColor(JPanel jp) {
        jp.setBackground(new Color(255, 255, 255));
    }

    public int generateHisCode() {
        int amount = 1;
        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "select * from siinqee.history;";
            ResultSet set = st.executeQuery(cmd);
            while (set.next()) {
                amount++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        int his_code = amount + 1;
        return his_code;
    }

    public void addToLogOutHistory(String userName) {
        int his_code = generateHisCode();

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String dateNow = setDateNow();
            System.out.println(dateNow);
            String cmd = "insert into history values(" + his_code + ", 'Login Session', 'New Log Out', '" + userName + "', '" + dateNow + "')";
            st.executeUpdate(cmd);
            System.out.println("Log out History Saved!");
        } catch (Exception e) {
            System.out.println("Error Occured");
            e.printStackTrace();
        }
    }

    public void logOut() {
        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "update siinqee.admin set ad_session = 'Passive' where ad_username = \"" + username + "\"";
            int done = st.executeUpdate(cmd);
            System.out.println("Account Log Out Done!");
            if (done == 1) {
                addToLogOutHistory(username);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void addToRemovedAdminHistory(String userName) {
        int his_code = generateHisCode();

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String dateNow = setDateNow();
            System.out.println(dateNow);
            String cmd = "insert into history values(" + his_code + ", 'Removed Account', 'One Account Removed', '" + userName + "', '" + dateNow + "')";
            st.executeUpdate(cmd);
        } catch (Exception e) {
            System.out.println("Error Occured");
            e.printStackTrace();
        }
    }

    public void removeAdmin() {
        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();
            String command = "SELECT * FROM siinqee.admin";
            ResultSet set = st.executeQuery(command);

            int response = JOptionPane.showConfirmDialog(new JFrame(), " Do you want to delete your Account and log out of this session?", "Confirm to Delete", JOptionPane.YES_NO_CANCEL_OPTION);

            //0 means yes 1 means no 
            if (response == 0) {
                command = "DELETE FROM siinqee.admin WHERE ad_username = '" + username + "'";
                st.executeUpdate(command);
                addToRemovedAdminHistory(username);
                JOptionPane.showMessageDialog(new JFrame(), "Successfully deleted!");
                dispose();
                new Login().setVisible(true);
            } else if (response == 1) {
                JOptionPane.showMessageDialog(new JFrame(), "Account is not deleted!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), e, "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public void registerClientButton() {
        boolean valid;

        valid = false;
        String name = JOptionPane.showInputDialog(new JFrame(), "Enter Client's Name: ", "Client Registeration Form", JOptionPane.QUESTION_MESSAGE);
        while (valid != true) {
            if (name.isEmpty() || name.length() >= 35) {
                name = JOptionPane.showInputDialog(new JFrame(), "Please enter correct Client's Name: ", "Client Registeration Form", JOptionPane.ERROR_MESSAGE);
            } else {
                valid = true;
                break;
            }
        }

        valid = false;
        String branch = JOptionPane.showInputDialog(new JFrame(), "Enter Client's Branch: ", "Client Registeration Form", JOptionPane.QUESTION_MESSAGE);
        while (valid != true) {
            if (branch.isEmpty() || checkBranch(branch) == false) {
                branch = JOptionPane.showInputDialog(new JFrame(), "Please enter correct Branch's Name: ", "Client Registeration Form", JOptionPane.ERROR_MESSAGE);
            } else {
                valid = true;
                break;
            }
        }

        valid = false;
        String ip = JOptionPane.showInputDialog(new JFrame(), "Enter Client's IP Address: ", "Client Registeration Form", JOptionPane.QUESTION_MESSAGE);
        while (valid != true) {
            if (checkIP(ip) == false || ip.isEmpty()) {
                ip = JOptionPane.showInputDialog(new JFrame(), "Duplplicate found or Invalid IP!\nPlease configure and register new one!: ", "Client Registeration Form", JOptionPane.ERROR_MESSAGE);
            } else {
                valid = true;
                break;
            }
        }

        valid = false;
        String cname = JOptionPane.showInputDialog(new JFrame(), "Enter Client's computer name: ", "Client Registeration Form", JOptionPane.QUESTION_MESSAGE);
        while (valid != true) {
            if (cname.isEmpty() || cname.length() >= 35) {
                cname = JOptionPane.showInputDialog(new JFrame(), "Please enter correct computer name: ", "Client Registeration Form", JOptionPane.ERROR_MESSAGE);
            } else {
                valid = true;
                break;
            }
        }

        if (valid) {
            registerClient(name, getBranchCode(branch), ip, cname);
            JOptionPane.showMessageDialog(new JFrame(), "Successfully Registered!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void registerClient(String clName, int brCode, String clIp, String pcName) {
        String cls = "registerClient";
        int clCode = generateClientCode();
        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "insert into siinqee.client values(" + clCode + ", \"" + clName + "\", \"" + pcName + "\", \"" + clIp + "\", " + brCode + ", \"Dormant\", '" + new java.util.Date() + "')";
            st.executeUpdate(cmd);

        } catch (Exception e) {
            System.out.println("Error Occured: " + cls);
            e.printStackTrace();
        }

    }

    public int generateClientCode() {
        int amount = 0;
        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "select * from siinqee.client;";
            ResultSet set = st.executeQuery(cmd);
            while (set.next()) {
                amount++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        int cl_code = amount + 1;
        return cl_code;
    }

    public int getBranchCode(String brName) {
        String cls = "getClientBranchCode";
        int brCode = 0;

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "select * from siinqee.branch";

            ResultSet set = st.executeQuery(cmd);

            while (set.next()) {
                if (brName.equals(set.getString("br_name"))) {
                    brCode = set.getInt("br_code");
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Error Occured: " + cls);
            e.printStackTrace();
        }

        return brCode;
    }

    public boolean checkIP(String clIPAddress) {
        boolean isValid = true;

        JFrame jf = new JFrame();
        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();

            String cmd = "SELECT * FROM siinqee.client";
            ResultSet set = st.executeQuery(cmd);
            while (set.next()) {
                if (clIPAddress.equals(set.getString("cl_ipaddress"))) {
                    isValid = false;
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Error");
            e.printStackTrace();
        }

        return isValid;
    }

    public void registerBranchButton() {
        boolean valid;

        valid = false;
        String name = JOptionPane.showInputDialog(new JFrame(), "Enter Branchs's Name: ", "Branch Registeration Form", JOptionPane.QUESTION_MESSAGE);
        while (valid != true) {
            if (name.isEmpty() || name.length() >= 35) {
                name = JOptionPane.showInputDialog(new JFrame(), "Please enter correct Branch's Name: ", "Branch Registeration Form", JOptionPane.ERROR_MESSAGE);
            } else {
                valid = true;
                break;
            }
        }

        valid = false;
        String country = JOptionPane.showInputDialog(new JFrame(), "Enter Branch's Country: ", "Branch Registeration Form", JOptionPane.QUESTION_MESSAGE);
        while (valid != true) {
            if (country.isEmpty()) {
                country = JOptionPane.showInputDialog(new JFrame(), "Please enter correct country: ", "Branch Registeration Form", JOptionPane.ERROR_MESSAGE);
            } else {
                valid = true;
                break;
            }
        }

        valid = false;
        String region = JOptionPane.showInputDialog(new JFrame(), "Enter Branch's Region: ", "Branch Registeration Form", JOptionPane.QUESTION_MESSAGE);
        while (valid != true) {
            if (region.isEmpty()) {
                region = JOptionPane.showInputDialog(new JFrame(), "Please enter correct region: ", "Branch Registeration Form", JOptionPane.ERROR_MESSAGE);
            } else {
                valid = true;
                break;
            }
        }

        valid = false;
        String zone = JOptionPane.showInputDialog(new JFrame(), "Enter Branch's Zone: ", "Branch Registeration Form", JOptionPane.QUESTION_MESSAGE);
        while (valid != true) {
            if (zone.isEmpty()) {
                zone = JOptionPane.showInputDialog(new JFrame(), "Please enter correct zone: ", "Branch Registeration Form", JOptionPane.ERROR_MESSAGE);
            } else {
                valid = true;
                break;
            }
        }

        valid = false;
        String woreda = JOptionPane.showInputDialog(new JFrame(), "Enter Branch's woreda: ", "Branch Registeration Form", JOptionPane.QUESTION_MESSAGE);
        while (valid != true) {
            if (woreda.isEmpty()) {
                woreda = JOptionPane.showInputDialog(new JFrame(), "Please enter correct woreda: ", "Branch Registeration Form", JOptionPane.ERROR_MESSAGE);
            } else {
                valid = true;
                break;
            }
        }

        valid = false;
        String city = JOptionPane.showInputDialog(new JFrame(), "Enter Branch's city: ", "Branch Registeration Form", JOptionPane.QUESTION_MESSAGE);
        while (valid != true) {
            if (city.isEmpty()) {
                city = JOptionPane.showInputDialog(new JFrame(), "Please enter correct city: ", "Branch Registeration Form", JOptionPane.ERROR_MESSAGE);
            } else {
                valid = true;
                break;
            }
        }

        valid = false;
        String kebele = JOptionPane.showInputDialog(new JFrame(), "Enter Branch's kebele: ", "Branch Registeration Form", JOptionPane.QUESTION_MESSAGE);
        while (valid != true) {
            if (kebele.isEmpty()) {
                kebele = JOptionPane.showInputDialog(new JFrame(), "Please enter correct kebele: ", "Branch Registeration Form", JOptionPane.ERROR_MESSAGE);
            } else {
                valid = true;
                break;
            }
        }

        valid = false;
        String ip = JOptionPane.showInputDialog(new JFrame(), "Enter Branch's ip: ", "Branch Registeration Form", JOptionPane.QUESTION_MESSAGE);
        while (valid != true) {
            if (ip.isEmpty() || checkBranchIP(ip) == false) {
                ip = JOptionPane.showInputDialog(new JFrame(), "Please enter correct ip it might be taken: ", "Branch Registeration Form", JOptionPane.ERROR_MESSAGE);
            } else {
                valid = true;
                break;
            }
        }

        if (valid) {
            registerBranch(generateBranchCode(), name, country, region, zone, woreda, city, kebele, ip);
            JOptionPane.showMessageDialog(new JFrame(), "Branch Successfully Registered!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public boolean checkBranchIP(String ip) {
        String cls = "checkBranchIP";
        boolean valid = true;

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "select * from siinqee.branch";

            ResultSet set = st.executeQuery(cmd);

            while (set.next()) {
                if (ip.equals(set.getString("br_ipaddress"))) {
                    valid = false;
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Error Occured: " + cls);
            e.printStackTrace();
        }

        return valid;
    }

    public void registerBranch(int brCode, String name, String country, String region, String zone, String woreda, String city, String kebele, String ip) {
        String cls = "registerBranch";

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "insert into siinqee.branch values(" + brCode + ", \"" + name + "\", \"" + country + "\", \"" + region + "\", \"" + zone + "\", \"" + woreda + "\", \"" + city + "\", \"" + kebele + "\" ,'" + ip + "')";
            st.executeUpdate(cmd);

        } catch (Exception e) {
            System.out.println("Error Occured: " + cls);
            e.printStackTrace();
        }
    }

    public int generateBranchCode() {
        int amount = 0;
        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "select * from siinqee.branch;";
            ResultSet set = st.executeQuery(cmd);
            while (set.next()) {
                amount++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        int br_code = amount + 1;
        return br_code;
    }

    public boolean checkBranch(String brName) {
        boolean isValid = false;

        JFrame jf = new JFrame();

        if (brName.equals("Your branch here...") && informed == false) {
            JOptionPane.showMessageDialog(jf, "Please enter branch name in the given text box and Try Again!");
            informed = true;
        } else {
            try {
                Connection cn = getConnection();
                Statement st = cn.createStatement();

                String cmd = "SELECT * FROM siinqee.branch";
                ResultSet set = st.executeQuery(cmd);

                while (set.next()) {
                    if (brName.equals(set.getString("br_name"))) {
                        isValid = true;
                        break;
                    }
                }

                if (!isValid && informed == false) {
                    JOptionPane.showMessageDialog(jf, "Branch name not found!\nPlease correct your spelling and Try again!", "Register Error", JOptionPane.ERROR_MESSAGE);
                    informed = true;
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(jf, e);
            }
        }

        return isValid;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        kGradientPanel1 = new keeptoo.KGradientPanel();
        fullNameLabel = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        logOutLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        HomePanel = new javax.swing.JPanel();
        quickScanLabel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        pingIPPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        registerClientShortcutButton = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        testConnectivity = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        NetworkPanel = new javax.swing.JPanel();
        registerPanel = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        scanBranchesButton = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        deepScanButton = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        registerBranchButton = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        branchesStatus = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        clientstatusButton = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        MessagesPanel = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel26 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel24 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        messageTable = new javax.swing.JTable();
        AccountsPanel = new javax.swing.JPanel();
        allAccountsButton = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        createAccountButton = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        removeAdminAccountButton = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        editAccountButton = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        AboutPanel = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        TabsPanel = new javax.swing.JPanel();
        HomeButton = new javax.swing.JPanel();
        homeLabel = new javax.swing.JLabel();
        NetworkButton = new javax.swing.JPanel();
        NetworkLabel = new javax.swing.JLabel();
        MessagesButton = new javax.swing.JPanel();
        MessagesLabel = new javax.swing.JLabel();
        AccountsButton = new javax.swing.JPanel();
        AccountsLabel = new javax.swing.JLabel();
        AboutButton = new javax.swing.JPanel();
        AboutLabel = new javax.swing.JLabel();
        NotificationsPanel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        warningNotification = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        warningLabel2 = new javax.swing.JLabel();
        warningLabel = new javax.swing.JLabel();
        allgoodNotification = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        warningLabel3 = new javax.swing.JLabel();
        warningLabel1 = new javax.swing.JLabel();
        minimizeLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        kGradientPanel1.setkEndColor(new java.awt.Color(0, 167, 118));
        kGradientPanel1.setkStartColor(new java.awt.Color(255, 204, 0));
        kGradientPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        fullNameLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/user_24px.png"))); // NOI18N
        fullNameLabel.setText("USER NAME");
        kGradientPanel1.add(fullNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 20, 160, 30));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/close_window_30px.png"))); // NOI18N
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel10MousePressed(evt);
            }
        });
        kGradientPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 20, -1, -1));

        logOutLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/logout_rounded_left_30px.png"))); // NOI18N
        logOutLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                logOutLabelMousePressed(evt);
            }
        });
        kGradientPanel1.add(logOutLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 20, -1, -1));
        kGradientPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel2.setFont(new java.awt.Font("Uni Sans Regular", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Siinqee Network Adminstration Center");
        kGradientPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 60, 370, -1));

        HomePanel.setBackground(new java.awt.Color(204, 255, 204));

        quickScanLabel.setBackground(new java.awt.Color(255, 255, 255));
        quickScanLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                quickScanLabelMousePressed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel11.setText("     QUICK SCAN");

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/radar_100px.png"))); // NOI18N

        javax.swing.GroupLayout quickScanLabelLayout = new javax.swing.GroupLayout(quickScanLabel);
        quickScanLabel.setLayout(quickScanLabelLayout);
        quickScanLabelLayout.setHorizontalGroup(
            quickScanLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(quickScanLabelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(quickScanLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addGroup(quickScanLabelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        quickScanLabelLayout.setVerticalGroup(
            quickScanLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, quickScanLabelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pingIPPanel.setBackground(new java.awt.Color(255, 255, 255));
        pingIPPanel.setPreferredSize(new java.awt.Dimension(160, 29));
        pingIPPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pingIPPanelMousePressed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel9.setText("          CHECK IP");

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/checkconnected_100px.png"))); // NOI18N

        javax.swing.GroupLayout pingIPPanelLayout = new javax.swing.GroupLayout(pingIPPanel);
        pingIPPanel.setLayout(pingIPPanelLayout);
        pingIPPanelLayout.setHorizontalGroup(
            pingIPPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pingIPPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pingIPPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pingIPPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pingIPPanelLayout.setVerticalGroup(
            pingIPPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pingIPPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        registerClientShortcutButton.setBackground(new java.awt.Color(255, 255, 255));
        registerClientShortcutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                registerClientShortcutButtonMousePressed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel13.setText("     REGISTER CLIENT");

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/windows_client_100px.png"))); // NOI18N

        javax.swing.GroupLayout registerClientShortcutButtonLayout = new javax.swing.GroupLayout(registerClientShortcutButton);
        registerClientShortcutButton.setLayout(registerClientShortcutButtonLayout);
        registerClientShortcutButtonLayout.setHorizontalGroup(
            registerClientShortcutButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, registerClientShortcutButtonLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        registerClientShortcutButtonLayout.setVerticalGroup(
            registerClientShortcutButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, registerClientShortcutButtonLayout.createSequentialGroup()
                .addGap(0, 15, Short.MAX_VALUE)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        testConnectivity.setBackground(new java.awt.Color(255, 255, 255));
        testConnectivity.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                testConnectivityMousePressed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel12.setText("TEST CONNECTION");

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/wired_network_connection_100px.png"))); // NOI18N

        javax.swing.GroupLayout testConnectivityLayout = new javax.swing.GroupLayout(testConnectivity);
        testConnectivity.setLayout(testConnectivityLayout);
        testConnectivityLayout.setHorizontalGroup(
            testConnectivityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testConnectivityLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(testConnectivityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addGroup(testConnectivityLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        testConnectivityLayout.setVerticalGroup(
            testConnectivityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, testConnectivityLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout HomePanelLayout = new javax.swing.GroupLayout(HomePanel);
        HomePanel.setLayout(HomePanelLayout);
        HomePanelLayout.setHorizontalGroup(
            HomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HomePanelLayout.createSequentialGroup()
                .addGap(104, 104, 104)
                .addGroup(HomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pingIPPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(testConnectivity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(40, 40, 40)
                .addGroup(HomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(quickScanLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(registerClientShortcutButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(86, Short.MAX_VALUE))
        );
        HomePanelLayout.setVerticalGroup(
            HomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HomePanelLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(HomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(quickScanLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pingIPPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                .addGap(41, 41, 41)
                .addGroup(HomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(registerClientShortcutButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(testConnectivity, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        kGradientPanel1.add(HomePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(68, 90, 550, 390));

        NetworkPanel.setBackground(new java.awt.Color(204, 204, 255));
        NetworkPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        registerPanel.setBackground(new java.awt.Color(255, 255, 255));
        registerPanel.setPreferredSize(new java.awt.Dimension(160, 29));
        registerPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                registerPanelMousePressed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel20.setText("  REGISTER CLIENT");

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/windows_client_100px.png"))); // NOI18N

        javax.swing.GroupLayout registerPanelLayout = new javax.swing.GroupLayout(registerPanel);
        registerPanel.setLayout(registerPanelLayout);
        registerPanelLayout.setHorizontalGroup(
            registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(registerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, registerPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        registerPanelLayout.setVerticalGroup(
            registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, registerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        NetworkPanel.add(registerPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 30, 150, 150));

        scanBranchesButton.setBackground(new java.awt.Color(255, 255, 255));
        scanBranchesButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                scanBranchesButtonMousePressed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel24.setText("SCAN BRANCHES");

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/branchesradar_100px.png"))); // NOI18N

        javax.swing.GroupLayout scanBranchesButtonLayout = new javax.swing.GroupLayout(scanBranchesButton);
        scanBranchesButton.setLayout(scanBranchesButtonLayout);
        scanBranchesButtonLayout.setHorizontalGroup(
            scanBranchesButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scanBranchesButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(scanBranchesButtonLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel25)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        scanBranchesButtonLayout.setVerticalGroup(
            scanBranchesButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, scanBranchesButtonLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel24)
                .addGap(22, 22, 22))
        );

        NetworkPanel.add(scanBranchesButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, 140, 150));

        deepScanButton.setBackground(new java.awt.Color(255, 255, 255));
        deepScanButton.setPreferredSize(new java.awt.Dimension(160, 29));
        deepScanButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                deepScanButtonMousePressed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel22.setText("    DEEP SCAN");

        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/deepradar_100px.png"))); // NOI18N

        javax.swing.GroupLayout deepScanButtonLayout = new javax.swing.GroupLayout(deepScanButton);
        deepScanButton.setLayout(deepScanButtonLayout);
        deepScanButtonLayout.setHorizontalGroup(
            deepScanButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deepScanButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, deepScanButtonLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel23)
                .addGap(20, 20, 20))
        );
        deepScanButtonLayout.setVerticalGroup(
            deepScanButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, deepScanButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        NetworkPanel.add(deepScanButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 140, 150));

        registerBranchButton.setBackground(new java.awt.Color(255, 255, 255));
        registerBranchButton.setPreferredSize(new java.awt.Dimension(160, 29));
        registerBranchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                registerBranchButtonMousePressed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel26.setText("REGISTER BRANCH");

        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/registerlocation_100px.png"))); // NOI18N

        javax.swing.GroupLayout registerBranchButtonLayout = new javax.swing.GroupLayout(registerBranchButton);
        registerBranchButton.setLayout(registerBranchButtonLayout);
        registerBranchButtonLayout.setHorizontalGroup(
            registerBranchButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(registerBranchButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, registerBranchButtonLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        registerBranchButtonLayout.setVerticalGroup(
            registerBranchButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, registerBranchButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        NetworkPanel.add(registerBranchButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 220, 150, 150));

        branchesStatus.setBackground(new java.awt.Color(255, 255, 255));
        branchesStatus.setPreferredSize(new java.awt.Dimension(160, 29));
        branchesStatus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                branchesStatusMousePressed(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel32.setText("BRANCHES STATUS");

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/new_network_100px.png"))); // NOI18N

        javax.swing.GroupLayout branchesStatusLayout = new javax.swing.GroupLayout(branchesStatus);
        branchesStatus.setLayout(branchesStatusLayout);
        branchesStatusLayout.setHorizontalGroup(
            branchesStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, branchesStatusLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel33)
                .addGap(20, 20, 20))
        );
        branchesStatusLayout.setVerticalGroup(
            branchesStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, branchesStatusLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        NetworkPanel.add(branchesStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 220, 140, 150));

        clientstatusButton.setBackground(new java.awt.Color(255, 255, 255));
        clientstatusButton.setPreferredSize(new java.awt.Dimension(160, 29));
        clientstatusButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                clientstatusButtonMousePressed(evt);
            }
        });

        jLabel42.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel42.setText("  CLIENTS STATUS");

        jLabel43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/clientnetworking_manager_100px.png"))); // NOI18N

        javax.swing.GroupLayout clientstatusButtonLayout = new javax.swing.GroupLayout(clientstatusButton);
        clientstatusButton.setLayout(clientstatusButtonLayout);
        clientstatusButtonLayout.setHorizontalGroup(
            clientstatusButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, clientstatusButtonLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(jLabel43)
                .addGap(20, 20, 20))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, clientstatusButtonLayout.createSequentialGroup()
                .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        clientstatusButtonLayout.setVerticalGroup(
            clientstatusButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, clientstatusButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        NetworkPanel.add(clientstatusButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 30, 140, 150));

        kGradientPanel1.add(NetworkPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(68, 90, 550, 390));

        MessagesPanel.setBackground(new java.awt.Color(255, 211, 186));
        MessagesPanel.setPreferredSize(new java.awt.Dimension(774, 390));
        MessagesPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setPreferredSize(new java.awt.Dimension(160, 29));

        jLabel1.setFont(new java.awt.Font("Raleway SemiBold", 0, 18)); // NOI18N
        jLabel1.setText("Filter");

        jLabel3.setFont(new java.awt.Font("Raleway Medium", 0, 14)); // NOI18N
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/speed_50px.png"))); // NOI18N
        jLabel3.setText("Speed Issue");

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
        );

        jLabel6.setFont(new java.awt.Font("Raleway Medium", 0, 14)); // NOI18N
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/ethernet_off_50px.png"))); // NOI18N
        jLabel6.setText("Interruption");

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
        );

        jLabel7.setFont(new java.awt.Font("Raleway Medium", 0, 14)); // NOI18N
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/error_50px.png"))); // NOI18N
        jLabel7.setText("Physical Damage");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jLabel8.setFont(new java.awt.Font("Raleway Medium", 0, 14)); // NOI18N
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/ask_question_50px.png"))); // NOI18N
        jLabel8.setText("Others");

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel24, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel26, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        MessagesPanel.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 200, 368));

        messageTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Client Name", "Detail", "Branch", "IP Address"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(messageTable);

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
        );

        MessagesPanel.add(jPanel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(214, 11, 550, 368));

        kGradientPanel1.add(MessagesPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(68, 90, -1, 390));

        AccountsPanel.setBackground(new java.awt.Color(204, 233, 239));

        allAccountsButton.setBackground(new java.awt.Color(255, 255, 255));
        allAccountsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                allAccountsButtonMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                allAccountsButtonMousePressed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel34.setText("     ALL ACCOUNTS");

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/user_groups_100px.png"))); // NOI18N

        javax.swing.GroupLayout allAccountsButtonLayout = new javax.swing.GroupLayout(allAccountsButton);
        allAccountsButton.setLayout(allAccountsButtonLayout);
        allAccountsButtonLayout.setHorizontalGroup(
            allAccountsButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(allAccountsButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(allAccountsButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(allAccountsButtonLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        allAccountsButtonLayout.setVerticalGroup(
            allAccountsButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, allAccountsButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        createAccountButton.setBackground(new java.awt.Color(255, 255, 255));
        createAccountButton.setPreferredSize(new java.awt.Dimension(160, 29));
        createAccountButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                createAccountButtonMousePressed(evt);
            }
        });

        jLabel36.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel36.setText(" CREATE ACCOUNT");

        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/add_user_male_100px.png"))); // NOI18N

        javax.swing.GroupLayout createAccountButtonLayout = new javax.swing.GroupLayout(createAccountButton);
        createAccountButton.setLayout(createAccountButtonLayout);
        createAccountButtonLayout.setHorizontalGroup(
            createAccountButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createAccountButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(createAccountButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(createAccountButtonLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        createAccountButtonLayout.setVerticalGroup(
            createAccountButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, createAccountButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        removeAdminAccountButton.setBackground(new java.awt.Color(255, 255, 255));
        removeAdminAccountButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                removeAdminAccountButtonMousePressed(evt);
            }
        });

        jLabel38.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel38.setText("   REMOVE ACCOUNT");

        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/remove_100px.png"))); // NOI18N

        javax.swing.GroupLayout removeAdminAccountButtonLayout = new javax.swing.GroupLayout(removeAdminAccountButton);
        removeAdminAccountButton.setLayout(removeAdminAccountButtonLayout);
        removeAdminAccountButtonLayout.setHorizontalGroup(
            removeAdminAccountButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, removeAdminAccountButtonLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        removeAdminAccountButtonLayout.setVerticalGroup(
            removeAdminAccountButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, removeAdminAccountButtonLayout.createSequentialGroup()
                .addGap(0, 15, Short.MAX_VALUE)
                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        editAccountButton.setBackground(new java.awt.Color(255, 255, 255));
        editAccountButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                editAccountButtonMousePressed(evt);
            }
        });

        jLabel40.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel40.setText("    EDIT ACCOUNT");

        jLabel41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/edit_100px.png"))); // NOI18N

        javax.swing.GroupLayout editAccountButtonLayout = new javax.swing.GroupLayout(editAccountButton);
        editAccountButton.setLayout(editAccountButtonLayout);
        editAccountButtonLayout.setHorizontalGroup(
            editAccountButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editAccountButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editAccountButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addGroup(editAccountButtonLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        editAccountButtonLayout.setVerticalGroup(
            editAccountButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editAccountButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout AccountsPanelLayout = new javax.swing.GroupLayout(AccountsPanel);
        AccountsPanel.setLayout(AccountsPanelLayout);
        AccountsPanelLayout.setHorizontalGroup(
            AccountsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AccountsPanelLayout.createSequentialGroup()
                .addGap(104, 104, 104)
                .addGroup(AccountsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(createAccountButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editAccountButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(34, 34, 34)
                .addGroup(AccountsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(allAccountsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(removeAdminAccountButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(92, Short.MAX_VALUE))
        );
        AccountsPanelLayout.setVerticalGroup(
            AccountsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AccountsPanelLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(AccountsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(allAccountsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(createAccountButton, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                .addGap(38, 38, 38)
                .addGroup(AccountsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(removeAdminAccountButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editAccountButton, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        kGradientPanel1.add(AccountsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(68, 90, 550, 390));

        AboutPanel.setBackground(new java.awt.Color(204, 255, 204));

        jPanel19.setBackground(new java.awt.Color(255, 255, 255));
        jPanel19.setPreferredSize(new java.awt.Dimension(160, 29));

        jLabel44.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel44.setText("About");

        jLabel45.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel45.setText("Siinqee Bank, 2023 ");

        jLabel46.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel46.setText("Samuel Mersha");

        jLabel47.setFont(new java.awt.Font("Raleway SemiBold", 0, 14)); // NOI18N
        jLabel47.setText("Developer");

        jLabel48.setFont(new java.awt.Font("Raleway SemiBold", 0, 14)); // NOI18N
        jLabel48.setText("Developer Role");

        jLabel49.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel49.setText("Electrical & Comp. Engr. Intern");

        jLabel50.setFont(new java.awt.Font("Raleway SemiBold", 0, 14)); // NOI18N
        jLabel50.setText("Company");

        jLabel51.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel51.setText("Siinqee Bank");

        jLabel52.setFont(new java.awt.Font("Raleway SemiBold", 0, 14)); // NOI18N
        jLabel52.setText("Dedicated to");

        jLabel53.setFont(new java.awt.Font("Raleway", 0, 14)); // NOI18N
        jLabel53.setText("Siinqee Network Operators");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel45, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addComponent(jLabel47, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                    .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                    .addComponent(jLabel52, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel46, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel49, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel51, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel53, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addComponent(jLabel47)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel46)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel48)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel49)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel50)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel51)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel52)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel53)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout AboutPanelLayout = new javax.swing.GroupLayout(AboutPanel);
        AboutPanel.setLayout(AboutPanelLayout);
        AboutPanelLayout.setHorizontalGroup(
            AboutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AboutPanelLayout.createSequentialGroup()
                .addGap(104, 104, 104)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(106, Short.MAX_VALUE))
        );
        AboutPanelLayout.setVerticalGroup(
            AboutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AboutPanelLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        kGradientPanel1.add(AboutPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(68, 90, 550, 390));

        TabsPanel.setBackground(new java.awt.Color(0, 102, 102));
        TabsPanel.setForeground(new java.awt.Color(255, 255, 255));

        HomeButton.setBackground(new java.awt.Color(255, 204, 153));
        HomeButton.setForeground(new java.awt.Color(255, 255, 255));
        HomeButton.setPreferredSize(new java.awt.Dimension(150, 0));
        HomeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                HomeButtonMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                HomeButtonMousePressed(evt);
            }
        });

        homeLabel.setFont(new java.awt.Font("UniSansRegular", 1, 18)); // NOI18N
        homeLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/home_30px.png"))); // NOI18N
        homeLabel.setText("Home");
        homeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                homeLabelMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                homeLabelMousePressed(evt);
            }
        });

        javax.swing.GroupLayout HomeButtonLayout = new javax.swing.GroupLayout(HomeButton);
        HomeButton.setLayout(HomeButtonLayout);
        HomeButtonLayout.setHorizontalGroup(
            HomeButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HomeButtonLayout.createSequentialGroup()
                .addGap(0, 34, Short.MAX_VALUE)
                .addComponent(homeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        HomeButtonLayout.setVerticalGroup(
            HomeButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(homeLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        NetworkButton.setBackground(new java.awt.Color(255, 255, 255));
        NetworkButton.setForeground(new java.awt.Color(255, 255, 255));
        NetworkButton.setPreferredSize(new java.awt.Dimension(150, 37));
        NetworkButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NetworkButtonMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                NetworkButtonMousePressed(evt);
            }
        });

        NetworkLabel.setFont(new java.awt.Font("UniSansRegular", 1, 18)); // NOI18N
        NetworkLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/network_30px.png"))); // NOI18N
        NetworkLabel.setText("Network");
        NetworkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NetworkLabelMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                NetworkLabelMousePressed(evt);
            }
        });

        javax.swing.GroupLayout NetworkButtonLayout = new javax.swing.GroupLayout(NetworkButton);
        NetworkButton.setLayout(NetworkButtonLayout);
        NetworkButtonLayout.setHorizontalGroup(
            NetworkButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, NetworkButtonLayout.createSequentialGroup()
                .addGap(0, 18, Short.MAX_VALUE)
                .addComponent(NetworkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        NetworkButtonLayout.setVerticalGroup(
            NetworkButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(NetworkLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        MessagesButton.setBackground(new java.awt.Color(255, 255, 255));
        MessagesButton.setForeground(new java.awt.Color(255, 255, 255));
        MessagesButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MessagesButtonMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                MessagesButtonMousePressed(evt);
            }
        });

        MessagesLabel.setFont(new java.awt.Font("UniSansRegular", 1, 18)); // NOI18N
        MessagesLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/speech_bubble_30px.png"))); // NOI18N
        MessagesLabel.setText("Messages");
        MessagesLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MessagesLabelMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                MessagesLabelMousePressed(evt);
            }
        });

        javax.swing.GroupLayout MessagesButtonLayout = new javax.swing.GroupLayout(MessagesButton);
        MessagesButton.setLayout(MessagesButtonLayout);
        MessagesButtonLayout.setHorizontalGroup(
            MessagesButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MessagesButtonLayout.createSequentialGroup()
                .addGap(0, 19, Short.MAX_VALUE)
                .addComponent(MessagesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        MessagesButtonLayout.setVerticalGroup(
            MessagesButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MessagesLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        AccountsButton.setBackground(new java.awt.Color(255, 255, 255));
        AccountsButton.setForeground(new java.awt.Color(255, 255, 255));
        AccountsButton.setPreferredSize(new java.awt.Dimension(150, 0));
        AccountsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AccountsButtonMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                AccountsButtonMousePressed(evt);
            }
        });

        AccountsLabel.setFont(new java.awt.Font("UniSansRegular", 1, 18)); // NOI18N
        AccountsLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/user_shield_30px.png"))); // NOI18N
        AccountsLabel.setText("Accounts");
        AccountsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AccountsLabelMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                AccountsLabelMousePressed(evt);
            }
        });

        javax.swing.GroupLayout AccountsButtonLayout = new javax.swing.GroupLayout(AccountsButton);
        AccountsButton.setLayout(AccountsButtonLayout);
        AccountsButtonLayout.setHorizontalGroup(
            AccountsButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AccountsButtonLayout.createSequentialGroup()
                .addGap(0, 19, Short.MAX_VALUE)
                .addComponent(AccountsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        AccountsButtonLayout.setVerticalGroup(
            AccountsButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(AccountsLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        AboutButton.setBackground(new java.awt.Color(255, 255, 255));
        AboutButton.setForeground(new java.awt.Color(255, 255, 255));
        AboutButton.setPreferredSize(new java.awt.Dimension(150, 0));
        AboutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AboutButtonMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                AboutButtonMousePressed(evt);
            }
        });

        AboutLabel.setFont(new java.awt.Font("UniSansRegular", 1, 18)); // NOI18N
        AboutLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/info_30px.png"))); // NOI18N
        AboutLabel.setText("About");
        AboutLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AboutLabelMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                AboutLabelMousePressed(evt);
            }
        });

        javax.swing.GroupLayout AboutButtonLayout = new javax.swing.GroupLayout(AboutButton);
        AboutButton.setLayout(AboutButtonLayout);
        AboutButtonLayout.setHorizontalGroup(
            AboutButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AboutButtonLayout.createSequentialGroup()
                .addGap(0, 19, Short.MAX_VALUE)
                .addComponent(AboutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        AboutButtonLayout.setVerticalGroup(
            AboutButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(AboutLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout TabsPanelLayout = new javax.swing.GroupLayout(TabsPanel);
        TabsPanel.setLayout(TabsPanelLayout);
        TabsPanelLayout.setHorizontalGroup(
            TabsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TabsPanelLayout.createSequentialGroup()
                .addComponent(HomeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(NetworkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(MessagesButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AccountsButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AboutButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        TabsPanelLayout.setVerticalGroup(
            TabsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(HomeButton, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
            .addComponent(NetworkButton, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
            .addComponent(MessagesButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(AccountsButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
            .addComponent(AboutButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        kGradientPanel1.add(TabsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(71, 492, -1, 40));

        NotificationsPanel.setBackground(new java.awt.Color(255, 255, 255));
        NotificationsPanel.setName(""); // NOI18N
        NotificationsPanel.setPreferredSize(new java.awt.Dimension(220, 391));
        NotificationsPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBackground(new java.awt.Color(255, 204, 153));

        jLabel5.setFont(new java.awt.Font("Uni Sans Regular", 1, 14)); // NOI18N
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/popup_30px.png"))); // NOI18N
        jLabel5.setText("Notifications");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addComponent(jLabel5)
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        NotificationsPanel.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 230, 60));
        NotificationsPanel.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 288, 220, -1));
        NotificationsPanel.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 170, 220, -1));

        warningNotification.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel28.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/warning_30px.png"))); // NOI18N
        jLabel28.setText("Warning");
        warningNotification.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, -1, -1));
        warningNotification.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 200, 10));

        warningLabel2.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        warningLabel2.setText("Please check them out!");
        warningNotification.add(warningLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 200, -1));

        warningLabel.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        warningLabel.setText("Some Computer are down.");
        warningNotification.add(warningLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 200, -1));

        NotificationsPanel.add(warningNotification, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 220, 110));

        allgoodNotification.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel29.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/ok_30px.png"))); // NOI18N
        jLabel29.setText("All Good");
        allgoodNotification.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, -1, -1));
        allgoodNotification.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 200, 10));

        warningLabel3.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        warningLabel3.setText("Enjoy your time!");
        allgoodNotification.add(warningLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 200, -1));

        warningLabel1.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        warningLabel1.setText("All Client Computers are up.");
        allgoodNotification.add(warningLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 200, -1));

        NotificationsPanel.add(allgoodNotification, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 220, 110));

        kGradientPanel1.add(NotificationsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(625, 91, 220, 390));

        minimizeLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/minimize_window_30px.png"))); // NOI18N
        minimizeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                minimizeLabelMousePressed(evt);
            }
        });
        kGradientPanel1.add(minimizeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 20, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 910, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        dispose();
        System.exit(1);
    }//GEN-LAST:event_jLabel10MouseClicked

    private void HomeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HomeButtonMouseClicked
        setHomeActive();
    }//GEN-LAST:event_HomeButtonMouseClicked

    private void NetworkButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_NetworkButtonMouseClicked
        setNetworkActive();
    }//GEN-LAST:event_NetworkButtonMouseClicked

    private void MessagesButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MessagesButtonMouseClicked
        setMessagesActive();
    }//GEN-LAST:event_MessagesButtonMouseClicked

    private void AccountsButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AccountsButtonMouseClicked
        setAccountsActive();
    }//GEN-LAST:event_AccountsButtonMouseClicked

    private void AboutButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AboutButtonMouseClicked
        setAboutActive();
    }//GEN-LAST:event_AboutButtonMouseClicked

    private void homeLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeLabelMouseClicked
        setHomeActive();
    }//GEN-LAST:event_homeLabelMouseClicked

    private void NetworkLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_NetworkLabelMouseClicked
        setNetworkActive();
    }//GEN-LAST:event_NetworkLabelMouseClicked

    private void MessagesLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MessagesLabelMouseClicked
        setMessagesActive();
    }//GEN-LAST:event_MessagesLabelMouseClicked

    private void AccountsLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AccountsLabelMouseClicked
        setAccountsActive();
    }//GEN-LAST:event_AccountsLabelMouseClicked

    private void AboutLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AboutLabelMouseClicked
        setAboutActive();
    }//GEN-LAST:event_AboutLabelMouseClicked

    private void HomeButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HomeButtonMousePressed
        setHomeActive();
    }//GEN-LAST:event_HomeButtonMousePressed

    private void homeLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeLabelMousePressed
        setHomeActive();
    }//GEN-LAST:event_homeLabelMousePressed

    private void jLabel10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MousePressed
        dispose();
        System.exit(1);
    }//GEN-LAST:event_jLabel10MousePressed

    private void NetworkButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_NetworkButtonMousePressed
        setNetworkActive();
    }//GEN-LAST:event_NetworkButtonMousePressed

    private void NetworkLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_NetworkLabelMousePressed
        setNetworkActive();
    }//GEN-LAST:event_NetworkLabelMousePressed

    private void MessagesButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MessagesButtonMousePressed
        setMessagesActive();
    }//GEN-LAST:event_MessagesButtonMousePressed

    private void MessagesLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MessagesLabelMousePressed
        setMessagesActive();
    }//GEN-LAST:event_MessagesLabelMousePressed

    private void AccountsButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AccountsButtonMousePressed
        setAccountsActive();
    }//GEN-LAST:event_AccountsButtonMousePressed

    private void AccountsLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AccountsLabelMousePressed
        setAccountsActive();
    }//GEN-LAST:event_AccountsLabelMousePressed

    private void AboutButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AboutButtonMousePressed
        setAboutActive();
    }//GEN-LAST:event_AboutButtonMousePressed

    private void AboutLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AboutLabelMousePressed
        setAboutActive();
    }//GEN-LAST:event_AboutLabelMousePressed

    private void pingIPPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pingIPPanelMousePressed
        try {
            String ipAddress = null;
            ipAddress = JOptionPane.showInputDialog(new JFrame(), "Enter IP Address: ");
            long pingLatencyTime = 0;

            if (ipAddress.isEmpty()) {
                JOptionPane.showMessageDialog(new JFrame(), "Empty IP!\nPlease fill ip prompted and try again!");
            } else {
                pingLatencyTime = PingIP(ipAddress);
            }

            if (pingLatencyTime > 0) {
                JOptionPane.showMessageDialog(new JFrame(), "It is working!\nPing Latency Time: " + pingLatencyTime + " ms");
            } else if (!ipAddress.isEmpty()) {
                JOptionPane.showMessageDialog(new JFrame(), "This IP is not responding!");
            }
        } catch (Exception e) {
            System.err.println(e);
        }

    }//GEN-LAST:event_pingIPPanelMousePressed

    private void quickScanLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_quickScanLabelMousePressed
        QuickScan q = new QuickScan();
        q.setVisible(true);
        q.scanDatabase();
    }//GEN-LAST:event_quickScanLabelMousePressed

    private void createAccountButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_createAccountButtonMousePressed
        new RegisterAdmin().setVisible(true);
    }//GEN-LAST:event_createAccountButtonMousePressed

    private void allAccountsButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_allAccountsButtonMouseClicked
        new ListAccounts().setVisible(true);
    }//GEN-LAST:event_allAccountsButtonMouseClicked

    private void allAccountsButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_allAccountsButtonMousePressed
        new ListAccounts().setVisible(true);
    }//GEN-LAST:event_allAccountsButtonMousePressed

    private void editAccountButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editAccountButtonMousePressed
        EditAdminAccount ea = new EditAdminAccount();
        ea.fillAdminData(username);
        ea.setVisible(true);
    }//GEN-LAST:event_editAccountButtonMousePressed

    private void removeAdminAccountButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_removeAdminAccountButtonMousePressed
        removeAdmin();
    }//GEN-LAST:event_removeAdminAccountButtonMousePressed

    private void logOutLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logOutLabelMousePressed
        int response = JOptionPane.showConfirmDialog(new JFrame(), " Do you want to log out?", "Confirm to Log Out", JOptionPane.YES_NO_CANCEL_OPTION);

        //0 means yes 1 means no 
        if (response == 0) {
            logOut();
            dispose();
            new Login().setVisible(true);
        }

    }//GEN-LAST:event_logOutLabelMousePressed

    private void minimizeLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minimizeLabelMousePressed
        this.setExtendedState(Main.ICONIFIED);
    }//GEN-LAST:event_minimizeLabelMousePressed

    private void testConnectivityMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_testConnectivityMousePressed
        if (connected()) {
            JOptionPane.showMessageDialog(new JFrame(), "Your Internet is working!");
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Your Internet is not connected!\nPlease look out for possible solution!");
        }
    }//GEN-LAST:event_testConnectivityMousePressed

    private void registerClientShortcutButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_registerClientShortcutButtonMousePressed
        registerClientButton();
    }//GEN-LAST:event_registerClientShortcutButtonMousePressed

    private void registerPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_registerPanelMousePressed
        registerClientButton();
    }//GEN-LAST:event_registerPanelMousePressed

    private void registerBranchButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_registerBranchButtonMousePressed
        registerBranchButton();
    }//GEN-LAST:event_registerBranchButtonMousePressed

    private void clientstatusButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clientstatusButtonMousePressed
        new ClientStatus().setVisible(true);
    }//GEN-LAST:event_clientstatusButtonMousePressed

    private void deepScanButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deepScanButtonMousePressed
        JOptionPane.showMessageDialog(new JFrame(), "This might take some time please be patient!", "Info", JOptionPane.INFORMATION_MESSAGE);
        deepScan();
    }//GEN-LAST:event_deepScanButtonMousePressed

    private void branchesStatusMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_branchesStatusMousePressed
        JOptionPane.showMessageDialog(new JFrame(), "This might take a while. Please be patient!", "Scan Info", JOptionPane.INFORMATION_MESSAGE);
        new BranchesStatus().setVisible(true);
    }//GEN-LAST:event_branchesStatusMousePressed

    private void scanBranchesButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scanBranchesButtonMousePressed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(new JFrame(), "This might take a while. Please be patient!", "Branches Status", JOptionPane.INFORMATION_MESSAGE);
        scanBranchesStatus();
    }//GEN-LAST:event_scanBranchesButtonMousePressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AboutButton;
    private javax.swing.JLabel AboutLabel;
    private javax.swing.JPanel AboutPanel;
    private javax.swing.JPanel AccountsButton;
    private javax.swing.JLabel AccountsLabel;
    private javax.swing.JPanel AccountsPanel;
    private javax.swing.JPanel HomeButton;
    private javax.swing.JPanel HomePanel;
    private javax.swing.JPanel MessagesButton;
    private javax.swing.JLabel MessagesLabel;
    private javax.swing.JPanel MessagesPanel;
    private javax.swing.JPanel NetworkButton;
    private javax.swing.JLabel NetworkLabel;
    private javax.swing.JPanel NetworkPanel;
    private javax.swing.JPanel NotificationsPanel;
    private javax.swing.JPanel TabsPanel;
    private javax.swing.JPanel allAccountsButton;
    private javax.swing.JPanel allgoodNotification;
    private javax.swing.JPanel branchesStatus;
    private javax.swing.JPanel clientstatusButton;
    private javax.swing.JPanel createAccountButton;
    private javax.swing.JPanel deepScanButton;
    private javax.swing.JPanel editAccountButton;
    private javax.swing.JLabel fullNameLabel;
    private javax.swing.JLabel homeLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private keeptoo.KGradientPanel kGradientPanel1;
    private javax.swing.JLabel logOutLabel;
    private javax.swing.JTable messageTable;
    private javax.swing.JLabel minimizeLabel;
    private javax.swing.JPanel pingIPPanel;
    private javax.swing.JPanel quickScanLabel;
    private javax.swing.JPanel registerBranchButton;
    private javax.swing.JPanel registerClientShortcutButton;
    private javax.swing.JPanel registerPanel;
    private javax.swing.JPanel removeAdminAccountButton;
    private javax.swing.JPanel scanBranchesButton;
    private javax.swing.JPanel testConnectivity;
    private javax.swing.JLabel warningLabel;
    private javax.swing.JLabel warningLabel1;
    private javax.swing.JLabel warningLabel2;
    private javax.swing.JLabel warningLabel3;
    private javax.swing.JPanel warningNotification;
    // End of variables declaration//GEN-END:variables
}
