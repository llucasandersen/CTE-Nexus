package org.lucassserver.com;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.lang.Thread.sleep;

public class Helppage extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JButton continueButton;



    public Helppage() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.DARK_GRAY); // Dark theme background

        // Adding different help sections
        cardPanel.add(createWelcomePanel(), "Welcome");
        cardPanel.add(createHowToUseSearchPanel(), "How to Use Search");
        cardPanel.add(createHowToUseAISearchPanel(), "How to Use AI Search");
        cardPanel.add(createOtherInfoPanel(), "Other Information");

        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.CENTER);
        add(createNavigationPanel(), BorderLayout.SOUTH);
    }


        private static JPanel homepage = null;

        public JPanel getHomepagePanel() {
            if (homepage == null) {
                homepage = new Homepage().getPanel();
            }
            return homepage;
        }

    private JPanel createNavigationPanel() {
        JPanel navigationPanel = new JPanel();
        navigationPanel.setBackground(Color.DARK_GRAY); // Consistent with the dark theme

        // Creating buttons for each help section
        JButton btnWelcome = new JButton("Welcome");
        styleButton(btnWelcome);
        btnWelcome.addActionListener(e -> cardLayout.show(cardPanel, "Welcome"));

        JButton btnHowToUseSearch = new JButton("How to Use Search");
        styleButton(btnHowToUseSearch);
        btnHowToUseSearch.addActionListener(e -> cardLayout.show(cardPanel, "How to Use Search"));

        JButton btnHowToUseAISearch = new JButton("How to Use AI Search");
        styleButton(btnHowToUseAISearch);
        btnHowToUseAISearch.addActionListener(e -> cardLayout.show(cardPanel, "How to Use AI Search"));

        JButton btnOtherInfo = new JButton("Other Information");
        styleButton(btnOtherInfo);
        btnOtherInfo.addActionListener(e -> cardLayout.show(cardPanel, "Other Information"));

        // Adding buttons to the navigation panel
        navigationPanel.add(btnWelcome);
        navigationPanel.add(btnHowToUseSearch);
        navigationPanel.add(btnHowToUseAISearch);
        navigationPanel.add(btnOtherInfo);

        return navigationPanel;
    }


    private JPanel createOtherInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.DARK_GRAY);

        // Informational Text
        JLabel infoLabel = new JLabel("<html><p>Now that you know the basic features, here are some other tips:</p>"
                + "<ul>"
                + "<li>You can filter the companies via the tabs on top of the home page.</li>"
                + "<li>You can press on the boxes for more detail.</li>"
                + "<li>There will also be an UpVote button which is community attributed. The more votes a company has, the higher it is on the trending tab for users who don't really know what they are looking for.</li>"
                + "<li>If you ever need help again, there will be a help button in the top right corner leading you back to this page.</li>"
                + "</ul>"
                + "<p>Good luck Searching!</p></html>");
        infoLabel.setForeground(Color.WHITE);
        panel.add(infoLabel);

        // Continue Button (Initially invisible)
        JButton continueButton = new JButton("Continue");
        styleButton(continueButton); // Apply the styling method
        continueButton.setVisible(false);
        getHomepagePanel();
        continueButton.addActionListener(e -> {
            // Navigate to the Homepage panel
            continueButton.setText("Please Wait...");
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(homepage);
            frame.revalidate();
        });

        // Timer to show the button after a delay
        Timer timer = new Timer(10000, e -> continueButton.setVisible(true));
        timer.setRepeats(false);
        timer.start();

        panel.add(continueButton);

        return panel;
    }

    private JPanel createHowToUseAISearchPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.DARK_GRAY);

        // Instructional Text
        JLabel instructionLabel = new JLabel("<html><p>Okay, now you know how to manually search. Let's move on to AI Search. It will give you the best results from your search and the data in the database. For example, ask the AI: \"I need duct work on the school\".</p></html>");
        instructionLabel.setForeground(Color.WHITE);
        panel.add(instructionLabel);

        // AI Search Field
        JTextField searchField = new JTextField(20);
        searchField.setMaximumSize(new Dimension(searchField.getPreferredSize().width, 40));
        searchField.setForeground(Color.WHITE);
        searchField.setBackground(Color.DARK_GRAY);
        searchField.setBorder(new RoundedBorder(10));
        searchField.setCaretColor(Color.WHITE);
        searchField.setAlignmentX(Component.LEFT_ALIGNMENT); // Align field to the left
        panel.add(Box.createRigidArea(new Dimension(0, 5))); // Add a small spacer
        panel.add(searchField);

        // AI Search Button
        JButton aiSearchButton = new JButton("Search");
        styleButton(aiSearchButton);
        aiSearchButton.setAlignmentX(Component.LEFT_ALIGNMENT); // Align button to the left
        panel.add(aiSearchButton);

        // Example AI Response (Initially invisible)
        JEditorPane aiResponseExample = new JEditorPane();
        aiResponseExample.setContentType("text/html"); // Set content type to HTML
        aiResponseExample.setText("<html><body style='color: white;'>" +
                "<h2>AI Response:</h2>" +
                "<p>Hello, from the provided information, the best company for duct work on the school is <strong>Andersen Air</strong>.</p>" +
                "<p><strong>Andersen Air</strong> is a company that does HVAC work for Cleburne and does repairs on ducts and furnaces.</p>" +
                "<p><strong>NAME:</strong> Andersen Air<br>" +
                "<strong>TYPE:</strong> For-Profit<br>" +
                "<strong>RESOURCES:</strong> AC work, and duct repair<br>" +
                "<strong>CONTACT:</strong> office@andersenair.com<br>" +
                "<strong>WEBSITE:</strong> andersenair.com<br>" +
                "<strong>DESCRIPTION:</strong> Andersen Air is a company that does HVAC work for Cleburne and does repairs on ducts and furnaces.</p>" +
                "</body></html>");
        aiResponseExample.setEditable(false);
        aiResponseExample.setForeground(Color.WHITE);
        aiResponseExample.setBackground(Color.DARK_GRAY);
        aiResponseExample.setVisible(false);
        aiResponseExample.setAlignmentX(Component.LEFT_ALIGNMENT); // Align text area to the left
        JScrollPane aiResponseScrollPane = new JScrollPane(aiResponseExample);
        aiResponseScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT); // Align scroll pane to the left
        aiResponseScrollPane.setMaximumSize(new Dimension(600, 200)); // Set maximum size of scroll pane

        // Continue Button (Initially invisible)
        JButton continueButton = new JButton("Continue");
        styleButton(continueButton);
        continueButton.setAlignmentX(Component.LEFT_ALIGNMENT); // Align button to the left
        continueButton.addActionListener(e -> cardLayout.show(cardPanel, "Other Information"));
        continueButton.setVisible(false);

        // AI Search Button Action Listener
        aiSearchButton.addActionListener(e -> {
            // Hide the search field and button, display the AI response
            searchField.setVisible(false);
            aiSearchButton.setVisible(false);
            aiResponseExample.setVisible(true);
            panel.add(aiResponseScrollPane); // Add scroll pane containing AI response

            // Timer to show the Continue button after a delay
            Timer timer = new Timer(3000, ev -> continueButton.setVisible(true));
            timer.setRepeats(false);
            timer.start();
        });

        panel.add(continueButton);

        return panel;
    }


    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY); // Dark theme background

        JLabel welcomeLabel = new JLabel("<html><h1>Welcome to the CTE Partners and Business App</h1><p>Before you start, let me run you through the basics.</p></html>");
        welcomeLabel.setForeground(Color.WHITE); // Text color for readability
        panel.add(welcomeLabel, BorderLayout.NORTH);

        continueButton = new JButton("Continue");
        styleButton(continueButton);
        continueButton.addActionListener(e -> cardLayout.show(cardPanel, "How to Use Search"));
        continueButton.setVisible(false); // Initially hide the button

        // Timer to show the button after a delay
        Timer timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                continueButton.setVisible(true);
            }
        });
        timer.setRepeats(false);
        timer.start();

        panel.add(continueButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createHowToUseSearchPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.DARK_GRAY);

        // Instructional Text
        JLabel instructionLabel = new JLabel("<html><p>Let's start with the basics: you can search for data manually via the search button. Try Searching yourself.</p></html>");
        instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align text to the left
        instructionLabel.setForeground(Color.WHITE);
        panel.add(instructionLabel);

        // Search Field
        JTextField searchField = new JTextField(20);
        searchField.setMaximumSize(new Dimension(searchField.getPreferredSize().width, 40));
        searchField.setForeground(Color.WHITE);
        searchField.setBackground(Color.DARK_GRAY);
        searchField.setBorder(new RoundedBorder(10));
        searchField.setCaretColor(Color.WHITE);
        searchField.setAlignmentX(Component.LEFT_ALIGNMENT); // Align field to the left
        panel.add(Box.createRigidArea(new Dimension(0, 5))); // Add a small spacer
        panel.add(searchField);

        // Search Button
        JButton searchButton = new JButton("Search");
        styleButton(searchButton); // Apply the styling method

        // Feedback Label (Initially invisible)
        JLabel feedbackLabel = new JLabel("<html><p>Good job! But what if you don't know what you're looking for but have a rough idea? Then this is where this comes in.</p></html>");
        feedbackLabel.setForeground(Color.WHITE);
        feedbackLabel.setVisible(false);

        // Continue Button (Initially invisible)
        JButton continueButton = new JButton("Continue");
        styleButton(continueButton); // Apply the styling method
        continueButton.addActionListener(e -> cardLayout.show(cardPanel, "How to Use AI Search"));
        continueButton.setVisible(false);

        // Search Button Action
        searchButton.addActionListener(e -> {
            feedbackLabel.setVisible(true);
            continueButton.setVisible(true);
        });

        panel.add(searchField);
        panel.add(searchButton);
        panel.add(feedbackLabel);
        panel.add(continueButton);

        return panel;
    }

    private void styleButton(JButton button) {
        button.setForeground(Color.WHITE);
        button.setBackground(Color.GRAY);
        button.setBorder(new RoundedBorder(10)); // Rounded border
    }

    public JPanel getPanel() {
        return this;
    }



    // Additional methods (createHowToUseSearchPanel, createHowToUseAISearchPanel, createOtherInfoPanel, createNavigationPanel) remain unchanged

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
}
