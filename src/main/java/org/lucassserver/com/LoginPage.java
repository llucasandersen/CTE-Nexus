package org.lucassserver.com;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPage {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel loginPanel;
    private JPanel logoutloginPanel;
    private JTextField user;
    private JPasswordField password;
    private JButton loginbutton;
    private JLabel userlabel;
    private JLabel passwordlabel;
    private JPanel title;
    private JLabel titleofpanel;
    private JPanel panel1;


    private static final String DATABASE_URL = "jdbc:mysql://96.39.211.90:12345/serverdata";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "p757sbrq86xpd42jg655kb3";

    public LoginPage() {
        loginbutton.addActionListener(e -> authenticateUser());
    }



    private void authenticateUser() {
        String username = user.getText();
        String userPassword = new String(password.getPassword());

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);

            // Check if the user exists in the login table
            String loginQuery = "SELECT * FROM login WHERE username = ? AND password = ?";
            PreparedStatement loginStatement = connection.prepareStatement(loginQuery);
            loginStatement.setString(1, username);
            loginStatement.setString(2, userPassword);

            ResultSet loginResultSet = loginStatement.executeQuery();

            if (loginResultSet.next()) {
                // User exists in the login table
                String userType = loginResultSet.getString("usertype");

                // Check if the user is new
                String newUserQuery = "SELECT * FROM new_user WHERE nonnewuser = ?";
                PreparedStatement newUserStatement = connection.prepareStatement(newUserQuery);
                newUserStatement.setString(1, username);

                ResultSet newUserResultSet = newUserStatement.executeQuery();

                if (!newUserResultSet.next()) {
                    // User is new, add them to the new_user table
                    String insertNewUserQuery = "INSERT INTO new_user (nonnewuser) VALUES (?)";
                    PreparedStatement insertNewUserStmt = connection.prepareStatement(insertNewUserQuery);
                    insertNewUserStmt.setString(1, username);
                    insertNewUserStmt.executeUpdate();
                    insertNewUserStmt.close();

                    // Now direct the new user to the HelpPanel
                    if ("normal".equals(userType)) {
                        cardLayout.show(cardPanel, "HelpPanel");
                    }
                    if ("admin".equals(userType)) {
                        cardLayout.show(cardPanel, "adminPanel");
                    }
                } else {
                    // User is not new, direct to home or admin panel based on user type
                    if ("normal".equals(userType)) {
                        cardLayout.show(cardPanel, "homePanel");
                    } else if ("admin".equals(userType)) {
                        cardLayout.show(cardPanel, "adminPanel");
                    } else {
                        displayErrorMessage("<html><font color='red'>Invalid user type</font></html>");
                    }
                }

                newUserResultSet.close();
                newUserStatement.close();
            } else {
                // User does not exist in the login table
                displayErrorMessage("<html><font color='red'>Invalid username or password</font></html>");
            }

            loginResultSet.close();
            loginStatement.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }


    private void displayErrorMessage(String errorMessage) {
        titleofpanel.setText(errorMessage);


        Timer timer = new Timer(2000, e -> titleofpanel.setText("CTE Partners and Business"));
        timer.setRepeats(false);
        timer.start();
    }


    public void createAndShowGUI() {
        JFrame frame = new JFrame("Login and Home Pages");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);


        loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout());
        loginPanel.add(title, BorderLayout.NORTH);
        loginPanel.add(panel1, BorderLayout.CENTER);
        cardPanel.add(loginPanel, "loginPanel");



        JPanel HelpPanel = new Helppage().getPanel();
        cardPanel.add(HelpPanel, "HelpPanel");


        JPanel homePanel = new Homepage().getPanel();
        cardPanel.add(homePanel, "homePanel");


        JPanel adminPanel = new AdminPage().getMainPanel();
        cardPanel.add(adminPanel, "adminPanel");

        frame.add(cardPanel);
        frame.setResizable(false);
        frame.setPreferredSize(new Dimension(600, 700));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginPage().createAndShowGUI();
        });
    }



}
