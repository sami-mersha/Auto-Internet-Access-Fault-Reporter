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
import static siinqee.net.admin.SiinqeeNetAdmin.setDateNow;

/**
 *
 * @author samimersha
 */
public class EditAdminAccount extends javax.swing.JFrame {

    boolean notInformed = true;
    String username = "<unknown>";

    /**
     * Creates new form EditAdminAccount
     */
    public EditAdminAccount() {
        initComponents();
    }

    public void addToHistory(String userName) {
        int his_code = generateHisCode();

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String dateNow = setDateNow();
            String cmd = "insert into history values(" + his_code + ", 'Modification', 'Admin', '" + userName + "', '" + dateNow + "')";
            st.executeUpdate(cmd);
        } catch (Exception e) {
            System.out.println("Error Occured");
            e.printStackTrace();
        }
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

    public void modifyAdmin(String firstName, String lastName, String gender, String office, String role, String userName, String pwd, int phoneNumber, String email) {
        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "UPDATE siinqee.admin SET ad_firstname = '" + firstName + "', ad_lastname = '" + lastName + "', ad_sex = '" + gender + "', ad_office = '" + office + "', ad_role = '" + role + "', ad_password = '" + pwd + "', ad_phonenumber = " + phoneNumber + ", ad_email = '" + email + "' WHERE ad_username = '" + userName + "'";
            st.executeUpdate(cmd);
            JOptionPane.showMessageDialog(new JFrame(), "Successfully Modified!");
            dispose();
        } catch (Exception e) {
            System.out.println("Error Occured");
            e.printStackTrace();
        }

    }

    public boolean checkPassword(String firstPwd, String secondPwd) {
        JFrame jf = new JFrame();
        boolean valid = true;

        if (firstPwd == null) {
            JOptionPane.showMessageDialog(jf, "Please enter your password!");
            pwdField.setText("Your password here...");
            valid = false;
            notInformed = false;
        } else if (secondPwd == null) {
            JOptionPane.showMessageDialog(jf, "Please confirm your password!");
            confirmPwdField.setText("Confirm your password here...");
            valid = false;
            notInformed = false;
        } else if (firstPwd.equals(secondPwd) == false) {
            JOptionPane.showMessageDialog(jf, "Passwords do not match!\nPlease try again!");
            valid = false;
            notInformed = false;
        } else if (secondPwd.length() > 16) {
            JOptionPane.showMessageDialog(jf, "Your password must be less than 16 character!");
            valid = false;
            notInformed = false;
        } else if (secondPwd.length() < 8) {
            JOptionPane.showMessageDialog(jf, "Your password must be greater or equal to 8 character.\nPlease enter another password!");
            valid = false;
            notInformed = false;
        } else if (secondPwd.equals("12345678")) {
            JOptionPane.showMessageDialog(jf, "The password is too easy to predict.\nPlease enter another password!");
            valid = false;
            notInformed = false;
        }

        return valid;
    }

    public void fillAdminData(String username) {
        this.username = username;
        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();
            String command = "SELECT * FROM siinqee.admin";

            ResultSet set = st.executeQuery(command);

            String firstName, lastName, gender, office, role, pwd, email;
            int phoneNumber;
            while (set.next()) {
                if (username.equals(set.getString("ad_username"))) {
                    firstName = set.getString("ad_firstname");
                    firstNameTextField.setText(firstName);
                    lastName = set.getString("ad_lastname");
                    lastNameTextField.setText(lastName);
                    gender = set.getString("ad_sex");
                    if (gender.equals("M")) {
                        malerb.setSelected(true);
                    } else {
                        femalerb.setSelected(true);
                    }
                    office = set.getString("ad_office");
                    officeTextField.setText(office);
                    role = set.getString("ad_role");
                    roleTextField.setText(role);

                    usernameLabel.setText(username);
                    pwd = set.getString("ad_password");
                    pwdField.setText(pwd);
                    confirmPwdField.setText(pwd);
                    phoneNumber = set.getInt("ad_phonenumber");
                    phoneNumberTextField.setText("0" + phoneNumber);
                    email = set.getString("ad_email");
                    emailTextField.setText(email);

                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        kGradientPanel1 = new keeptoo.KGradientPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        lastNameTextField = new javax.swing.JTextField();
        firstNameTextField = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        pwdField = new javax.swing.JPasswordField();
        jLabel15 = new javax.swing.JLabel();
        confirmPwdField = new javax.swing.JPasswordField();
        femalerb = new javax.swing.JRadioButton();
        malerb = new javax.swing.JRadioButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        officeTextField = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        roleTextField = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        phoneNumberTextField = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        emailTextField = new javax.swing.JTextField();
        submitButton = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        userNamePanel = new javax.swing.JPanel();
        usernameLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        kGradientPanel1.setForeground(new java.awt.Color(0, 102, 102));
        kGradientPanel1.setkEndColor(new java.awt.Color(0, 102, 102));
        kGradientPanel1.setkStartColor(new java.awt.Color(51, 0, 102));
        kGradientPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Montserrat", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/user_shield_30px.png"))); // NOI18N
        jLabel5.setText("Edit Admin Account");
        kGradientPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 30, -1, 40));

        jLabel10.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/form_25px.png"))); // NOI18N
        jLabel10.setText("Admin Modification Form");
        kGradientPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 80, 400, 30));

        jLabel11.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("First Name:");
        kGradientPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 140, 120, 30));
        kGradientPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 110, 690, 17));

        lastNameTextField.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        kGradientPanel1.add(lastNameTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 240, 300, -1));

        firstNameTextField.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        kGradientPanel1.add(firstNameTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 170, 300, -1));

        jLabel12.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Last Name:");
        kGradientPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 210, 120, 30));

        jLabel13.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Password:");
        kGradientPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 210, 120, 30));

        jLabel14.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Username:");
        kGradientPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 140, 120, 30));

        pwdField.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        kGradientPanel1.add(pwdField, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 240, 320, -1));

        jLabel15.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Sex:");
        kGradientPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 290, 80, 30));

        confirmPwdField.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        kGradientPanel1.add(confirmPwdField, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 310, 320, -1));

        buttonGroup1.add(femalerb);
        femalerb.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        femalerb.setText("Female");
        kGradientPanel1.add(femalerb, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 310, -1, -1));

        buttonGroup1.add(malerb);
        malerb.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        malerb.setText("Male");
        kGradientPanel1.add(malerb, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 310, -1, -1));

        jLabel16.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Confirm Password:");
        kGradientPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 280, 150, 30));

        jLabel17.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Office:");
        kGradientPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 350, 120, 30));

        officeTextField.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        kGradientPanel1.add(officeTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 380, 300, -1));

        jLabel18.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Role:");
        kGradientPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 420, 120, 30));

        roleTextField.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        kGradientPanel1.add(roleTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 450, 300, -1));

        jLabel19.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Phone Number:");
        kGradientPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 350, 120, 30));

        phoneNumberTextField.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        kGradientPanel1.add(phoneNumberTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 380, 320, -1));

        jLabel20.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Email:");
        kGradientPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 420, 120, 30));

        emailTextField.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        kGradientPanel1.add(emailTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 450, 320, -1));

        submitButton.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        submitButton.setText("Submit");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });
        kGradientPanel1.add(submitButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 530, -1, -1));

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/close_window_30px.png"))); // NOI18N
        jLabel21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel21MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel21MousePressed(evt);
            }
        });
        kGradientPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 20, -1, -1));
        kGradientPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(365, 586, -1, -1));

        usernameLabel.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        usernameLabel.setText("Username");

        javax.swing.GroupLayout userNamePanelLayout = new javax.swing.GroupLayout(userNamePanel);
        userNamePanel.setLayout(userNamePanelLayout);
        userNamePanelLayout.setHorizontalGroup(
            userNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(usernameLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
        );
        userNamePanelLayout.setVerticalGroup(
            userNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userNamePanelLayout.createSequentialGroup()
                .addComponent(usernameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        kGradientPanel1.add(userNamePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 170, 320, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 810, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        notInformed = true;
        try {
            String firstName = firstNameTextField.getText();
            String lastName = lastNameTextField.getText();

            String gender = null;
            if (malerb.isSelected()) {
                gender = "M";
            } else if (femalerb.isSelected()) {
                gender = "F";
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "Please select customers gender!");
            }
            String office = officeTextField.getText();
            String role = roleTextField.getText();

            String pwd = pwdField.getText();
            String pwdConfirmation = confirmPwdField.getText();
            int phoneNumber = Integer.parseInt(phoneNumberTextField.getText());
            String email = emailTextField.getText();

            if (gender != null && checkPassword(pwd, pwdConfirmation)) {
                modifyAdmin(firstName, lastName, gender, office, role, username, pwd, phoneNumber, email);
                addToHistory(username);
            } else if (notInformed) {
                JOptionPane.showMessageDialog(new JFrame(), "Please make sure to fill the form correctly and Try Again!");
            }

        } catch (Exception e) {
            System.out.println("Error: ");
            e.printStackTrace();
        }

    }//GEN-LAST:event_submitButtonActionPerformed

    private void jLabel21MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel21MouseClicked
        dispose();
    }//GEN-LAST:event_jLabel21MouseClicked

    private void jLabel21MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel21MousePressed
        dispose();
    }//GEN-LAST:event_jLabel21MousePressed

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
            java.util.logging.Logger.getLogger(EditAdminAccount.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditAdminAccount.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditAdminAccount.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditAdminAccount.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPasswordField confirmPwdField;
    private javax.swing.JTextField emailTextField;
    private javax.swing.JRadioButton femalerb;
    private javax.swing.JTextField firstNameTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JSeparator jSeparator2;
    private keeptoo.KGradientPanel kGradientPanel1;
    private javax.swing.JTextField lastNameTextField;
    private javax.swing.JRadioButton malerb;
    private javax.swing.JTextField officeTextField;
    private javax.swing.JTextField phoneNumberTextField;
    private javax.swing.JPasswordField pwdField;
    private javax.swing.JTextField roleTextField;
    private javax.swing.JButton submitButton;
    private javax.swing.JPanel userNamePanel;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables
}
