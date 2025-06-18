package com.phonekart.auth;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.regex.*;
import javax.swing.*;
import javax.xml.bind.JAXBException;
import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.*;
import org.jpmml.model.PMMLUtil;
import org.xml.sax.SAXException;

public class SellPage extends JFrame implements ActionListener {

    // UI Components
    private JTextField brand, model, battery, screenSize, storage, ram, camera, title, contact;
    private JTextArea description;
    private JButton uploadImage, sellPhone, clear, back;
    private JLabel imageLabel;
    private ImageIcon imageIcon;
    
    // Data fields
    private Frame previousFrame;
    private String user_name;
    private File selectedImageFile;
    private int productId;
    private Evaluator modelEvaluator;

    // PMML Field Name Constants
    private static final String PMML_BATTERY = "Battery Capacity (mAh)";
    private static final String PMML_SCREEN = "screen_size(inches)";
    private static final String PMML_STORAGE = "storage";
    private static final String PMML_RAM = "RAM";
    private static final String PMML_CAM1 = "cam_1";
    private static final String PMML_CAM2 = "cam_2";
    private static final String PMML_CAM3 = "cam_3";
    private static final String PMML_CAM4 = "cam_4";
    private static final String PMML_CAM5 = "cam_5";

    public SellPage(JFrame previousFrame, String user_name) {
        this.user_name = user_name;
        this.previousFrame = previousFrame;

        // Load PMML model
        try {
            InputStream pmmlStream = getClass().getResourceAsStream("/model.pmml");
            if (pmmlStream == null) {
                throw new FileNotFoundException("PMML model file not found in resources");
            }
            modelEvaluator = new LoadingModelEvaluatorBuilder()
                .load(pmmlStream)
                .build();
            modelEvaluator.verify();
            
            // Debug: Print model input fields
            System.out.println("Model loaded successfully. Input fields:");
            for (InputField field : modelEvaluator.getInputFields()) {
                System.out.println("- " + field.getName().getValue());
            }
        } catch (IOException | JAXBException | SAXException e) {
            System.err.println("Failed to load price prediction model: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Price prediction unavailable. Using default pricing.", 
                "Model Warning", 
                JOptionPane.WARNING_MESSAGE);
        }

        initializeUI();
    }

    private void initializeUI() {
        setTitle("PhoneKart - Sell Your Phone");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        // Main panel setup
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240));

        // Form panel with scrolling
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setPreferredSize(new Dimension(600, 1000));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        JScrollPane scrollPane = new JScrollPane(formPanel, 
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(620, 600));

        // Add form to main panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        mainPanel.add(scrollPane, gbc);
        add(mainPanel, BorderLayout.CENTER);

        // Title
        JLabel sellingPageTitle = new JLabel("Sell Your Phone");
        sellingPageTitle.setFont(new Font("Serif", Font.BOLD, 36));
        sellingPageTitle.setForeground(new Color(0, 102, 204));
        sellingPageTitle.setBounds(200, 20, 400, 50);
        formPanel.add(sellingPageTitle);

        // Form fields
        int y = 100;
        brand = addTextField("Brand:", y, formPanel);
        model = addTextField("Model:", y += 60, formPanel);
        battery = addTextField("Battery (mAh):", y += 60, formPanel);
        screenSize = addTextField("Screen (inches):", y += 60, formPanel);
        storage = addTextField("Storage (GB):", y += 60, formPanel);
        ram = addTextField("RAM (GB):", y += 60, formPanel);
        camera = addTextField("Camera (MP):", y += 60, formPanel);
        title = addTextField("Title:", y += 60, formPanel);

        // Description
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        descLabel.setBounds(50, y += 60, 180, 30);
        formPanel.add(descLabel);
        
        description = new JTextArea(3, 20);
        description.setFont(new Font("Arial", Font.PLAIN, 16));
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(description);
        descScroll.setBounds(230, y, 220, 60);
        formPanel.add(descScroll);

        // Contact
        contact = addTextField("Contact:", y += 80, formPanel);

        // Image upload
        uploadImage = new JButton("Upload Image");
        uploadImage.setBounds(230, y += 60, 220, 30);
        uploadImage.addActionListener(this);
        formPanel.add(uploadImage);

        imageLabel = new JLabel();
        imageLabel.setBounds(230, y += 50, 220, 150);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        formPanel.add(imageLabel);

        // Action buttons
        sellPhone = new JButton("POST");
        sellPhone.setBounds(50, y += 180, 150, 40);
        sellPhone.setFont(new Font("Arial", Font.BOLD, 16));
        sellPhone.addActionListener(this);
        formPanel.add(sellPhone);

        clear = new JButton("CLEAR");
        clear.setBounds(410, y, 150, 40);
        clear.setFont(new Font("Arial", Font.BOLD, 16));
        clear.addActionListener(this);
        formPanel.add(clear);

        // Back button
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setBackground(new Color(240, 240, 240));
        back = new JButton("←");
        back.setBackground(Color.GRAY);
        back.setForeground(Color.WHITE);
        back.setFont(new Font("Arial", Font.BOLD, 14));
        back.setPreferredSize(new Dimension(50, 50));
        back.addActionListener(this);
        backPanel.add(back);
        add(backPanel, BorderLayout.NORTH);
    }

    private JTextField addTextField(String label, int y, JPanel panel) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        jLabel.setBounds(50, y, 180, 30);
        panel.add(jLabel);

        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.setBounds(230, y, 220, 30);
        panel.add(textField);
        return textField;
    }

    private byte[] readImageFile(File file) throws IOException {
        try (InputStream is = new FileInputStream(file);
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            return os.toByteArray();
        }
    }

    private int[] parseCameraMP(String cameraStr) {
        int[] cameras = new int[5];
        if (cameraStr == null || cameraStr.isEmpty()) {
            return cameras;
        }
        
        Matcher matcher = Pattern.compile("(\\d+)").matcher(cameraStr);
        for (int i = 0; i < 5 && matcher.find(); i++) {
            try {
                cameras[i] = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                cameras[i] = 0;
            }
        }
        return cameras;
    }

    private int predictPrice(int battery, double screenSize, int storage, int ram,
                           int cam1, int cam2, int cam3, int cam4, int cam5) {
        if (modelEvaluator == null) {
            System.err.println("Model evaluator is null - using fallback pricing");
            return generateFallbackPrice();
        }
        
        try {
            Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();
            
            // Map all input fields according to PMML model expectations
            for (InputField inputField : modelEvaluator.getInputFields()) {
                FieldName fieldName = inputField.getName();
                String name = fieldName.getValue();
                Object value = null;
                
                if (name.equals(PMML_BATTERY)) {
                    value = battery;
                } else if (name.equals(PMML_SCREEN)) {
                    value = screenSize;
                } else if (name.equals(PMML_STORAGE)) {
                    value = storage;
                } else if (name.equals(PMML_RAM)) {
                    value = ram;
                } else if (name.equals(PMML_CAM1)) {
                    value = cam1;
                } else if (name.equals(PMML_CAM2)) {
                    value = cam2;
                } else if (name.equals(PMML_CAM3)) {
                    value = cam3;
                } else if (name.equals(PMML_CAM4)) {
                    value = cam4;
                } else if (name.equals(PMML_CAM5)) {
                    value = cam5;
                } else {
                    System.err.println("Unknown PMML field: " + name);
                }
                
                if (value != null) {
                    arguments.put(fieldName, inputField.prepare(value));
                }
            }
            
            // Evaluate model
            Map<FieldName, ?> results = modelEvaluator.evaluate(arguments);
            FieldName targetName = modelEvaluator.getTargetFields().get(0).getName();
            Object targetValue = results.get(targetName);
            
            if (targetValue instanceof Number) {
                int price = ((Number)targetValue).intValue();
                System.out.println("Model predicted price: " + price);
                return price;
            }
            
            System.err.println("Invalid prediction result: " + targetValue);
        } catch (Exception e) {
            System.err.println("Prediction error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return generateFallbackPrice();
    }

    private int generateFallbackPrice() {
        int price = 20000 + new Random().nextInt(20001);
        System.out.println("Using fallback price: " + price);
        return price;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == uploadImage) {
            handleImageUpload();
        } else if (e.getSource() == back) {
            this.setVisible(false);
            previousFrame.setVisible(true);
        } else if (e.getSource() == clear) {
            clearForm();
        } else if (e.getSource() == sellPhone) {
            handleSellPhone();
        }
    }

    private void handleImageUpload() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Phone Image");
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            imageIcon = new ImageIcon(selectedImageFile.getAbsolutePath());
            imageLabel.setIcon(new ImageIcon(imageIcon.getImage()
                .getScaledInstance(220, 150, Image.SCALE_SMOOTH)));
        }
    }

    private void clearForm() {
        brand.setText("");
        model.setText("");
        battery.setText("");
        screenSize.setText("");
        storage.setText("");
        ram.setText("");
        camera.setText("");
        title.setText("");
        description.setText("");
        contact.setText("");
        imageLabel.setIcon(null);
        selectedImageFile = null;
    }

    private void handleSellPhone() {
        try {
            // Validate form
            if (!validateForm()) return;
            
            // Parse inputs
            int batteryCap = parseNumber(battery.getText());
            double screenSizeVal = parseDouble(screenSize.getText());
            int storageVal = parseNumber(storage.getText());
            int ramVal = parseNumber(ram.getText());
            int[] cameras = parseCameraMP(camera.getText());
            
            System.out.println("Predicting with values:");
            System.out.println("- Battery: " + batteryCap + "mAh");
            System.out.println("- Screen: " + screenSizeVal + " inches");
            System.out.println("- Storage: " + storageVal + "GB");
            System.out.println("- RAM: " + ramVal + "GB");
            System.out.println("- Cameras: " + Arrays.toString(cameras));
            
            // Predict price
            int price = predictPrice(
                batteryCap, screenSizeVal, storageVal, ramVal,
                cameras[0], cameras[1], cameras[2], cameras[3], cameras[4]
            );
            
            // Save to database
            if (saveToDatabase(price)) {
                showPricePopup(price);
                clearForm();
            }
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for specifications");
        } catch (Exception e) {
            showError("System error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        if (brand.getText().trim().isEmpty()) {
            showError("Brand is required");
            return false;
        }
        if (model.getText().trim().isEmpty()) {
            showError("Model is required");
            return false;
        }
        if (battery.getText().trim().isEmpty()) {
            showError("Battery capacity is required");
            return false;
        }
        if (screenSize.getText().trim().isEmpty()) {
            showError("Screen size is required");
            return false;
        }
        if (storage.getText().trim().isEmpty()) {
            showError("Storage is required");
            return false;
        }
        if (ram.getText().trim().isEmpty()) {
            showError("RAM is required");
            return false;
        }
        if (camera.getText().trim().isEmpty()) {
            showError("Camera info is required");
            return false;
        }
        if (title.getText().trim().isEmpty()) {
            showError("Title is required");
            return false;
        }
        if (description.getText().trim().isEmpty()) {
            showError("Description is required");
            return false;
        }
        if (selectedImageFile == null) {
            showError("Please upload an image of your phone");
            return false;
        }
        return true;
    }

    private boolean saveToDatabase(int price) throws Exception {
        Conn c = new Conn();
        String contactNum = contact.getText().trim().isEmpty() ? 
            getSavedContact() : contact.getText();
        
        byte[] imageData = readImageFile(selectedImageFile);
        
        String sql = "INSERT INTO products (productid, brand, model, battery_capacity, " +
                     "screen_size, storagespace, ram, camera_mp, title, prod_description, " +
                     "contact, username, price, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = c.c.prepareStatement(sql)) {
            pstmt.setInt(1, new Random().nextInt(801) + 100); // productId
            pstmt.setString(2, brand.getText());
            pstmt.setString(3, model.getText());
            pstmt.setString(4, battery.getText());
            pstmt.setString(5, screenSize.getText());
            pstmt.setString(6, storage.getText());
            pstmt.setString(7, ram.getText());
            pstmt.setString(8, camera.getText());
            pstmt.setString(9, title.getText());
            pstmt.setString(10, description.getText());
            pstmt.setString(11, contactNum);
            pstmt.setString(12, user_name);
            pstmt.setInt(13, price);
            pstmt.setBytes(14, imageData);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Database insert affected " + rowsAffected + " rows");
            return rowsAffected > 0;
        }
    }

    private String getSavedContact() throws SQLException {
        Conn c = new Conn();
        try (ResultSet rs = c.s.executeQuery(
            "SELECT contact FROM users WHERE username = '" + user_name + "'")) {
            return rs.next() ? rs.getString(1) : "";
        }
    }

    private int parseNumber(String text) throws NumberFormatException {
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    private double parseDouble(String text) throws NumberFormatException {
        return Double.parseDouble(text.replaceAll("[^0-9.]", ""));
    }

    private void showPricePopup(int price) {
        JOptionPane.showMessageDialog(this, 
            String.format("<html><div style='font-size:14pt;padding:10px'>" +
                         "Your phone's estimated value:<br><center><b>₹%,d</b></center></div></html>", 
                         price),
            "Price Evaluation",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            "<html><div style='padding:10px'>" + message + "</div></html>",
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}



