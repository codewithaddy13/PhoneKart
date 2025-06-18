package com.phonekart.auth;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

public class BuyingPage extends JFrame implements ActionListener {
    JButton becomeSeller, searchButton, back;
    JTextField searchBar;
    JFrame previousFrame;
    String user_name;
    Conn conn;
    JPanel gridPanel;
    JPanel productsPanel;

    BuyingPage(JFrame previousFrame, String user_name) {
        this.user_name = user_name;      
        this.previousFrame = previousFrame;
        System.out.println("On Buying Page : " + " " + this.user_name);
        
        conn = new Conn();

        // Initialize ElasticSearch index in background
        initializeElasticSearch();

        setTitle("PhoneKart - Buy Phones");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));

        // Navbar Panel
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(new Color(30, 144, 255));
        navbar.setPreferredSize(new Dimension(getWidth(), 80));

        // Left side components panel
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        leftPanel.setBackground(new Color(30, 144, 255));

        back = new JButton("← Back");
        styleButton(back, new Color(100, 149, 237));
        leftPanel.add(back);

        ImageIcon logoIcon = new ImageIcon(ClassLoader.getSystemResource("images/logo.png"));
        Image scaledLogo = logoIcon.getImage().getScaledInstance(120, 50, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(scaledLogo));
        leftPanel.add(logo);

        leftPanel.add(Box.createHorizontalStrut(40));

        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(30, 144, 255));
        searchPanel.setLayout(new BorderLayout(5, 0));

        searchBar = new JTextField(25);
        searchBar.setFont(new Font("Arial", Font.PLAIN, 16));
        searchBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        searchBar.addActionListener(this);
        searchPanel.add(searchBar, BorderLayout.CENTER);

        searchButton = new JButton("🔍");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 18));
        searchButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        searchPanel.add(searchButton, BorderLayout.EAST);

        leftPanel.add(searchPanel);
        leftPanel.add(Box.createHorizontalStrut(40));

        becomeSeller = new JButton("Become a Seller");
        styleButton(becomeSeller, new Color(255, 69, 0));
        leftPanel.add(becomeSeller);

        navbar.add(leftPanel, BorderLayout.CENTER);

        // Right side - Clickable name label
        JButton nameButton = createNameButton();
        navbar.add(nameButton, BorderLayout.EAST);

        mainPanel.add(navbar, BorderLayout.NORTH);

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 248, 255));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        JLabel title = new JLabel("Browse Phones on PhoneKart");
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(new Color(30, 144, 255));
        titlePanel.add(title);

        JPanel contentContainer = new JPanel(new BorderLayout());
        contentContainer.add(titlePanel, BorderLayout.NORTH);

        productsPanel = new JPanel(new GridBagLayout());
        productsPanel.setBackground(new Color(240, 248, 255));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.anchor = GridBagConstraints.CENTER;
        
        gridPanel = new JPanel(new GridLayout(0, 3, 30, 30));
        gridPanel.setBackground(new Color(240, 248, 255));
        
        loadProducts("SELECT * FROM products");
        
        productsPanel.add(gridPanel, gbc);
        
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(240, 248, 255));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        contentContainer.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(contentContainer, BorderLayout.CENTER);

        add(mainPanel);

        back.addActionListener(this);
        searchButton.addActionListener(this);
        becomeSeller.addActionListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void initializeElasticSearch() {
        SwingWorker<Void, Void> indexWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    ElasticSearchIndexer.indexAllProducts();
                } catch (Exception e) {
                    System.err.println("ElasticSearch initialization failed: " + e.getMessage());
                    // Continue with SQL-only functionality
                }
                return null;
            }
        };
        indexWorker.execute();
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
            new ProfilePage(this, user_name);
        });
        
        return nameButton;
    }

    private String getUserFullName() {
        String fullName = user_name;
        
        try {
            String query = "SELECT first_name, last_name FROM users WHERE username = '" + user_name + "'";
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

    private void loadProducts(String query) {
        gridPanel.removeAll();
        
        try {
            ResultSet rs = conn.s.executeQuery(query);
            
            while (rs.next()) {
                gridPanel.add(createProductCard(rs));
            }
            
            if (gridPanel.getComponentCount() == 0) {
                JLabel noResults = new JLabel("No products found matching your search");
                noResults.setFont(new Font("Arial", Font.PLAIN, 18));
                gridPanel.add(noResults);
            }
            
            gridPanel.revalidate();
            gridPanel.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createProductCard(ResultSet rs) throws SQLException {
        int productId = rs.getInt("productid");
        String title = rs.getString("title");
        String brand = rs.getString("brand");
        String model = rs.getString("model");
        String ram = rs.getString("ram");
        String storage = rs.getString("storagespace");
        String screenSize = rs.getString("screen_size");
        String camera = rs.getString("camera_mp");
        int price = rs.getInt("price");
        Blob imageBlob = rs.getBlob("image");

        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setPreferredSize(new Dimension(300, 450));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        card.add(titlePanel, BorderLayout.NORTH);

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            if (imageBlob != null) {
                byte[] imageData = imageBlob.getBytes(1, (int)imageBlob.length());
                ImageIcon imageIcon = new ImageIcon(imageData);
                Image scaledImage = imageIcon.getImage().getScaledInstance(250, 200, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                imageLabel.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/phone_placeholder.jpg")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            imageLabel.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/phone_placeholder.jpg")));
        }
        card.add(imageLabel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel brandModelLabel = new JLabel(brand + " " + model);
        brandModelLabel.setFont(new Font("Arial", Font.BOLD, 16));
        brandModelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel specsLabel = new JLabel(
            "<html><center>" +
            ram + " RAM • " + 
            storage + " Storage<br>" +
            screenSize + " Screen • " +
            camera + " Camera" +
            "</center></html>"
        );
        specsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        specsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel("₹" + price);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 22));
        priceLabel.setForeground(new Color(30, 144, 255));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton buyButton = new JButton("Buy Now");
        styleButton(buyButton, new Color(30, 144, 255));
        buyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buyButton.setPreferredSize(new Dimension(120, 40));
        
        buyButton.addActionListener(e -> {
            new ProductDetailsPage(this, productId, user_name);
        });

        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(brandModelLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(specsLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(15));
        infoPanel.add(buyButton);

        card.add(infoPanel, BorderLayout.SOUTH);

        return card;
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
        if (ae.getSource() == becomeSeller) {
            SellPage sellPage = new SellPage(this, this.user_name);
            sellPage.setVisible(true);
        } else if (ae.getSource() == back) {
            this.setVisible(false);
            previousFrame.setVisible(true);
        } else if (ae.getSource() == searchButton || ae.getSource() == searchBar) {
            String searchText = searchBar.getText().trim();
            
            if (searchText.isEmpty() || searchText.equalsIgnoreCase("all brands")) {
                loadProducts("SELECT * FROM products");
            } else {
                performSearch(searchText);
            }
        }
    }

    private void performSearch(String searchText) {
        SwingWorker<List<Integer>, Void> searchWorker = new SwingWorker<List<Integer>, Void>() {
            @Override
            protected List<Integer> doInBackground() throws Exception {
                try {
                    return ElasticSearchOperations.searchProducts(searchText);
                } catch (Exception e) {
                    System.err.println("ElasticSearch failed: " + e.getMessage());
                    return null; // Indicate failure
                }
            }

            @Override
            protected void done() {
                try {
                    List<Integer> productIds = get();
                    if (productIds == null) {
                        // ElasticSearch failed, fallback to SQL
                        fallbackSqlSearch(searchText);
                    } else if (productIds.isEmpty()) {
                        loadProducts("SELECT * FROM products WHERE 1=0"); // Show empty results
                    } else {
                        // Build SQL query with the found product IDs
                        String ids = productIds.toString().replace("[", "").replace("]", "");
                        loadProducts("SELECT * FROM products WHERE productid IN (" + ids + ")");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    fallbackSqlSearch(searchText);
                }
            }
        };
        searchWorker.execute();
    }

    private void fallbackSqlSearch(String searchText) {
        String query = "SELECT * FROM products WHERE " +
                       "LOWER(title) LIKE '%" + searchText.toLowerCase() + "%' OR " +
                       "LOWER(brand) LIKE '%" + searchText.toLowerCase() + "%' OR " +
                       "LOWER(model) LIKE '%" + searchText.toLowerCase() + "%' OR " +
                       "LOWER(prod_description) LIKE '%" + searchText.toLowerCase() + "%'";
        loadProducts(query);
    }

    public static void main(String[] args) {
        new BuyingPage(null, "testuser");
    }
}
