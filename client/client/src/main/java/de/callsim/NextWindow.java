package de.callsim;

import javax.json.Json;
import javax.json.JsonObject;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class NextWindow {
    JPanel rootPanel;
    JButton call;
    JPanel listPanel;
    JPanel userPanel;
    JPanel blueBg;
    JPanel dataContainer;
    JPanel firstNamePanel;
    JPanel actionPanel;
    JPanel whiteBg;
    JLabel firstNameLabelBig;
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
    private JScrollPane ScrollPane;

    public ArrayList<String> log;
    public String selectedUser = null;
    boolean gettingCalled = false;
    boolean callInProgress = false;
    boolean callAccepted = false;
    boolean updating = false;

    // HashMap that contains all user and its current status (Offline, InCall, Online)
    HashMap<String, String> userData = new HashMap<>();
    // HashMap with each Status and a color-code and Info-Text
    HashMap<String, String> stateColors = new HashMap<String, String>() {{
        put("\"Online\"", "0,166,0;This user is online and available");
        put("\"inCall\"", "249,179,96;This user is online but currently in another call");
        put("\"Offline\"", "111,3,30;This user is not online");

    }};

    public NextWindow() {
        log = new ArrayList<String>(); // Setting up a new Log-Display showing the current action on the bottom left
        log(client.clientUsername + " logged in"); // Upon start, show that you're successfully logged in
        callContainer.setVisible(false); // Don´t show the call Container.

        /*
         Create a Timer which sends a getListRequest every 5 seconds and upon response,
         updates the UserList and User activity
         */
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(
                new TimerTask(){
                    @Override
                    public void run(){
                        sentListRequest();
                    }
                }, 0, 5000
        );

        call.addActionListener(e -> {
            // Only be able to make a call if you're in no call and are not getting called
            if(!callInProgress && !gettingCalled) startACall(selectedUser);
        });
        hangUpBtn.addActionListener(e -> cancelCall()); // send a end call action
    }

    // Function that sends a User Status message to the websocket
    public void sentListRequest(){
        updating = true; // Whilst updating the List, interacting with List-Items should be deactivated to avoid bugs
        JsonObject json = Json.createObjectBuilder()
                .add("action", "UserStatuses")
                .build();
        // send UserStatuses message to Websocket
        client.clientEndPoint.sendMessage(json);
    }

    // Websocket triggers this function with a new user(-status) List
    public void getListResponse(HashMap<String, String> userData){
        // Update the userData Hashmap
        this.userData = userData;
        // Remove all old values
        for(Component comp : listPanel.getComponents()){
            listPanel.remove(comp);
        }
        listPanel.setLayout(new GridLayout(0,1));
        listPanel.setPreferredSize(new Dimension(150,userData.size()*50));

        // Add new List-Items
        for (String key : userData.keySet()){
            UserItem item = new UserItem(key);
            // Set up action Listeners
            item.getUserBtn().addActionListener(e -> {
                if (!callInProgress && !gettingCalled && !updating){
                    selectedUser = key;
                    updateCallDisplay();
                    updateBigDisplay(selectedUser);
                }
            });
            listPanel.add(item);

        }
        // Updating the number of Users
        numberOfUsers.setText(userData.size() + " user" + (userData.size() != 1 ? "s" : "") + " registered");
        if (selectedUser != null) updateBigDisplay(selectedUser);
        updating = false; // enabling the interactivity with List-Items
        updateCallDisplay();
        listPanel.revalidate();
        ScrollPane.getVerticalScrollBar().setUnitIncrement(5);
    }

    // Starting a call and updating the display
    public void startACall(String targetUser) {
        sentListRequest();
        if(!callInProgress && !gettingCalled) client.sendStartCallMessage(targetUser);
    }

    // Canceling a call, updating the log and update the display
    public void cancelCall() {

        log("Cancelling call...");
        setCallDisplay(false);
        client.sendRespondSelfDeclineMessage();
        log("Call has been successfully cancelled");
    }

    /*
    Updating the Display on the right. Show Username,
    Activity and Call button or the HangUp Button and Progress Bar
     */
    public void updateBigDisplay(String newUsername) {
        if (userData == null) return;
        String nameOfUser = newUsername;
        String stateOfUser = stateColors.get(userData.get(newUsername));

        // Setting up the color coding
        String color = stateOfUser.split(";")[0];
        int colorR = Integer.parseInt(color.split(",")[0]),
                colorG = Integer.parseInt(color.split(",")[1]),
                colorB = Integer.parseInt(color.split(",")[2]);

        String stateTooltip = stateOfUser.split(";")[1];

        firstNameLabelBig.setText(nameOfUser);
        userStatebar2.setForeground(new Color(colorR, colorG, colorB));
        userStatebar2.setToolTipText(stateTooltip);
    }

    // Setting the text of the log on the bottom left
    public void log(String msg) {
        log.add(0, msg); /* always add to beginning */
        stateDisplay.setText(msg);
    }

    // Show the CallDisplay or User Display
    public void setCallDisplay(boolean vis) {
        callInProgress = vis;
        updateCallDisplay();
    }

    // Update the Display
    public void updateCallDisplay() {
        callContainer.setVisible(!callInProgress && selectedUser != null);
        hangUpBtn.setVisible(!callAccepted);
        callProgressPanel.setVisible(callInProgress);
        listPanel.setEnabled(!callInProgress);
    }
}