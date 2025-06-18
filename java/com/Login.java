package com.phonekart.auth;

import java.awt.*;

import javax.swing.*;
import java.awt.event.*;
import java.sql.ResultSet;

public class Login extends JFrame implements ActionListener {

    JButton login, signUp;
    JTextField f1;
    JPasswordField f2;

    Login() {
        setTitle("PhoneKart");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-Screen Mode
        setLayout(null); // Using absolute positioning for precise placement

        // Create a layered pane to manage background and foreground components
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1920, 1080);
        add(layeredPane);

        // Background panel with stickers
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Set background color
                g2d.setColor(new Color(240, 248, 255)); // AliceBlue background
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw decorative elements (stickers)
                // Floating smartphones
                drawPhoneSticker(g2d, 100, 150, 80, 150, new Color(100, 149, 237)); // CornflowerBlue
                drawPhoneSticker(g2d, 1700, 300, 100, 180, new Color(255, 105, 180)); // HotPink
                drawPhoneSticker(g2d, 200, 600, 70, 130, new Color(60, 179, 113)); // MediumSeaGreen
                
                // Shopping cart stickers
                drawCartSticker(g2d, 1500, 150, 80, new Color(255, 165, 0)); // Orange
                drawCartSticker(g2d, 250, 400, 60, new Color(138, 43, 226)); // BlueViolet
                
                // Discount tags
                drawDiscountTag(g2d, 1600, 600, 120, 60, "20% OFF", new Color(255, 69, 0)); // OrangeRed
                drawDiscountTag(g2d, 300, 800, 100, 50, "SALE", new Color(0, 128, 0)); // Green
                
                // Abstract decorative elements
                drawBubble(g2d, 1300, 700, 50, new Color(173, 216, 230, 100)); // LightBlue with transparency
                drawBubble(g2d, 400, 200, 70, new Color(255, 192, 203, 100)); // Pink with transparency
            }
            
            private void drawPhoneSticker(Graphics2D g2d, int x, int y, int width, int height, Color color) {
                g2d.setColor(color);
                g2d.fillRoundRect(x, y, width, height, 30, 30);
                g2d.setColor(color.darker());
                g2d.drawRoundRect(x, y, width, height, 30, 30);
                
                // Screen
                g2d.setColor(Color.white);
                g2d.fillRoundRect(x+5, y+10, width-10, height-20, 20, 20);
                
                // Camera
                g2d.setColor(color.darker());
                g2d.fillOval(x+width/2-5, y+5, 10, 10);
            }
            
            private void drawCartSticker(Graphics2D g2d, int x, int y, int size, Color color) {
                g2d.setColor(color);
                // Cart body
                g2d.fillRect(x, y+size/2, size, size/3);
                // Wheels
                g2d.setColor(Color.black);
                g2d.fillOval(x+5, y+size/2+size/3-5, 10, 10);
                g2d.fillOval(x+size-15, y+size/2+size/3-5, 10, 10);
                // Handle
                g2d.setColor(color);
                g2d.drawArc(x-10, y+size/3, 20, 20, 0, 180);
            }
            
            private void drawDiscountTag(Graphics2D g2d, int x, int y, int width, int height, String text, Color color) {
                g2d.setColor(color);
                g2d.fillRoundRect(x, y, width, height, 15, 15);
                g2d.setColor(Color.white);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                g2d.drawString(text, x + (width - textWidth)/2, y + height/2 + fm.getAscent()/2 - 2);
                
                // Fold corner
                int[] xPoints = {x, x+20, x};
                int[] yPoints = {y, y, y+20};
                g2d.setColor(color.brighter());
                g2d.fillPolygon(xPoints, yPoints, 3);
            }
            
            private void drawBubble(Graphics2D g2d, int x, int y, int diameter, Color color) {
                g2d.setColor(color);
                g2d.fillOval(x, y, diameter, diameter);
            }
        };
        backgroundPanel.setBounds(0, 0, 1920, 1080);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        // Navbar with logo and sign up button
        JPanel navbar = new JPanel();
        navbar.setLayout(null);
        navbar.setBackground(new Color(30, 144, 255)); // DodgerBlue
        navbar.setBounds(0, 0, 1920, 90);
        navbar.setOpaque(true);
        layeredPane.add(navbar, JLayeredPane.PALETTE_LAYER);

        // Smaller logo
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("images/logo.png"));
        Image i2 = i1.getImage().getScaledInstance(180, 80, Image.SCALE_SMOOTH);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel logo = new JLabel(i3);
        logo.setBounds(20, 5, 177, 80);
        navbar.add(logo);

        // Sign Up button in navbar
        signUp = new JButton("Sign Up");
        signUp.setFont(new Font("Arial", Font.BOLD, 18));
        signUp.setBackground(new Color(0, 0, 139)); // DarkBlue
        signUp.setForeground(Color.WHITE);
        signUp.setBounds(1400, 25, 120, 40);
        signUp.addActionListener(this);
        navbar.add(signUp);

        // Centered Welcome text at the top
        JLabel welcomeText = new JLabel("𝕎𝕖𝕝𝕔𝕠𝕞𝕖 𝕥𝕠 ℙ𝕙𝕠𝕟𝕖𝕂𝕒𝕣𝕥");
        welcomeText.setFont(new Font("Serif", Font.BOLD, 45));
        welcomeText.setForeground(new Color(0, 102, 204));
        welcomeText.setBounds(500, 100, 600, 80);
        welcomeText.setOpaque(false);
        layeredPane.add(welcomeText, JLayeredPane.PALETTE_LAYER);

        // Smartphone-shaped login panel
        JPanel phonePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw phone body
                g2d.setColor(new Color(50, 50, 50));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 60, 60);
                
                // Draw screen
                g2d.setColor(new Color(230, 230, 250));
                g2d.fillRoundRect(20, 20, getWidth()-40, getHeight()-40, 40, 40);
                
                // Draw camera notch
                g2d.setColor(new Color(50, 50, 50));
                g2d.fillOval(getWidth()/2 - 30, 10, 60, 20);
            }
        };
        phonePanel.setLayout(null);
        phonePanel.setBounds(600, 250, 300, 500);
        phonePanel.setOpaque(false);
        layeredPane.add(phonePanel, JLayeredPane.PALETTE_LAYER);

        // Login components inside the phone screen
        JLabel uname = new JLabel("Username:");
        uname.setFont(new Font("Raleway", Font.BOLD, 18));
        uname.setBounds(40, 100, 220, 30);
        phonePanel.add(uname);

        f1 = new JTextField(20);
        f1.setFont(new Font("Arial", Font.PLAIN, 16));
        f1.setBounds(40, 140, 220, 35);
        phonePanel.add(f1);

        JLabel pword = new JLabel("Password:");
        pword.setFont(new Font("Raleway", Font.BOLD, 18));
        pword.setBounds(40, 200, 220, 30);
        phonePanel.add(pword);

        f2 = new JPasswordField(20);
        f2.setFont(new Font("Arial", Font.PLAIN, 16));
        f2.setBounds(40, 240, 220, 35);
        phonePanel.add(f2);

        login = new JButton("Log In");
        login.setBackground(new Color(0, 0, 139)); // DarkBlue
        login.setForeground(Color.WHITE);
        login.setFont(new Font("Arial", Font.BOLD, 20));
        login.setBounds(40, 320, 220, 45);
        login.addActionListener(this);
        phonePanel.add(login);

        // Home button at bottom (decorative)
        JPanel homeButton = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(200, 200, 200));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        homeButton.setBounds(110, 400, 80, 10);
        phonePanel.add(homeButton);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ac) {
        String password = new String(f2.getPassword());
        String user_name = f1.getText();
        if (ac.getSource() == login) {
            if(user_name.equals("")) {
                JOptionPane.showMessageDialog(this, "Please enter the username!");
            }
            else if(password.equals("")) {
                JOptionPane.showMessageDialog(this, "Please enter the password!");
            }
            else {
                try {
                    Conn c = new Conn();
                    String query = "SELECT * FROM users WHERE username = '" + user_name + "' AND pword = '" + password + "'";
                    
                    // Execute the query
                    ResultSet rs = c.s.executeQuery(query);
                    if(rs.next()) {
                        signUp.setText("Log Out");
                        signUp.removeActionListener(this);
                        signUp.addActionListener(e -> logoutAction());
                        new BuyingPage(this, user_name);
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "Invalid username/password!");
                        f1.setText("");
                        f2.setText("");
                    }

                } catch(Exception e) {
                    System.out.println(e);
                }
                
            }
        } else if (ac.getSource() == signUp) {
            this.setVisible(false);
            new SignUp(this);
        }
    }
    
    private void logoutAction() {
        int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Log Out", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            signUp.setText("Sign Up");
            signUp.removeActionListener(this);
            signUp.addActionListener(this);
            JOptionPane.showMessageDialog(this, "Logged out successfully!");
            f1.setText("");
            f2.setText("");
        }
    }
    
    public static void main(String[] args) {
        new Login();
    }
}



