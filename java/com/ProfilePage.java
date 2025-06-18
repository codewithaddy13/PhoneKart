package com.phonekart.auth;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;

public class ProfilePage extends JFrame implements ActionListener {
    private JFrame previousFrame;
    private String username;
    private Conn conn;
    
    private JTextField firstNameField, lastNameField, usernameField, contactField;
    private JPasswordField passwordField;
    private JButton saveButton, backButton;

    public ProfilePage(JFrame previousFrame, String username) {
        this.previousFrame = previousFrame;
        this.username = username;
        this.conn = new Conn();

        setTitle("PhoneKart - User Profile");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255));

        // Navbar Panel
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(new Color(30, 144, 255));
        navbar.setPreferredSize(new Dimension(getWidth(), 70));

        // Back Button
        backButton = new JButton("← Back");
        styleButton(backButton, new Color(100, 149, 237));
        backButton.addActionListener(this);
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        leftPanel.setBackground(new Color(30, 144, 255));
        leftPanel.add(backButton);
        
        // Logo
        ImageIcon logoIcon = new ImageIcon(ClassLoader.getSystemResource("images/logo.png"));
        Image scaledLogo = logoIcon.getImage().getScaledInstance(100, 40, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(scaledLogo));
        leftPanel.add(logo);
        
        // Title
        JLabel title = new JLabel("Edit Profile");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        leftPanel.add(title);
        
        navbar.add(leftPanel, BorderLayout.CENTER);
        add(navbar, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Profile Picture Panel
        JPanel picturePanel = new JPanel(new BorderLayout());
        picturePanel.setBackground(new Color(240, 248, 255));
        
        JLabel pictureLabel = new JLabel();
        pictureLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pictureLabel.setPreferredSize(new Dimension(150, 150));
        pictureLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        // Try to load user's profile picture or use default
        try {
            ImageIcon defaultIcon = new ImageIcon(ClassLoader.getSystemResource("images/profile_default.png"));
            Image scaledImage = defaultIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            pictureLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            pictureLabel.setText("No Image");
        }
        
        JButton changePictureButton = new JButton("Change Picture");
        styleButton(changePictureButton, new Color(30, 144, 255));
        changePictureButton.setPreferredSize(new Dimension(150, 30));
        
        picturePanel.add(pictureLabel, BorderLayout.CENTER);
        picturePanel.add(changePictureButton, BorderLayout.SOUTH);
        
        gbc.gridwidth = 1;
        gbc.gridheight = 6;
        mainPanel.add(picturePanel, gbc);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formGbc.insets = new Insets(10, 10, 10, 10);
        formGbc.anchor = GridBagConstraints.WEST;

        // Load user data
        String[] userData = loadUserData();
        
        // First Name
        formPanel.add(createLabel("First Name:"), formGbc);
        formGbc.gridx++;
        firstNameField = createTextField(userData[0]);
        formPanel.add(firstNameField, formGbc);
        
        // Last Name
        formGbc.gridx = 0;
        formGbc.gridy++;
        formPanel.add(createLabel("Last Name:"), formGbc);
        formGbc.gridx++;
        lastNameField = createTextField(userData[1]);
        formPanel.add(lastNameField, formGbc);
        
        // Username
        formGbc.gridx = 0;
        formGbc.gridy++;
        formPanel.add(createLabel("Username:"), formGbc);
        formGbc.gridx++;
        usernameField = createTextField(userData[2]);
        usernameField.setEditable(false); // Username shouldn't be editable
        usernameField.setBackground(new Color(240, 240, 240));
        formPanel.add(usernameField, formGbc);
        
        // Password
        formGbc.gridx = 0;
        formGbc.gridy++;
        formPanel.add(createLabel("Password:"), formGbc);
        formGbc.gridx++;
        passwordField = new JPasswordField(userData[3]);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        passwordField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(passwordField, formGbc);
        
        // Contact
        formGbc.gridx = 0;
        formGbc.gridy++;
        formPanel.add(createLabel("Contact:"), formGbc);
        formGbc.gridx++;
        contactField = createTextField(userData[4]);
        formPanel.add(contactField, formGbc);
        
        // Save Button
        formGbc.gridx = 1;
        formGbc.gridy++;
        formGbc.anchor = GridBagConstraints.EAST;
        saveButton = new JButton("Save Changes");
        styleButton(saveButton, new Color(30, 144, 255));
        saveButton.addActionListener(this);
        formPanel.add(saveButton, formGbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        mainPanel.add(formPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private String[] loadUserData() {
        String[] userData = new String[5]; // first_name, last_name, username, password, contact
        
        try {
            String query = "SELECT first_name, last_name, username, pword, contact FROM users WHERE username = '" + username + "'";
            ResultSet rs = conn.s.executeQuery(query);
            
            if (rs.next()) {
                userData[0] = rs.getString("first_name");
                userData[1] = rs.getString("last_name");
                userData[2] = rs.getString("username");
                userData[3] = rs.getString("pword");
                userData[4] = rs.getString("contact");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading user data", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return userData;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        return label;
    }

    private JTextField createTextField(String text) {
        JTextField textField = new JTextField(text);
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        textField.setPreferredSize(new Dimension(250, 35));
        return textField;
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == backButton) {
            this.dispose();
        } else if (ae.getSource() == saveButton) {
            saveUserData();
        }
    }

    private void saveUserData() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String contact = contactField.getText().trim();
        
        if (firstName.isEmpty() && lastName.isEmpty() && password.isEmpty() && contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String query = "UPDATE users SET " +
                           "first_name = '" + firstName + "', " +
                           "last_name = '" + lastName + "', " +
                           "pword = '" + password + "', " +
                           "contact = '" + contact + "' " +
                           "WHERE username = '" + username + "'";
            
            int rowsAffected = conn.s.executeUpdate(query);
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating profile", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new ProfilePage(null, "testuser");
    }
}