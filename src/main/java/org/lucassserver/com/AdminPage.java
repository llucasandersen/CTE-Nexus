package org.lucassserver.com;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class AdminPage {
    private JPanel mainPanel;
    private JTextField searchField;
    private JButton searchButton;
    private JTable partnerTable;
    private JButton addDataButton;
    private JButton addUserButton;
    private JButton removeUserButton;
    private JButton removeDataButton;
    private JButton logoutButton;

    private static final String DATABASE_URL = System.getenv("DATABASE_URL");
    private static final String DATABASE_USER = System.getenv("DATABASE_USER");
    private static final String DATABASE_PASSWORD = System.getenv("DATABASE_PASSWORD");

    public AdminPage() {
        initComponents();
        loadPartnerData();
        addEventListeners();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout());
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        partnerTable = new JTable(new DefaultTableModel(new Object[]{"Name", "Type"}, 0));
        addDataButton = new JButton("Add Data");
        addUserButton = new JButton("Add User");
        removeUserButton = new JButton("Remove User");
        removeDataButton = new JButton("Remove Data");
        logoutButton = new JButton("Go to the Homepage");

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(logoutButton);

        JScrollPane tableScrollPane = new JScrollPane(partnerTable);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addDataButton);
        buttonPanel.add(addUserButton);
        buttonPanel.add(removeUserButton);
        buttonPanel.add(removeDataButton);
        getHomepagePanel();


        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

    }

    private void addEventListeners() {
        searchButton.addActionListener(e -> searchPartnerData());
        addDataButton.addActionListener(e -> openAddDataWindow());
        addUserButton.addActionListener(e -> openAddUserPanel());
        removeUserButton.addActionListener(e -> openRemoveUserPanel());
        removeDataButton.addActionListener(e -> openRemoveDataPanel());
        logoutButton.addActionListener(e -> logout());
    }

    private void loadPartnerData() {
        DefaultTableModel tableModel = (DefaultTableModel) partnerTable.getModel();
        tableModel.setRowCount(0);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);

            String query = "SELECT name, type FROM partnerdata";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String type = resultSet.getString("type");
                tableModel.addRow(new Object[]{name, type});
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void searchPartnerData() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            loadPartnerData();
        } else {
            DefaultTableModel tableModel = (DefaultTableModel) partnerTable.getModel();
            tableModel.setRowCount(0);

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);

                String query = "SELECT name, type FROM partnerdata WHERE name LIKE ? OR type LIKE ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, "%" + searchText + "%");
                preparedStatement.setString(2, "%" + searchText + "%");

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String type = resultSet.getString("type");
                    tableModel.addRow(new Object[]{name, type});
                }

                resultSet.close();
                preparedStatement.close();
                connection.close();

            } catch (ClassNotFoundException | SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void openAddDataWindow() {
        JFrame addDataFrame = new JFrame("Add Data");
        addDataFrame.setSize(400, 300);
        addDataFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel addDataPanel = new JPanel(new GridLayout(7, 2, 10, 5));

        JTextField typeField = new JTextField();
        JTextField resourcesField = new JTextField();
        JTextField contactField = new JTextField();
        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{"Most Useful", "Sponsors", "Education", "Learning Resources", "Google Services", "Teacher Resources"});
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();

        addDataPanel.add(new JLabel("Type:"));
        addDataPanel.add(typeField);
        addDataPanel.add(new JLabel("Resources:"));
        addDataPanel.add(resourcesField);
        addDataPanel.add(new JLabel("Contact:"));
        addDataPanel.add(contactField);
        addDataPanel.add(new JLabel("Category:"));
        addDataPanel.add(categoryComboBox);
        addDataPanel.add(new JLabel("Name:"));
        addDataPanel.add(nameField);
        addDataPanel.add(new JLabel("Description:"));
        addDataPanel.add(descriptionField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            saveDataToDatabase(typeField.getText(), resourcesField.getText(), contactField.getText(), categoryComboBox.getSelectedItem().toString(), nameField.getText(), descriptionField.getText(), addDataFrame);
            addDataFrame.dispose();
            mainPanel.setVisible(true);
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            addDataFrame.dispose();
            mainPanel.setVisible(true);
        });

        addDataPanel.add(saveButton);
        addDataPanel.add(backButton);

        addDataFrame.add(addDataPanel);
        addDataFrame.setVisible(true);
    }

    private void saveDataToDatabase(String type, String resources, String contact, String category, String name, String description, JFrame addDataFrame) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);

            String partnerDataQuery = "INSERT INTO partnerdata (type, resources, contact, category, name, description) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement partnerDataStatement = connection.prepareStatement(partnerDataQuery);
            partnerDataStatement.setString(1, type);
            partnerDataStatement.setString(2, resources);
            partnerDataStatement.setString(3, contact);
            partnerDataStatement.setString(4, category);
            partnerDataStatement.setString(5, name);
            partnerDataStatement.setString(6, description);

            partnerDataStatement.executeUpdate();
            partnerDataStatement.close();

            String getLastIdQuery = "SELECT LAST_INSERT_ID() as last_id";
            PreparedStatement getLastIdStatement = connection.prepareStatement(getLastIdQuery);
            ResultSet resultSet = getLastIdStatement.executeQuery();

            int lastId = -1;
            if (resultSet.next()) {
                lastId = resultSet.getInt("last_id");
            }

            String loginQuery = "INSERT INTO login (id, username, password, usertype) VALUES (?, ?, ?, ?)";
            PreparedStatement loginStatement = connection.prepareStatement(loginQuery);
            loginStatement.setInt(1, lastId);
            loginStatement.setString(2, name);
            loginStatement.setString(3, "password");
            loginStatement.setString(4, "normal");

            loginStatement.executeUpdate();

            loginStatement.close();
            getLastIdStatement.close();
            resultSet.close();
            connection.close();

            loadPartnerData();

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void openAddUserPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        JPanel addUserPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField usernameField = new JTextField("", 20);
        JPasswordField passwordField = new JPasswordField("", 20);
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"normal", "admin"});

        gbc.gridx = 0;
        gbc.gridy = 0;
        addUserPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        addUserPanel.add(usernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        addUserPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        addUserPanel.add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        addUserPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        addUserPanel.add(roleComboBox, gbc);

        JButton saveUserButton = new JButton("Save User");
        saveUserButton.addActionListener(e -> {
            saveUserData(usernameField.getText(), new String(passwordField.getPassword()), roleComboBox.getSelectedItem().toString());
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
            initComponents();
            loadPartnerData();
            addEventListeners();
            frame.setContentPane(mainPanel);
            frame.revalidate();
            frame.repaint();
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
            initComponents();
            loadPartnerData();
            addEventListeners();
            frame.setContentPane(mainPanel);
            frame.revalidate();
            frame.repaint();
            mainPanel.setVisible(true);
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        addUserPanel.add(saveUserButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        addUserPanel.add(backButton, gbc);

        mainPanel.add(addUserPanel, BorderLayout.CENTER);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void saveUserData(String username, String password, String role) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);

            String query = "INSERT INTO login (username, password, usertype) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, role);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void openRemoveUserPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        JPanel removeUserPanel = new JPanel(new BorderLayout());

        DefaultListModel<String> userListModel = new DefaultListModel<>();
        JList<String> userList = new JList<>(userListModel);
        loadUserList(userListModel);

        JScrollPane userListScrollPane = new JScrollPane(userList);

        JButton removeUserButton = new JButton("Remove User");
        removeUserButton.addActionListener(e -> removeSelectedUser(userList.getSelectedValue()));

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
            initComponents();
            loadPartnerData();
            addEventListeners();
            frame.setContentPane(mainPanel);
            frame.revalidate();
            frame.repaint();
        });

        removeUserPanel.add(userListScrollPane, BorderLayout.CENTER);
        removeUserPanel.add(removeUserButton, BorderLayout.EAST);
        removeUserPanel.add(backButton, BorderLayout.SOUTH);

        mainPanel.add(removeUserPanel, BorderLayout.CENTER);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void loadUserList(DefaultListModel<String> userListModel) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);

            String query = "SELECT username FROM login";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                userListModel.addElement(username);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void removeSelectedUser(String username) {
        if (username != null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);

                String query = "DELETE FROM login WHERE username=?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username);
                preparedStatement.executeUpdate();

                preparedStatement.close();
                connection.close();

                JOptionPane.showMessageDialog(mainPanel, "User removed successfully!");

                mainPanel.setVisible(true);

            } catch (ClassNotFoundException | SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void openRemoveDataPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        JPanel removeDataPanel = new JPanel(new BorderLayout());

        DefaultListModel<String> dataListModel = new DefaultListModel<>();
        JList<String> dataList = new JList<>(dataListModel);
        loadDataList(dataListModel);

        JScrollPane dataListScrollPane = new JScrollPane(dataList);

        JButton removeDataButton = new JButton("Remove Data");
        removeDataButton.addActionListener(e -> removeSelectedData(dataList.getSelectedValue()));

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
            initComponents();
            loadPartnerData();
            addEventListeners();
            frame.setContentPane(mainPanel);
            frame.revalidate();
            frame.repaint();
        });

        removeDataPanel.add(dataListScrollPane, BorderLayout.CENTER);
        removeDataPanel.add(removeDataButton, BorderLayout.EAST);
        removeDataPanel.add(backButton, BorderLayout.SOUTH);

        mainPanel.add(removeDataPanel, BorderLayout.CENTER);

        mainPanel.revalidate();
        mainPanel.repaint();
    }
    private void logout() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
        frame.setContentPane(homepage);
        frame.revalidate();
        frame.repaint();
    }

    private static JPanel homepage = null;

    public JPanel getHomepagePanel() {
        if (homepage == null) {
            homepage = new Homepage().getPanel();
        }
        return homepage;
    }



    private void loadDataList(DefaultListModel<String> dataListModel) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);

            String query = "SELECT name FROM partnerdata";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String dataName = resultSet.getString("name");
                dataListModel.addElement(dataName);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void removeSelectedData(String dataName) {
        if (dataName != null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);

                String query = "DELETE FROM partnerdata WHERE name=?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, dataName);
                preparedStatement.executeUpdate();

                preparedStatement.close();
                connection.close();

                JOptionPane.showMessageDialog(mainPanel, "Data removed successfully!");

                mainPanel.setVisible(true);

                loadPartnerData();

            } catch (ClassNotFoundException | SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminPage().showUI());
    }

    public void showUI() {
        JFrame frame = new JFrame("Admin Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
