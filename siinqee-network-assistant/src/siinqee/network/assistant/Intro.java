/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package siinqee.network.assistant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static siinqee.network.assistant.SiinqeeNetworkAssistant.getConnection;
import static siinqee.network.assistant.SiinqeeNetworkAssistant.setDateNow;

/**
 *
 * @author samimersha
 */
public class Intro extends javax.swing.JFrame {

    int clCode, brCode;
    String clPCName = getSystemName(), clIPAddress = getLocalIP();
    boolean informed = false;

    /**
     * Creates new form Intro
     */
    public Intro() {
        initComponents();
        clientPCName.setText(clPCName);
        clientIPAddress.setText(clIPAddress);
    }

    public void writeIntoLocalDB(String name) {
        String content = "";
        content += clCode;
        content += "\n";
        content += name;
        try {
            FileWriter myWriter = new FileWriter("localdb.txt");
            myWriter.write(content);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    public void createLocalDB() {
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

    public String getSystemName() {
        String systemName = "DESKTOP-XXXXXX";

        try {
            systemName = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            System.err.println("Error Occured!");
            e.printStackTrace();
        }
        return systemName;
    }

    public String getLocalIP() {
        String ipAddress = "XXX.XXX.XXX.XXX";

        try {
            InetAddress localhost = InetAddress.getLocalHost();
            ipAddress = localhost.getHostAddress().trim();
        } catch (Exception e) {
            System.err.println("Error Occured!");
            e.printStackTrace();
        }
        return ipAddress;
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

    public void addToHistory(String clName) {
        String event = "New Client Registeration";

        int his_code = generateHisCode();

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String dateNow = setDateNow();
            System.out.println(dateNow);
            String cmd = "insert into siinqee.history values(" + his_code + ", 'Registeration', '" + event + "', '" + clName + "', '" + dateNow + "')";
            st.executeUpdate(cmd);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean registerClient(String clName, String brName) {
        boolean registered = false;
        boolean branchIsValid = checkBranch(brName);
        boolean nameIsValid = checkName(clName);
        boolean ipIsValid = checkIP();

        if (ipIsValid) {
            System.out.println("Ip is Valid!");

            clCode = generateClientCode();

            if (branchIsValid && nameIsValid) {
                try {
                    Connection con = getConnection();
                    Statement st = con.createStatement();
                    java.util.Date date = new java.util.Date();
                    String cmd = "insert into siinqee.client values(" + clCode + ", \"" + clName + "\", \"" + clPCName + "\", \"" + clIPAddress + "\", " + brCode + ", \"Dormant\", '" + date + "')";
                    st.executeUpdate(cmd);

                    createLocalDB();
                    writeIntoLocalDB(clName);
                    registered = true;

                } catch (Exception e) {
                    System.out.println("Error Occured!");
                    e.printStackTrace();
                }
            }
        } else if (informed == false) {
            JOptionPane.showMessageDialog(new JFrame(), "Invalid IP\nPlease Contact Network Operations Center!");
        }

        return registered;
    }

    public boolean checkIP() {
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

        int cl_code = amount + 2;
        return cl_code;
    }

    public boolean checkName(String clName) {
        boolean isValid = true;

        JFrame jf = new JFrame();
        if (clName.equals("Your name here...") && informed == false) {
            JOptionPane.showMessageDialog(jf, "Please enter your name in the given box!");
            isValid = false;
            informed = true;
        } else if (clName.length() > 40 && informed == false) {
            JOptionPane.showMessageDialog(jf, "Name's size must be less than 40 digit. \nPlease re enter your name!");
            isValid = false;
            informed = true;
        }

        return isValid;
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
                        brCode = set.getInt("br_code");
                        branchCode.setText("" + brCode);
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
    
    public void registerButton(){
        String clName = clientNameTextField.getText();
        String clBranch = branchTextField.getText();

        informed = false;
        boolean isRegistered = false;
        JFrame jf = new JFrame();

        if (clName.isEmpty()) {
            JOptionPane.showMessageDialog(jf, "Username field is empty!", "Register Error", JOptionPane.ERROR_MESSAGE);
            clientNameTextField.setText("Your name here...");
            informed = true;
        } else if (clBranch.isEmpty() && informed == false) {
            JOptionPane.showMessageDialog(jf, "Branch field is empty!", "Register Error", JOptionPane.ERROR_MESSAGE);
            branchTextField.setText("Your branch here...");
            informed = true;
        }

        isRegistered = registerClient(clName, clBranch);

        if (isRegistered) {
            dispose();
            addToHistory(clName);
            Main m = new Main(clName, clCode);
            m.setVisible(true);
            m.updateSession();
        }
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
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        clientNameTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        branchCode = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        branchTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        clientPCName = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        clientIPAddress = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        kGradientPanel1.setkEndColor(new java.awt.Color(0, 102, 102));
        kGradientPanel1.setkStartColor(new java.awt.Color(255, 204, 0));
        kGradientPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/network/assistant/assets/images/Siinqee 400x.jpg"))); // NOI18N
        kGradientPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 40, -1, -1));

        jPanel1.setBackground(new java.awt.Color(204, 231, 217));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        clientNameTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        clientNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientNameTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(clientNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(clientNameTextField, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/network/assistant/assets/images/user_24px.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        kGradientPanel1.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 330, -1, -1));

        branchCode.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        branchCode.setForeground(new java.awt.Color(255, 255, 255));
        branchCode.setText("XXXX");
        kGradientPanel1.add(branchCode, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 420, -1, -1));

        jLabel3.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Need Help? Contact Network Adminstrators");
        kGradientPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 590, 330, -1));
        kGradientPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 290, 400, 17));

        jPanel3.setBackground(new java.awt.Color(204, 231, 217));
        jPanel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel3MouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel3MousePressed(evt);
            }
        });

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel7MouseClicked(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Raleway", 0, 18)); // NOI18N
        jLabel10.setText("        Register");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        kGradientPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 520, 170, -1));

        jLabel4.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Branch");
        kGradientPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 390, -1, -1));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/network/assistant/assets/images/close_window_30px.png"))); // NOI18N
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });
        kGradientPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 10, 40, -1));

        jPanel5.setBackground(new java.awt.Color(204, 231, 217));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        branchTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        branchTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                branchTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(branchTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(branchTextField, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/network/assistant/assets/images/address_24px.png"))); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        kGradientPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 410, -1, -1));

        jLabel9.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/network/assistant/assets/images/user_shield_24px.png"))); // NOI18N
        jLabel9.setText("New Computer Registeration Form");
        kGradientPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 260, 400, 30));

        jLabel11.setFont(new java.awt.Font("Montserrat", 0, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Welcome To Siinqee Network Assistant");
        kGradientPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 230, -1, -1));

        jLabel5.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Your Name");
        kGradientPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 300, -1, -1));

        jLabel12.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Branch Code:");
        kGradientPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 420, -1, -1));

        jLabel13.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("PC Name:");
        kGradientPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 340, -1, -1));

        clientPCName.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        clientPCName.setForeground(new java.awt.Color(255, 255, 255));
        clientPCName.setText("DESKTOP-XXXXXX");
        kGradientPanel1.add(clientPCName, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 340, -1, -1));

        jLabel15.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("IP:");
        kGradientPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 380, -1, -1));

        clientIPAddress.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        clientIPAddress.setForeground(new java.awt.Color(255, 255, 255));
        clientIPAddress.setText("XXX.XXX.XXX.XXX");
        kGradientPanel1.add(clientIPAddress, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 380, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        dispose();
        System.exit(1);
    }//GEN-LAST:event_jLabel6MouseClicked

    private void jPanel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel7MouseClicked
        registerButton();
    }//GEN-LAST:event_jPanel7MouseClicked

    private void clientNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientNameTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clientNameTextFieldActionPerformed

    private void branchTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_branchTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_branchTextFieldActionPerformed

    private void jPanel3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel3MousePressed
        registerButton();
    }//GEN-LAST:event_jPanel3MousePressed

    private void jPanel3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel3MouseEntered
       
    }//GEN-LAST:event_jPanel3MouseEntered

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
            java.util.logging.Logger.getLogger(Intro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Intro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Intro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Intro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Intro().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel branchCode;
    private javax.swing.JTextField branchTextField;
    private javax.swing.JLabel clientIPAddress;
    private javax.swing.JTextField clientNameTextField;
    private javax.swing.JLabel clientPCName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JSeparator jSeparator1;
    private keeptoo.KGradientPanel kGradientPanel1;
    // End of variables declaration//GEN-END:variables
}
