package de.callsim;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Locale;
import java.util.HashMap;

public class NextWindow {
    JPanel rootPanel;
    JButton call;
    JPanel listPanel;
    JButton callUserDirectlyBtn;
    JPanel userPanel;
    JPanel listUserTemplate;
    JPanel blueBg;
    JPanel dataContainer;
    JPanel firstNamePanel;
    JPanel surnamePanel;
    JPanel actionPanel;
    JPanel furtherInfoPanel;
    JPanel whiteBg;
    JLabel usernameLabelBig;
    JLabel usernamePrefix;
    JLabel firstNameLabelBig;
    JLabel surnameLabelBig;
    JButton userBtn1;
    JButton userBtn2;
    JLabel numberOfUsers;
    JPanel callContainer;
    JPanel callProgressPanel;
    JProgressBar progressBar1;
    JPanel namePanelContainer;
    JProgressBar userStateBar;
    JLabel userStatebar2;
    JButton hangUpBtn;
    JPanel contentPanel;
    JPanel statePanel;
    JLabel stateDisplay;
    int currentUserId = -1;
    String userNamePrefix = "@";

    ArrayList<String> log;
    final int maxLogs = 10;

    boolean callInProgress = false;

    HashMap<String, String> stateColors = new HashMap<String, String>() {{
        put("Online", "0,166,0;This user is online and available");
        put("Busy", "249,179,96;This user is online but currently in another call");
        put("Offline", "111,3,30;This user is not online");

    }};

    /* ref: https://www.baeldung.com/java-initialize-hashmap#the-static-initializer-for-a-static-hashmap*/


    // no idea (yet) how dynamic rendering can be realized

    public NextWindow(HashMap<String, String> userData) {
        listPanel.setLayout(new GridLayout(0, 1));
        for (String key : userData.keySet()){
            JPanel btnGroup = new JPanel();
            btnGroup.setOpaque(false);
            JButton userBtn = new JButton(key);
            userBtn.setForeground(Color.white);
            userBtn.setOpaque(false);
            userBtn.setContentAreaFilled(false);
            userBtn.setBorderPainted(false);
            JButton callBtn = new JButton("\uD83D\uDCDE");
            callBtn.setForeground(Color.white);
            callBtn.setOpaque(false);
            callBtn.setContentAreaFilled(false);
            callBtn.setBorderPainted(false);
            btnGroup.setLayout(new GridLayout(1, 2));
            btnGroup.add(userBtn);
            btnGroup.add(callBtn);
            System.out.println("add to Panel");
            listPanel.add(btnGroup);
        }

        numberOfUsers.setText(userData.size() + " user" + (userData.size() != 1 ? "s" : "") + " registered");
        updateCallDisplay();

        log = new ArrayList<String>();

        call.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // code here will be executed after clicking on CALL

                /*JOptionPane.showMessageDialog(null, "lorem");*/

                String user = "der-bernd"; /* propably better way: giving userId as param */
                startACall(user);
            }
        });
        callUserDirectlyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = "der-bernd"; /* propably better way: giving userId as param */
                startACall(user);
            }
        });
        userBtn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String targetUser = "theoneandonly";
                updateBigDisplay(userData, targetUser);
            }
        });
        userBtn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String targetUser = "der-bernd";
                updateBigDisplay(userData, targetUser);
            }
        });
        hangUpBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelCall();
            }
        });
    }

    public void updateBigDisplay(HashMap<String, String> userData, String newUsername) {
        if (callInProgress) return; /* if a call is in progress, abort */
        String dataOfUser = userData.get(newUsername);
        if (dataOfUser == null) return;

        String firstNameOfUser = dataOfUser.split(",")[1];
        String surnameOfUser = dataOfUser.split(",")[0];
        String stateOfUser = stateColors.get(dataOfUser.split(",")[2]);

        String color = stateOfUser.split(";")[0];
        int colorR = Integer.parseInt(color.split(",")[0]),
                colorG = Integer.parseInt(color.split(",")[1]),
                colorB = Integer.parseInt(color.split(",")[2]);


        String stateTooltip = stateOfUser.split(";")[1];


        firstNameLabelBig.setText(firstNameOfUser);
        surnameLabelBig.setText(surnameOfUser);
        usernameLabelBig.setText(usernamePrefix.getText() + newUsername);
        userStatebar2.setForeground(new Color(colorR, colorG, colorB));
        userStatebar2.setToolTipText(stateTooltip);
    }

    public void startACall(String targetUser) {
        log("Starting new call...");
        setCallDisplay(true);

        /* request logic here */
        boolean browserOpen = true; /* simulating establishment of connection */
        if (browserOpen) log("Connection established");
    }

    public void cancelCall() {
        log("Cancelling call...");
        /* request logic here */

        setCallDisplay(false);
        log("Call has been successfully cancelled");
    }

    private void log(String msg) {
        log.add(0, msg); /* always add to beginning */
        if(log.size() > maxLogs){
            /* here you could reduce the list so it doensn't mess up the storage */
        }

        stateDisplay.setText(msg);
    }


    public void triggerCallDisplay() {
        callInProgress = !callInProgress;
        updateCallDisplay();
    }

    public void setCallDisplay(boolean vis) {
        callInProgress = vis;
        updateCallDisplay();
    }

    private void updateCallDisplay() {
        callContainer.setVisible(!callInProgress);
        callProgressPanel.setVisible(callInProgress);

        listPanel.setEnabled(!callInProgress); /* when call in progress, disable the sidebar */
    }
}