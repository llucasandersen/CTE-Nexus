package org.lucassserver.com;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.Border;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import static java.lang.Thread.sleep;

public class Homepage extends JPanel {
    private static final String API_KEY = "AIzaSyCJTc3g3cFaS3Vr16xfiuHnXC6XzPdwnW0";

    private JTextField searchField;
    private JButton searchButton;
    private JTabbedPane tabbedPane;
    private JPanel aiSearchPanel; // Panel for AI search
    private JTextField aiSearchField;
    private JButton aiSearchPageButton;
    private CardLayout cardLayout;
    private JPanel cardsPanel;
    private JButton aiSearchButton;
    private JButton helpbutton;
    private JButton reportButton; // Button for creating reports
    private static final String DATABASE_URL = "jdbc:mysql://96.39.211.90:12345/serverdata";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "p757sbrq86xpd42jg655kb3";

    public Homepage() {
        initComponents();
        createAISearchPanel();
        addEventListeners();
    }
    public String userId = randomString();

    public String randomString() {
        if (20 <= 0) {
            throw new IllegalArgumentException("String length must be positive.");
        }

        // Define characters that can be used in the random string
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // Create a secure random number generator
        SecureRandom random = new SecureRandom();

        // Create a StringBuilder to build the random string
        StringBuilder sb = new StringBuilder(20);

        // Generate random characters and append them to the StringBuilder
        for (int i = 0; i < 20; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    // Getter method to retrieve the userId
    public String getUserId() {
        return this.userId;
    }

    private void initComponents() {
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);

        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.BLACK);
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchButton.setForeground(Color.WHITE);
        searchButton.setBackground(Color.DARK_GRAY);
        topPanel.add(searchField);
        topPanel.add(searchButton);

        aiSearchButton = new JButton("AI Search");
        aiSearchButton.setForeground(Color.WHITE);
        aiSearchButton.setBackground(Color.DARK_GRAY); // Different color to distinguish
        topPanel.add(aiSearchButton);

        helpbutton = new JButton("Help");
        helpbutton.setForeground(Color.WHITE);
        helpbutton.setBackground(Color.DARK_GRAY);
        topPanel.add(helpbutton);

        reportButton = new JButton("Create Report");
        reportButton.setForeground(Color.WHITE);
        reportButton.setBackground(Color.DARK_GRAY);
        topPanel.add(reportButton);

        customizeTabbedPane();

        addTabs();

        add(topPanel, BorderLayout.NORTH);
        cardsPanel.add(tabbedPane, "ListPanel");
        add(cardsPanel, BorderLayout.CENTER);
    }

    private void createAISearchPanel() {
        aiSearchPanel = new JPanel();
        aiSearchPanel.setLayout(new BoxLayout(aiSearchPanel, BoxLayout.Y_AXIS));
        aiSearchPanel.setBackground(Color.BLACK); // Set background to black

        JLabel searchLabel = new JLabel("Search what you need");
        searchLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchLabel.setForeground(Color.WHITE); // Set text color to white for visibility

        aiSearchField = new JTextField(30);
        aiSearchField.setMaximumSize(new Dimension(aiSearchField.getPreferredSize().width, 40)); // to prevent stretching
        aiSearchField.setForeground(Color.WHITE); // Set text color to white
        aiSearchField.setBackground(Color.DARK_GRAY); // Set a darker background for the field
        aiSearchField.setCaretColor(Color.WHITE); // Set caret color
        // Custom rounded border with padding
        aiSearchField.setBorder(BorderFactory.createCompoundBorder(
                aiSearchField.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        aiSearchPageButton = new JButton("Search");
        aiSearchPageButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Custom styling for a rounded, modern look
        aiSearchPageButton.setBackground(Color.GRAY);
        aiSearchPageButton.setForeground(Color.WHITE);
        Border roundedBorder = new RoundedBorder(10); // 10 is the radius
        aiSearchPageButton.setBorder(roundedBorder);

        aiSearchPanel.add(Box.createVerticalStrut(20)); // Spacer
        aiSearchPanel.add(searchLabel);
        aiSearchPanel.add(Box.createVerticalStrut(10)); // Spacer
        aiSearchPanel.add(aiSearchField);
        aiSearchPanel.add(Box.createVerticalStrut(10)); // Spacer
        aiSearchPanel.add(aiSearchPageButton);

        String userInput = aiSearchField.getText().trim();

        cardsPanel.add(aiSearchPanel, "AISearchPanel");
    }

    private void loadAISearchPanel() {
        cardLayout.show(cardsPanel, "AISearchPanel");
    }

    public void performAISearch() {
        String searchText = aiSearchField.getText().trim();

        if (!searchText.isEmpty()) {
            String aiResponse = getAIResponse(searchText);

            // Display AI response on the AI search page
            JTextArea aiResponseArea = new JTextArea("AI Response: " + aiResponse);
            aiResponseArea.setEditable(false);
            aiResponseArea.setLineWrap(true);
            aiResponseArea.setWrapStyleWord(true);

            aiResponseArea.setBackground(Color.DARK_GRAY);
            aiResponseArea.setForeground(Color.WHITE);

            JScrollPane scrollPane = new JScrollPane(aiResponseArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            cardsPanel.add(scrollPane, BorderLayout.CENTER);

            // Create a back button to return to the main page
            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> cardLayout.show(cardsPanel, "ListPanel"));

            // Create a panel to display the AI response and back button
            JPanel aiResponsePanel = new JPanel(new BorderLayout());
            aiResponsePanel.add(aiResponseArea, BorderLayout.CENTER);
            aiResponsePanel.add(backButton, BorderLayout.SOUTH);
            aiResponsePanel.setBackground(Color.BLACK);

            // Add the AI response panel to the cardsPanel and show it
            cardsPanel.add(aiResponsePanel, "AIResponsePanel");
            cardLayout.show(cardsPanel, "AIResponsePanel");
        }
    }

    private String getAllPartnerDataAsString() {
        StringBuilder dataStringBuilder = new StringBuilder();

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
            String query = "SELECT name, type, description, contact, resources, website FROM partnerdata";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        String type = resultSet.getString("type");
                        String description = resultSet.getString("description");
                        String contact = resultSet.getString("contact");
                        String resources = resultSet.getString("resources");
                        String website = resultSet.getString("website");

                        // Append the data to the StringBuilder
                        dataStringBuilder.append("Name: ").append(name).append("\n");
                        dataStringBuilder.append("Type: ").append(type).append("\n");
                        dataStringBuilder.append("Description: ").append(description).append("\n\n");
                        dataStringBuilder.append("Resources: ").append(resources).append("\n\n");
                        dataStringBuilder.append("Contact: ").append(contact).append("\n");
                        dataStringBuilder.append("Website: ").append(website).append("\n\n");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Convert the StringBuilder to a String
        return dataStringBuilder.toString();
    }

    private String getAIResponse(String searchText) {
        String partnerData = getAllPartnerDataAsString();
        try {
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + API_KEY);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String inputJson = "{\"contents\":[{\"parts\":[{\"text\":\"From this data and ONLY THIS DATA, pick the best company from the QUESTION: " + partnerData + ". ANSWER WITH THE DATA YOU RECEIVED AND ONLY THAT DATA and from some knowledge, and say why you think is the best company/partner. Once you get the data and answer, below your answer, put the NAME: TYPE: RESOURCES: CONTACT: WEBSITE: and DESCRIPTION: The data you are given is provided by the database ANSWER DIRECTLY TO THEM, NOT THIRD PERSON, LIKE YOU ARE IN A CONVERSATION. THIS IS THE SEARCH: " + searchText + "\"}]}]}";
            connection.getOutputStream().write(inputJson.getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            connection.disconnect();

            // Extract the AI response text
            String aiResponse = extractAIText(response.toString());

            return aiResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String extractAIText(String jsonResponse) {
        try {
            JSONObject json = new JSONObject(jsonResponse);
            JSONArray candidates = json.getJSONArray("candidates");
            if (candidates.length() > 0) {
                JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");
                if (parts.length() > 0) {
                    JSONObject textPart = parts.getJSONObject(0);
                    String text = textPart.getString("text");
                    return text;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "AI Response: Blocked please pick another search term";
    }

    private void customizeTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                tabAreaInsets = new Insets(0, 0, 0, 0);
                selectedTabPadInsets = new Insets(0, 0, 0, 0);
            }

            @Override
            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 30;
            }

            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) + 10;
            }
        });
        tabbedPane.setBackground(Color.BLACK);
        tabbedPane.setForeground(Color.BLACK);
        tabbedPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    private static class RoundedBorder implements Border {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    private void addTabs() {
        String id = getUserId();
        String[] categories = {"Everything", "Most Useful", "Sponsors", "Education", "Learning Resources", "Google Services", "Teacher Resources", "Trending"};

        for (String category : categories) {
            JPanel tabPanel = new JPanel();
            tabPanel.setLayout(new BoxLayout(tabPanel, BoxLayout.Y_AXIS));
            tabPanel.setBackground(Color.BLACK);
            tabPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            List<Object[]> dataList = loadData(category);
            for (Object[] data : dataList) {
                tabPanel.add(createDataBox(data, id));
            }

            JScrollPane scrollPane = new JScrollPane(tabPanel);
            scrollPane.setBackground(Color.BLACK);
            tabbedPane.addTab(category, scrollPane);
        }
    }

    private List<Object[]> loadData(String category) {
        List<Object[]> dataList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
            String query;
            if ("Everything".equals(category)) {
                query = "SELECT name, type, description, resources, contact, website FROM partnerdata";
            } else if ("Trending".equals(category)) {
                query = "SELECT p.name, p.type, p.description, p.resources, p.contact, p.website, COUNT(f.favorite) AS favorite_count FROM partnerdata p JOIN user_favorites f ON p.name = f.favorite GROUP BY p.name ORDER BY favorite_count DESC";
            } else {
                query = "SELECT name, type, description, resources, contact, website FROM partnerdata WHERE category = ?";
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                if (!"Everything".equals(category) && !"Trending".equals(category)) {
                    preparedStatement.setString(1, category);
                }

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        dataList.add(new Object[]{resultSet.getString("name"), resultSet.getString("type"), resultSet.getString("description"), resultSet.getString("resources"), resultSet.getString("contact"), resultSet.getString("website")});
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return dataList;
    }

    public JPanel createDataBox(Object[] data, String userId) {
        JPanel box = new JPanel();
        box.setLayout(new BorderLayout());
        box.setBackground(Color.DARK_GRAY);
        box.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel nameLabel = new JLabel((String) data[0]);
        JLabel nameLabel1 = new JLabel((String) "\nType: " + data[1]);
        JLabel nameLabel2 = new JLabel((String) "Press for more details");
        nameLabel.setForeground(Color.WHITE);
        nameLabel1.setForeground(Color.WHITE);
        nameLabel2.setForeground(Color.WHITE);

        JButton favoriteButton = new JButton("UpVote");
        favoriteButton.setPreferredSize(new Dimension(100, 30)); // Set the preferred button size

        // Check if the company is already favorited by the user
        if (isCompanyFavorited(userId, (String) data[0])) {
            favoriteButton.setText("Voted");
        }

        favoriteButton.addActionListener(e -> {
            String companyName = (String) data[0];

            if (favoriteButton.getText().equals("UpVote")) {
                favoriteButton.setText("Received Vote");
                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                favoriteButton.setText("Voted");
                addToFavorites(userId, companyName);
            } else {
                favoriteButton.setText("UnVoted");
                favoriteButton.setText("UpVote");
                removeFromFavorites(userId, companyName);
            }
        });

        box.add(nameLabel, BorderLayout.NORTH);
        box.add(nameLabel1, BorderLayout.SOUTH);
        box.add(nameLabel2, BorderLayout.CENTER);
        box.add(favoriteButton, BorderLayout.EAST);

        box.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                openDetailPage(data);
            }
        });

        return box;
    }

    // Check if a company is already favorited by the user
    private boolean isCompanyFavorited(String userId, String companyName) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
            String query = "SELECT * FROM user_favorites WHERE username = ? AND favorite = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, userId);
                preparedStatement.setString(2, companyName);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // Return true if the company is favorited, false otherwise
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Add a company to the user's favorites
    private void addToFavorites(String userId, String companyName) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
            String insertSQL = "INSERT INTO user_favorites (username, favorite) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                preparedStatement.setString(1, userId);
                preparedStatement.setString(2, companyName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Remove a company from the user's favorites
    private void removeFromFavorites(String userId, String companyName) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
            String deleteSQL = "DELETE FROM user_favorites WHERE username = ? AND favorite = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
                preparedStatement.setString(1, userId);
                preparedStatement.setString(2, companyName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void openDetailPage(Object[] data) {
        JPanel detailPanel = new JPanel(new BorderLayout());
        JTextArea detailsArea = new JTextArea("Name: " + data[0] + "\nType: " + data[1] + "\nDescription: " + data[2] + "\nResources: " + data[3] + "\nContact: " + data[4] + "\nWebsite: " + data[5]);
        detailsArea.setEditable(false);
        detailsArea.setBackground(Color.DARK_GRAY);
        detailsArea.setForeground(Color.WHITE);
        detailsArea.setLineWrap(true); // Enable line wrapping
        detailsArea.setWrapStyleWord(true); // Wrap lines at word boundaries

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(cardsPanel, "ListPanel"));

        detailPanel.add(detailsArea, BorderLayout.CENTER);
        detailPanel.add(backButton, BorderLayout.SOUTH);
        detailPanel.setBackground(Color.BLACK);

        cardsPanel.add(detailPanel, "DetailPanel");
        cardLayout.show(cardsPanel, "DetailPanel");
    }

    private void addEventListeners() {
        searchButton.addActionListener(e -> performSearch());
        aiSearchButton.addActionListener(e -> loadAISearchPanel());
        aiSearchPageButton.addActionListener(e -> performAISearch());
        helpbutton.addActionListener(e -> loadHelpPanel());
        reportButton.addActionListener(e -> loadReportPanel());
    }

    private void loadReportPanel() {
        ReportPanel reportPanel = new ReportPanel(this);
        cardsPanel.add(reportPanel, "ReportPanel");
        cardLayout.show(cardsPanel, "ReportPanel");
    }

    private void loadHelpPanel() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new Helppage().getPanel());
        frame.revalidate();
        System.out.println("Help button pressed");
    }

    private static JPanel helppage = null;

    public JPanel gethelppage() {
        if (helppage == null) {
            helppage = new Helppage().getPanel();
        }
        return helppage;
    }

    private void performSearch() {
        String id = getUserId();
        String searchText = searchField.getText().trim().toLowerCase();
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex != -1) {
            Component selectedComponent = tabbedPane.getComponentAt(selectedIndex);
            if (selectedComponent instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) selectedComponent;
                JPanel panel = (JPanel) scrollPane.getViewport().getView();
                panel.removeAll();

                List<Object[]> dataList = loadData("Everything");
                for (Object[] data : dataList) {
                    String name = ((String) data[0]).toLowerCase();
                    String type = ((String) data[1]).toLowerCase();
                    String description = ((String) data[2]).toLowerCase();
                    String resources = ((String) data[3]).toLowerCase();
                    String contact = ((String) data[4]).toLowerCase();
                    String website = ((String) data[5]).toLowerCase();

                    if (name.contains(searchText) || type.contains(searchText) || description.contains(searchText)) {
                        panel.add(createDataBox(data, id));
                    }
                }

                panel.revalidate();
                panel.repaint();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Homepage().showUI());
    }

    public void showUI() {
        JFrame frame = new JFrame("Homepage");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setContentPane(new Homepage());
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setVisible(true);
    }

    public JPanel getPanel() {
        return this;
    }

    // Method to fetch all partner data
    public List<Object[]> getAllPartners() {
        return loadData("Everything");
    }
}

class ReportPanel extends JPanel {
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextArea resourcesArea;
    private JTextArea contactArea;
    private JTextArea websiteArea;
    private JButton generateButton;
    private JButton selectPartnerButton;
    private JRadioButton pdfButton;
    private JRadioButton jpgButton;
    private ButtonGroup formatGroup;
    private Homepage homepage;

    public ReportPanel(Homepage homepage) {
        this.homepage = homepage;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.BLACK);

        selectPartnerButton = new JButton("Select Partner");
        selectPartnerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectPartnerButton.addActionListener(e -> selectPartner());

        add(selectPartnerButton);

        add(createInputField("Name:", nameField = new JTextField(30)));
        add(createInputArea("Description:", descriptionArea = new JTextArea(5, 30)));
        add(createInputArea("Resources:", resourcesArea = new JTextArea(5, 30)));
        add(createInputArea("Contact:", contactArea = new JTextArea(5, 30)));
        add(createInputArea("Website:", websiteArea = new JTextArea(5, 30)));

        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formatPanel.setBackground(Color.BLACK);
        JLabel formatLabel = new JLabel("Select Format:");
        formatLabel.setForeground(Color.WHITE);
        formatPanel.add(formatLabel);

        pdfButton = new JRadioButton("PDF");
        pdfButton.setForeground(Color.WHITE);
        pdfButton.setBackground(Color.BLACK);
        jpgButton = new JRadioButton("JPG");
        jpgButton.setForeground(Color.WHITE);
        jpgButton.setBackground(Color.BLACK);

        formatGroup = new ButtonGroup();
        formatGroup.add(pdfButton);
        formatGroup.add(jpgButton);

        formatPanel.add(pdfButton);
        formatPanel.add(jpgButton);

        add(formatPanel);

        generateButton = new JButton("Generate Report");
        generateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        generateButton.addActionListener(e -> generateReport());

        add(Box.createVerticalStrut(10)); // Spacer
        add(generateButton);
    }

    private JPanel createInputField(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.BLACK);

        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        panel.add(label);

        textField.setForeground(Color.WHITE);
        textField.setBackground(Color.DARK_GRAY);
        panel.add(textField);

        return panel;
    }

    private JPanel createInputArea(String labelText, JTextArea textArea) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);

        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        panel.add(label);

        textArea.setForeground(Color.WHITE);
        textArea.setBackground(Color.DARK_GRAY);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane);

        return panel;
    }

    private void selectPartner() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Select Partner", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());

        List<Object[]> partners = homepage.getAllPartners();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Object[] partner : partners) {
            listModel.addElement((String) partner[0]);
        }

        JList<String> partnerList = new JList<>(listModel);
        partnerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(partnerList);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(e -> {
            int selectedIndex = partnerList.getSelectedIndex();
            if (selectedIndex != -1) {
                Object[] selectedPartner = partners.get(selectedIndex);
                populateFields(selectedPartner);
                dialog.dispose();
            }
        });

        dialog.add(selectButton, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void populateFields(Object[] data) {
        nameField.setText((String) data[0]);
        descriptionArea.setText((String) data[2]);
        resourcesArea.setText((String) data[3]);
        contactArea.setText((String) data[4]);
        websiteArea.setText((String) data[5]);
    }

    private void generateReport() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String resources = resourcesArea.getText().trim();
        String contact = contactArea.getText().trim();
        String website = websiteArea.getText().trim();

        String reportContent = "Report for Partner: " + name + "\n\n" +
                "Description:\n" + description + "\n\n" +
                "Resources:\n" + resources + "\n\n" +
                "Contact:\n" + contact + "\n\n" +
                "Website:\n" + website + "\n\n";

        if (pdfButton.isSelected()) {
            saveAsPDF(reportContent);
        } else if (jpgButton.isSelected()) {
            saveAsJPG(reportContent);
        }
    }

    private void saveAsPDF(String content) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new File(file.getParentFile(), file.getName() + ".pdf");
            }

            Document document = new Document();
            try {
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                document.add(new Paragraph(content));
                document.close();
                JOptionPane.showMessageDialog(this, "PDF report generated successfully!");
            } catch (DocumentException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveAsJPG(String content) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("JPG Files", "jpg"));
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".jpg")) {
                file = new File(file.getParentFile(), file.getName() + ".jpg");
            }

            BufferedImage image = new BufferedImage(600, 800, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            g.setColor(Color.BLACK);
            g.drawString(content, 10, 25);
            g.dispose();

            try {
                ImageIO.write(image, "jpg", file);
                JOptionPane.showMessageDialog(this, "JPG report generated successfully!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
