package com.phonekart.auth;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;

public class ProductDetailsPage extends JFrame {
    private JFrame previousFrame;
    private int productId;
    private String username;
    private Conn conn;

    public ProductDetailsPage(JFrame previousFrame, int productId, String username) {
        this.previousFrame = previousFrame;
        this.productId = productId;
        this.username = username;
        this.conn = new Conn();

        setTitle("PhoneKart - Product Details");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255));

        // Navbar Panel (updated to match BuyingPage)
        JPanel navbar = createNavbar();
        add(navbar, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        try {
            ResultSet rs = conn.s.executeQuery("SELECT * FROM products WHERE productid = " + productId);
            if (rs.next()) {
                mainPanel.add(createProductDetailsPanel(rs), BorderLayout.CENTER);
            } else {
                JOptionPane.showMessageDialog(this, "Product not found", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading product details", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }

        add(mainPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private JPanel createNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(new Color(30, 144, 255));
        navbar.setPreferredSize(new Dimension(getWidth(), 80));

        // Left side components panel
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        leftPanel.setBackground(new Color(30, 144, 255));

        // Back Button
        JButton back = new JButton("← Back");
        styleButton(back, new Color(100, 149, 237));
        back.addActionListener(e -> {
            this.dispose();
            previousFrame.setVisible(true);
        });
        leftPanel.add(back);

        // Logo - Made rectangular (wider)
        ImageIcon logoIcon = new ImageIcon(ClassLoader.getSystemResource("images/logo.png"));
        Image scaledLogo = logoIcon.getImage().getScaledInstance(120, 50, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(scaledLogo));
        leftPanel.add(logo);

        // Title
        JLabel title = new JLabel("Product Details");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        leftPanel.add(title);

        navbar.add(leftPanel, BorderLayout.CENTER);


        
//     // Right side - User's full name display
//        String fullName = getUserFullName();
//        JLabel usernameLabel = new JLabel(fullName);
//        usernameLabel.setFont(new Font("Arial", Font.BOLD, 20));
//        usernameLabel.setForeground(Color.WHITE);
//        usernameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
//        navbar.add(usernameLabel, BorderLayout.EAST);
        
     // Right side - Clickable name label
        JButton nameButton = createNameButton();
        navbar.add(nameButton, BorderLayout.EAST);

        

        return navbar;
    }
    
    private JButton createNameButton() {
        String fullName = getUserFullName();
        
        JButton nameButton = new JButton(fullName);
        nameButton.setFont(new Font("Arial", Font.BOLD, 20));
        nameButton.setForeground(Color.WHITE);
        nameButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        nameButton.setContentAreaFilled(false);
        nameButton.setFocusPainted(false);
        nameButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        nameButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                nameButton.setForeground(new Color(200, 200, 200));
            }
            public void mouseExited(MouseEvent e) {
                nameButton.setForeground(Color.WHITE);
            }
        });
        
        nameButton.addActionListener(e -> {
            new ProfilePage(this, username);
        });
        
        return nameButton;
    }
    
 // Method to get user's full name from database
    private String getUserFullName() {
        String fullName = username; // Default to username if we can't get the name
        
        try {
            String query = "SELECT first_name, last_name FROM users WHERE username = '" + username + "'";
            ResultSet rs = conn.s.executeQuery(query);
            
            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                fullName = firstName + " " + lastName;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return fullName;
    }

    private JPanel createProductDetailsPanel(ResultSet rs) throws SQLException {
        JPanel mainPanel = new JPanel(new BorderLayout(30, 30));
        mainPanel.setBackground(new Color(240, 248, 255));

        // Left panel - Image
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        imagePanel.setPreferredSize(new Dimension(500, 500));

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            Blob imageBlob = rs.getBlob("image");
            if (imageBlob != null) {
                byte[] imageData = imageBlob.getBytes(1, (int)imageBlob.length());
                ImageIcon imageIcon = new ImageIcon(imageData);
                Image scaledImage = imageIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                imageLabel.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/phone_placeholder.jpg")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            imageLabel.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/phone_placeholder.jpg")));
        }
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // Right panel - Details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        // Title
        JLabel titleLabel = new JLabel(rs.getString("title"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Brand and Model
        JLabel brandModelLabel = new JLabel(rs.getString("brand") + " " + rs.getString("model"));
        brandModelLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        brandModelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandModelLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Price
        JLabel priceLabel = new JLabel("₹" + rs.getInt("price"));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 30));
        priceLabel.setForeground(new Color(30, 144, 255));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Specifications
        JPanel specsPanel = new JPanel();
        specsPanel.setLayout(new GridLayout(0, 2, 15, 15));
        specsPanel.setBackground(Color.WHITE);
        specsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        specsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        addSpecItem(specsPanel, "Screen Size", rs.getString("screen_size"));
        addSpecItem(specsPanel, "RAM", rs.getString("ram"));
        addSpecItem(specsPanel, "Storage", rs.getString("storagespace"));
        addSpecItem(specsPanel, "Battery", rs.getString("battery_capacity"));
        addSpecItem(specsPanel, "Camera", rs.getString("camera_mp"));
        addSpecItem(specsPanel, "Seller", rs.getString("username"));

        // Description
        JLabel descLabel = new JLabel("<html><body style='width: 400px'>" + rs.getString("prod_description") + "</body></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Contact Button
        JButton contactButton = new JButton("Contact Seller");
        styleButton(contactButton, new Color(30, 144, 255));
        contactButton.setPreferredSize(new Dimension(200, 50));
        contactButton.addActionListener(e -> {
            try {
                JOptionPane.showMessageDialog(this, 
                    "Contact seller at: " + rs.getString("contact"), 
                    "Seller Contact", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (HeadlessException | SQLException e1) {
                e1.printStackTrace();
            }
        });

        // Buy Button
        JButton buyButton = new JButton("Buy Now");
        styleButton(buyButton, new Color(255, 69, 0));
        buyButton.setPreferredSize(new Dimension(200, 50));
        buyButton.addActionListener(e -> {
            try {
                JOptionPane.showMessageDialog(this, 
                    "Purchase initiated for " + rs.getString("title"), 
                    "Purchase", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (HeadlessException | SQLException e1) {
                e1.printStackTrace();
            }
        });

        buttonPanel.add(contactButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(buyButton);

        // Add components to details panel
        detailsPanel.add(titleLabel);
        detailsPanel.add(brandModelLabel);
        detailsPanel.add(priceLabel);
        detailsPanel.add(specsPanel);
        detailsPanel.add(descLabel);
        detailsPanel.add(Box.createVerticalStrut(20));
        detailsPanel.add(buttonPanel);

        // Add panels to main panel
        mainPanel.add(imagePanel, BorderLayout.WEST);
        mainPanel.add(detailsPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private void addSpecItem(JPanel panel, String label, String value) {
        JLabel specLabel = new JLabel(label + ":");
        specLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel specValue = new JLabel(value);
        specValue.setFont(new Font("Arial", Font.PLAIN, 16));
        
        panel.add(specLabel);
        panel.add(specValue);
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
}




