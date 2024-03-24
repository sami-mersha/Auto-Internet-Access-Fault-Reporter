/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package siinqee.network.assistant;

import java.awt.Desktop;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static siinqee.network.assistant.SiinqeeNetworkAssistant.getConnection;
import static siinqee.network.assistant.SiinqeeNetworkAssistant.setDateNow;
import java.net.*;

/**
 * @author samimersha
 */
public class Main extends javax.swing.JFrame {

    int cl_code = 0;
    String cl_name;

    /**
     * Creates new form Main
     */
    public Main(String cl_name, int cl_code) {
        initComponents();
        fullNameLabel.setText(cl_name);
        this.cl_code = cl_code;
        this.cl_name = cl_name;
        updateSession();
    }

    public void startUpdatingClientSession() {
        while (true) {
            updateSession();
            try {
                Thread.sleep(60 * 1000);
            } catch (Exception e) {
                System.out.println("Error Occured");
                e.printStackTrace();
            }
        }
    }

    public void updateSession() {
        //long pingLatencyTime = PingIP("172.24.20.19");
        long pingLatencyTime = PingIP("8.8.8.8");
        if (pingLatencyTime > 0) {
            try {
                Connection con = getConnection();
                Statement st = con.createStatement();
                java.util.Date date = new java.util.Date();
                String cmd = "update siinqee.client set cl_session = 'Active', cl_lastseen = '" + date + "' where cl_code = " + cl_code;
                st.executeUpdate(cmd);
                System.out.println("Checked: Ping Latency Time: " + pingLatencyTime);
            } catch (Exception e) {
                System.out.println("Error Occured");
                e.printStackTrace();
            }
        } else {
            System.out.println("Not Responding \nPing Latency Time: " + pingLatencyTime);
        }
    }

    private long PingIP(String ipAddress) {
        int maxTime = 2000;
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

    public void reportMessageButton() {
        boolean valid;

        valid = false;
        String info = "Choose fault type to be reported!"
                + "\nEnter"
                + "\n1. to report Speed Issue"
                + "\n2. to report Interruption"
                + "\n3. to report Physical Damage"
                + "\n4. to report Others"
                + "\nNotice: Please Enter by choosing from the numbers: ";
        String messageType = JOptionPane.showInputDialog(new JFrame(), info, "Fault Reporting Form", JOptionPane.QUESTION_MESSAGE);
        while (valid != true) {
            if (messageType.isEmpty() || messageType.length() > 1 || Integer.parseInt(messageType) > 4) {
                messageType = JOptionPane.showInputDialog(new JFrame(), "Invalid Input\n" + info, "Fault Reporting Form", JOptionPane.ERROR_MESSAGE);
            } else if (Integer.parseInt(messageType) == 1) {
                messageType = "Speed Issue";
                valid = true;
                break;
            } else if (Integer.parseInt(messageType) == 2) {
                messageType = "Interruption";
                valid = true;
                break;
            } else if (Integer.parseInt(messageType) == 3) {
                messageType = "Physical Damage";
                valid = true;
                break;
            } else if (Integer.parseInt(messageType) == 4) {
                messageType = "Other Issue";
                valid = true;
                break;
            } 
        }

        valid = false;
        String message = JOptionPane.showInputDialog(new JFrame(), "Write the report here: ", "Fault Reporting Form", JOptionPane.QUESTION_MESSAGE);
        while (valid != true) {
            if (message.isEmpty()) {
                message = JOptionPane.showInputDialog(new JFrame(), "Please enter the message properly: ", "Fault Reporting Form", JOptionPane.ERROR_MESSAGE);
            } else {
                valid = true;
                break;
            }
        }

        if (valid) {
            registerMessages(generateMessageCode(), messageType, message, cl_code);
            JOptionPane.showMessageDialog(new JFrame(), "Successfully Registered!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void registerMessages(int meCode, String meType, String mes, int clCode) {
        String cls = "registerMessages";

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "insert into message values("+ meCode +", \""+ meType +"\", \""+ mes +"\", "+ clCode +");";
            st.execute(cmd);
            System.out.println("Message recorded Successfully!");
            
        } catch (Exception e) {
            System.out.println("Error Occured: " + cls);
            e.printStackTrace();
        }
    }

    public int generateMessageCode() {
        int amount = 1;
        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "select * from siinqee.message;";
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
        String event = "Client with name <" + clName + "> has been removed.";

        int his_code = generateHisCode();

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String dateNow = setDateNow();
            String cmd = "insert into history values(" + his_code + ", 'Removed Client', '" + event + "', '" + clName + "', '" + dateNow + "')";
            st.executeUpdate(cmd);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void removePC() {
        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String clName = cl_name;

            String cmd = "delete from siinqee.client where cl_code = " + cl_code;
            int done = st.executeUpdate(cmd);
            System.out.println("Done: " + done);
            if (done == 1) {
                dispose();
                addToHistory(clName);
                System.exit(1);
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

        jPanel1 = new javax.swing.JPanel();
        kGradientPanel1 = new keeptoo.KGradientPanel();
        checkConnectivityPannel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        speedTest = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        reportButton = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        fullNameLabel = new javax.swing.JLabel();
        fullNameLabel1 = new javax.swing.JLabel();
        minimizeLabel = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        kGradientPanel1.setkEndColor(new java.awt.Color(0, 167, 118));
        kGradientPanel1.setkStartColor(new java.awt.Color(255, 204, 0));

        checkConnectivityPannel.setBackground(new java.awt.Color(204, 255, 204));
        checkConnectivityPannel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkConnectivityPannelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                checkConnectivityPannelMouseEntered(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/network/assistant/assets/images/wired_network_connection_200px.png"))); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 255, 204));

        jLabel1.setFont(new java.awt.Font("Uni Sans Regular", 1, 18)); // NOI18N
        jLabel1.setText("Check Connectivity");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1))
        );

        javax.swing.GroupLayout checkConnectivityPannelLayout = new javax.swing.GroupLayout(checkConnectivityPannel);
        checkConnectivityPannel.setLayout(checkConnectivityPannelLayout);
        checkConnectivityPannelLayout.setHorizontalGroup(
            checkConnectivityPannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(checkConnectivityPannelLayout.createSequentialGroup()
                .addGroup(checkConnectivityPannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(checkConnectivityPannelLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(checkConnectivityPannelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        checkConnectivityPannelLayout.setVerticalGroup(
            checkConnectivityPannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, checkConnectivityPannelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Uni Sans Regular", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText(" Network Dashboard");

        speedTest.setBackground(new java.awt.Color(204, 255, 204));
        speedTest.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                speedTestMousePressed(evt);
            }
        });

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/network/assistant/assets/images/speed_180px.png"))); // NOI18N

        jPanel6.setBackground(new java.awt.Color(255, 255, 204));

        jLabel7.setFont(new java.awt.Font("Uni Sans Regular", 1, 18)); // NOI18N
        jLabel7.setText("      Speed Test");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel7))
        );

        javax.swing.GroupLayout speedTestLayout = new javax.swing.GroupLayout(speedTest);
        speedTest.setLayout(speedTestLayout);
        speedTestLayout.setHorizontalGroup(
            speedTestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(speedTestLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(speedTestLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        speedTestLayout.setVerticalGroup(
            speedTestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, speedTestLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        reportButton.setBackground(new java.awt.Color(204, 255, 204));
        reportButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                reportButtonMousePressed(evt);
            }
        });

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/network/assistant/assets/images/system_report_180px.png"))); // NOI18N

        jPanel8.setBackground(new java.awt.Color(255, 255, 204));

        jLabel9.setFont(new java.awt.Font("Uni Sans Regular", 1, 18)); // NOI18N
        jLabel9.setText("Report a Problem");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel9))
        );

        javax.swing.GroupLayout reportButtonLayout = new javax.swing.GroupLayout(reportButton);
        reportButton.setLayout(reportButtonLayout);
        reportButtonLayout.setHorizontalGroup(
            reportButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reportButtonLayout.createSequentialGroup()
                .addGroup(reportButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(reportButtonLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(reportButtonLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        reportButtonLayout.setVerticalGroup(
            reportButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reportButtonLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/network/assistant/assets/images/close_window_30px.png"))); // NOI18N
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/network/assistant/assets/images/info_30px.png"))); // NOI18N

        fullNameLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/network/assistant/assets/images/user_30px.png"))); // NOI18N
        fullNameLabel.setText("USER NAME");

        fullNameLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/network/assistant/assets/images/logout_rounded_left_30px.png"))); // NOI18N
        fullNameLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fullNameLabel1MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                fullNameLabel1MouseExited(evt);
            }
        });

        minimizeLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siinqee/network/assistant/assets/images/minimize_window_30px.png"))); // NOI18N
        minimizeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                minimizeLabelMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                minimizeLabelMousePressed(evt);
            }
        });

        javax.swing.GroupLayout kGradientPanel1Layout = new javax.swing.GroupLayout(kGradientPanel1);
        kGradientPanel1.setLayout(kGradientPanel1Layout);
        kGradientPanel1Layout.setHorizontalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(minimizeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addContainerGap())
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addGap(113, 113, 113)
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addComponent(fullNameLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fullNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(checkConnectivityPannel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                        .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(speedTest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(53, 53, 53)
                        .addComponent(reportButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 107, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addGap(144, 144, 144))))
        );
        kGradientPanel1Layout.setVerticalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(minimizeLabel))
                .addGap(15, 15, 15)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(checkConnectivityPannel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(speedTest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(reportButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(fullNameLabel)
                    .addComponent(fullNameLabel1))
                .addGap(26, 26, 26))
        );

        getContentPane().add(kGradientPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        dispose();
    }//GEN-LAST:event_jLabel10MouseClicked

    private void fullNameLabel1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fullNameLabel1MouseExited

    }//GEN-LAST:event_fullNameLabel1MouseExited

    private void fullNameLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fullNameLabel1MouseClicked
        int response = JOptionPane.showConfirmDialog(new JFrame(), "Do you want to remove this pc from server?", "Confirm to Remove PC", JOptionPane.YES_NO_CANCEL_OPTION);
        //0 means yes 1 means no 
        if (response == 0) {
            removePC();
        } else {
            System.out.println("Unable to remove, please contact network operations center!");
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_fullNameLabel1MouseClicked

    private void checkConnectivityPannelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkConnectivityPannelMouseClicked

        if (connected()) {
            JOptionPane.showMessageDialog(new JFrame(), "Your Internet is working!");
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Your Internet is not connected! \nNetwork Technicians will visit you ASAP!\nContact Center number: 0114 645 654");
        }
    }//GEN-LAST:event_checkConnectivityPannelMouseClicked

    private void checkConnectivityPannelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkConnectivityPannelMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_checkConnectivityPannelMouseEntered

    private void speedTestMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_speedTestMousePressed
        Desktop browser = Desktop.getDesktop();

        int result = JOptionPane.showConfirmDialog(new JFrame(), "This function is on development\nDo you want to test your speed using your browser?", "Confirm Opening External Link",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            try {
                browser.browse(new URI("https://speedsmart.net/"));
                JOptionPane.showMessageDialog(new JFrame(), "Please Wait for your browser!");
            } catch (Exception e) {
                System.err.println("Error Occured");
                e.printStackTrace();
            }
        }

    }//GEN-LAST:event_speedTestMousePressed

    private void minimizeLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minimizeLabelMouseClicked

    }//GEN-LAST:event_minimizeLabelMouseClicked

    private void minimizeLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minimizeLabelMousePressed
        this.setExtendedState(Main.ICONIFIED);
    }//GEN-LAST:event_minimizeLabelMousePressed

    private void reportButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reportButtonMousePressed
        reportMessageButton();
    }//GEN-LAST:event_reportButtonMousePressed

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
    private javax.swing.JPanel checkConnectivityPannel;
    private javax.swing.JLabel fullNameLabel;
    private javax.swing.JLabel fullNameLabel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private keeptoo.KGradientPanel kGradientPanel1;
    private javax.swing.JLabel minimizeLabel;
    private javax.swing.JPanel reportButton;
    private javax.swing.JPanel speedTest;
    // End of variables declaration//GEN-END:variables
}
