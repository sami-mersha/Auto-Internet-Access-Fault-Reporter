/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package siinqee.net.admin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static siinqee.net.admin.SiinqeeNetAdmin.getConnection;

/**
 *
 * @author samimersha
 */
public class QuickScan extends javax.swing.JFrame {

    int scannedClients = 0, totalClients = 0, activenumber = 0, passivenumber = 0;

    /**
     * Creates new form QuickScan
     */
    public QuickScan() {
        initComponents();
    }

    public void scanDatabase() {

        String cls = "scanning Database";
        //JOptionPane.showMessageDialog(new JFrame(), "Scanning Please Wait!", "Scan Progress", JOptionPane.DEFAULT_OPTION);
        System.out.println(cls);
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

            //
            String content = checkAllClients(totalClients);

            int result = JOptionPane.showConfirmDialog(new JFrame(), "Found " + activenumber + " active clients "
                    + "\nand " + passivenumber + " passive clients."
                    + "\nDo you want to print passive clients detail?", "Scan Result",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                textArea.setText(content);
                textArea.print();
                dispose();
            } else if (result == JOptionPane.NO_OPTION) {
                dispose();
            } else {
                dispose();
            }
            //
        } catch (Exception e) {
            System.out.println("Error Occured: " + cls);
            e.printStackTrace();
        }
    }

    public String checkAllClients(int totalClients) {
        String clientName, clientBranchName, clientBranchCity, clientIP;
        boolean isActive = true;

        String printable = "Passive Computers List"
                + "\nName\t| IP Address\t| Branch\t| City";

        int percent = 0;
        int clCode = 1;
        while (scannedClients < totalClients) {
            isActive = checkClientActivity(clCode);
            if (isActive) {
                activenumber++;
            } else {
                passivenumber++;
                clientName = getClientName(clCode);
                clientIP = getClientIP(clCode);
                clientBranchName = getClientBranchName(clCode);
                clientBranchCity = getClientBranchCity(clCode);
                printable += "\n" + clientName + "\t| " + clientIP + "\t| " + clientBranchName + "\t| " + clientBranchCity;
            }
            scannedClients++;

            try {
                Thread.sleep(50);
            } catch (Exception e) {
                System.out.println("Error Occured: Thread Sleep");
                e.printStackTrace();
            }

            percent = getPercent(scannedClients, totalClients);
            System.out.println("Progress: " + percent + "%");
            progressBar.setValue(percent);
            percentLabel.setText(percent + "%");
            detailLabel.setText("Scanned Clients: " + scannedClients);

            clCode++;
        }
        java.util.Date date = new java.util.Date();
        printable += "\nDate: " + date;

        return printable;
    }

    public int getPercent(int a, int b) {
        int percent;
        float first = a, second = b, result;
        result = (first / second) * 100;
        percent = (int) result;

        return percent;
    }

    public String getClientBranchCity(int clCode) {
        String cls = "getClientBranchCity";
        String branchCity = "<unknown>";
        int brCode = getClientBranchCode(clCode);

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "select * from siinqee.branch";

            ResultSet set = st.executeQuery(cmd);

            while (set.next()) {
                if (brCode == set.getInt("br_code")) {
                    branchCity = set.getString("br_city");
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Error Occured: " + cls);
            e.printStackTrace();
        }

        return branchCity;
    }

    public String getClientBranchName(int clCode) {
        String cls = "getClientBranchName";
        String branchName = "<unknown>";
        int brCode = getClientBranchCode(clCode);

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "select * from siinqee.branch";

            ResultSet set = st.executeQuery(cmd);

            while (set.next()) {
                if (brCode == set.getInt("br_code")) {
                    branchName = set.getString("br_name");
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Error Occured: " + cls);
            e.printStackTrace();
        }

        return branchName;
    }

    public int getClientBranchCode(int clCode) {
        String cls = "getClientBranchCode";
        int brCode = 0;

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "select * from siinqee.client";

            ResultSet set = st.executeQuery(cmd);

            while (set.next()) {
                if (clCode == set.getInt("cl_code")) {
                    brCode = set.getInt("cl_branchcode");
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Error Occured: " + cls);
            e.printStackTrace();
        }

        return brCode;
    }

    public String getClientIP(int clCode) {
        String cls = "getClientIP";
        String ip = "<unknown>";

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "select * from siinqee.client";

            ResultSet set = st.executeQuery(cmd);

            while (set.next()) {
                if (clCode == set.getInt("cl_code")) {
                    ip = set.getString("cl_ipaddress");
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Error Occured: " + cls);
            e.printStackTrace();
        }

        return ip;
    }

    public String getClientName(int clCode) {
        String cls = "getClientName";
        String fullName = "<unknown>";

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "select * from siinqee.client";

            ResultSet set = st.executeQuery(cmd);

            while (set.next()) {
                if (clCode == set.getInt("cl_code")) {
                    fullName = set.getString("cl_name");
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Error Occured: " + cls);
            e.printStackTrace();
        }

        return fullName;
    }

    public boolean checkClientActivity(int clCode) {
        boolean active = false;

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "select * from siinqee.client";
            ResultSet set = st.executeQuery(cmd);

            while (set.next()) {
                if (clCode == set.getInt("cl_code")) {
                    if ("Active".equals(set.getString("cl_session"))) {
                        active = true;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println();
            e.printStackTrace();
        }

        return active;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        textAreaScrollPane = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        kGradientPanel1 = new keeptoo.KGradientPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        detailLabel = new javax.swing.JLabel();
        percentLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        textArea.setColumns(20);
        textArea.setRows(5);
        textAreaScrollPane.setViewportView(textArea);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        kGradientPanel1.setkEndColor(new java.awt.Color(51, 204, 255));
        kGradientPanel1.setkStartColor(new java.awt.Color(204, 255, 102));
        kGradientPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/radar_100px.png"))); // NOI18N
        kGradientPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 124, 104));

        jLabel2.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/cancel_25px.png"))); // NOI18N
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel2MousePressed(evt);
            }
        });
        kGradientPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(516, 0, -1, 45));

        detailLabel.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        detailLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/2info_30px.png"))); // NOI18N
        detailLabel.setText("Please Wait!");
        kGradientPanel1.add(detailLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 130, 380, 30));

        percentLabel.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        percentLabel.setText("0%");
        kGradientPanel1.add(percentLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(487, 130, 60, 30));

        getContentPane().add(kGradientPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 551, 160));
        getContentPane().add(progressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 160, 551, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MousePressed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_jLabel2MousePressed

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
            java.util.logging.Logger.getLogger(QuickScan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QuickScan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QuickScan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QuickScan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel detailLabel;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private keeptoo.KGradientPanel kGradientPanel1;
    public javax.swing.JLabel percentLabel;
    public javax.swing.JProgressBar progressBar;
    public javax.swing.JTextArea textArea;
    public javax.swing.JScrollPane textAreaScrollPane;
    // End of variables declaration//GEN-END:variables
}
