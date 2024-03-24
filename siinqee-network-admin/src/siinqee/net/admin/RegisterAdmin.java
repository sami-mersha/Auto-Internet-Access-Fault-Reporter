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
public class RegisterAdmin extends javax.swing.JFrame {

    boolean notInformed;

    /**
     * Creates new form SignUp
     */
    public RegisterAdmin() {
        initComponents();
    }

    public void addToHistory(String userName) {
        int his_code = generateHisCode();

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String dateNow = setDateNow();
            String cmd = "insert into history values(" + his_code + ", 'Registeration', 'New Admin', '" + userName + "', '" + dateNow + "')";
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

    public void registerAdmin(String firstName, String lastName, String gender, String office, String role, String userName, String pwd, int phoneNumber, String email) {
        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "insert into admin values('" + userName + "', '" + pwd + "', '" + firstName + "', '" + lastName + "', '" + gender + "', \"" + role + "\", \"" + office + "\", " + phoneNumber + ", '" + email + "', 'Passive')";
            st.executeUpdate(cmd);
            JOptionPane.showMessageDialog(new JFrame(), "Successfully Registered!");
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

    public boolean usernameIsValid(String username) {
        JFrame jf = new JFrame();
        boolean valid = true;

        if (username == null || username.equals("Your username Here...")) {
            JOptionPane.showMessageDialog(jf, "Please enter your username correctly!");
            userNameTextField.setText("Your username Here...");
            valid = false;
        } else if (username.length() > 10) {
            JOptionPane.showMessageDialog(jf, "Username's size must be less than 10 digit. \nPlease re enter another username!");
            valid = false;
        }

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "SELECT * FROM siinqee.admin";
            ResultSet set = st.executeQuery(cmd);

            while (set.next()) {
                if (set.getString("ad_username").equals(username)) {
                    JOptionPane.showMessageDialog(jf, "The username already exists.\nPlease Use another username!");
                    valid = false;
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error:");
            e.printStackTrace();
        }

        return valid;
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
        userNameTextField = new javax.swing.JTextField();
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
        registerButton = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        kGradientPanel1.setForeground(new java.awt.Color(0, 102, 102));
        kGradientPanel1.setkEndColor(new java.awt.Color(204, 153, 0));
        kGradientPanel1.setkStartColor(new java.awt.Color(0, 102, 102));

        jLabel5.setFont(new java.awt.Font("Montserrat", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/user_shield_30px.png"))); // NOI18N
        jLabel5.setText("Register New Account");

        jLabel10.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/form_25px.png"))); // NOI18N
        jLabel10.setText("New Admin Registeration Form");

        jLabel11.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("First Name:");

        lastNameTextField.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N

        firstNameTextField.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N

        jLabel12.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Last Name:");

        jLabel13.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Password:");

        userNameTextField.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N

        jLabel14.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Username:");

        pwdField.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N

        jLabel15.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Sex:");

        confirmPwdField.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N

        buttonGroup1.add(femalerb);
        femalerb.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        femalerb.setText("Female");

        buttonGroup1.add(malerb);
        malerb.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        malerb.setText("Male");

        jLabel16.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Confirm Password:");

        jLabel17.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Office:");

        officeTextField.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N

        jLabel18.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Role:");

        roleTextField.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N

        jLabel19.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Phone Number:");

        phoneNumberTextField.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N

        jLabel20.setFont(new java.awt.Font("UniSansRegular", 0, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Email:");

        emailTextField.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N

        registerButton.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        registerButton.setText("Register");
        registerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerButtonActionPerformed(evt);
            }
        });

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/net/admin/assets/images/close_window_30px.png"))); // NOI18N
        jLabel21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel21MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel21MousePressed(evt);
            }
        });

        javax.swing.GroupLayout kGradientPanel1Layout = new javax.swing.GroupLayout(kGradientPanel1);
        kGradientPanel1.setLayout(kGradientPanel1Layout);
        kGradientPanel1Layout.setHorizontalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addGap(250, 250, 250)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 690, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(240, 240, 240)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(firstNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60)
                        .addComponent(userNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(240, 240, 240)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(lastNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60)
                        .addComponent(pwdField, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                                .addGap(70, 70, 70)
                                .addComponent(malerb))
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(35, 35, 35)
                        .addComponent(femalerb)
                        .addGap(127, 127, 127)
                        .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(confirmPwdField, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(240, 240, 240)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(officeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60)
                        .addComponent(phoneNumberTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(240, 240, 240)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(roleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60)
                        .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(340, 340, 340)
                        .addComponent(registerButton))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(365, 365, 365)
                        .addComponent(jLabel1)))
                .addGap(0, 50, Short.MAX_VALUE))
        );
        kGradientPanel1Layout.setVerticalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel21)))
                .addGap(10, 10, 10)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(firstNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(userNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lastNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pwdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(malerb))
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(femalerb))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(confirmPwdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(11, 11, 11)
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(officeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(phoneNumberTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(roleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(51, 51, 51)
                .addComponent(registerButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addComponent(jLabel1))
        );

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

    private void jLabel21MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel21MouseClicked
        dispose();
    }//GEN-LAST:event_jLabel21MouseClicked

    private void jLabel21MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel21MousePressed
        dispose();
    }//GEN-LAST:event_jLabel21MousePressed

    private void registerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerButtonActionPerformed
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

            String userName = userNameTextField.getText();
            String pwd = pwdField.getText();
            String pwdConfirmation = confirmPwdField.getText();
            int phoneNumber = Integer.parseInt(phoneNumberTextField.getText());
            String email = emailTextField.getText();

            if (gender != null && usernameIsValid(userName) && checkPassword(pwd, pwdConfirmation)) {
                registerAdmin(firstName, lastName, gender, office, role, userName, pwd, phoneNumber, email);
                addToHistory(userName);
            } else if (notInformed) {
                JOptionPane.showMessageDialog(new JFrame(), "Please make sure to fill the form correctly and Try Again!");
            }

        } catch (Exception e) {
            System.out.println("Error: ");
            e.printStackTrace();
        }


    }//GEN-LAST:event_registerButtonActionPerformed

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
            java.util.logging.Logger.getLogger(RegisterAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegisterAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegisterAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegisterAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegisterAdmin().setVisible(true);
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
    private javax.swing.JButton registerButton;
    private javax.swing.JTextField roleTextField;
    private javax.swing.JTextField userNameTextField;
    // End of variables declaration//GEN-END:variables
}
