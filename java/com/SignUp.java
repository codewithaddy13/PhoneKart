package com.phonekart.auth;

import java.awt.*;

import javax.swing.*;
import java.awt.event.*;

public class SignUp extends JFrame implements ActionListener {
    JTextField f1, f2, f3, f5, f7;
    JPasswordField f4, f6;
    JRadioButton male, female, other;
    JButton signUp, clear, back;
    ButtonGroup genderGroup;
    JFrame previousFrame; 

    SignUp(JFrame previousFrame) {
    	this.previousFrame = previousFrame; 
        setTitle("PhoneKart - Sign Up");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(null);

        // Logo
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("images/logo.png"));
        Image i2 = i1.getImage().getScaledInstance(200, 60, Image.SCALE_SMOOTH);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel logo = new JLabel(i3);
        logo.setBounds(20, 20, 200, 60);
        add(logo);

        // Title
        JLabel title = new JLabel("𝕊𝕚𝕘𝕟 𝕌𝕡 𝕗𝕠𝕣 ℙ𝕙𝕠𝕟𝕖𝕂𝕒𝕣𝕥");
        title.setFont(new Font("Serif", Font.BOLD, 42));
        title.setForeground(new Color(0, 102, 204));
        title.setBounds(500, 50, 600, 80);
        add(title);

        // Sign-Up Panel
        JPanel signUpPanel = new JPanel();
        signUpPanel.setLayout(null);
        signUpPanel.setBackground(new Color(230, 230, 250));
        signUpPanel.setBounds(500, 180, 500, 550);
        add(signUpPanel);

        // Labels and Fields
        JLabel fname = new JLabel("First Name:");
        fname.setFont(new Font("Raleway", Font.BOLD, 20));
        fname.setBounds(50, 30, 180, 30);
        signUpPanel.add(fname);

        f1 = new JTextField(20);
        f1.setFont(new Font("Arial", Font.PLAIN, 16));
        f1.setBounds(230, 30, 220, 30);
        signUpPanel.add(f1);

        JLabel lname = new JLabel("Last Name:");
        lname.setFont(new Font("Raleway", Font.BOLD, 20));
        lname.setBounds(50, 80, 180, 30);
        signUpPanel.add(lname);

        f2 = new JTextField(20);
        f2.setFont(new Font("Arial", Font.PLAIN, 16));
        f2.setBounds(230, 80, 220, 30);
        signUpPanel.add(f2);

        JLabel uname = new JLabel("Username:");
        uname.setFont(new Font("Raleway", Font.BOLD, 20));
        uname.setBounds(50, 130, 180, 30);
        signUpPanel.add(uname);

        f3 = new JTextField(20);
        f3.setFont(new Font("Arial", Font.PLAIN, 16));
        f3.setBounds(230, 130, 220, 30);
        signUpPanel.add(f3);

        JLabel pword = new JLabel("Password:");
        pword.setFont(new Font("Raleway", Font.BOLD, 20));
        pword.setBounds(50, 180, 180, 30);
        signUpPanel.add(pword);

        f4 = new JPasswordField(20);
        f4.setFont(new Font("Arial", Font.PLAIN, 16));
        f4.setBounds(230, 180, 220, 30);
        signUpPanel.add(f4);

        JLabel confirmPword = new JLabel("Confirm Password:");
        confirmPword.setFont(new Font("Raleway", Font.BOLD, 20));
        confirmPword.setBounds(50, 230, 180, 30);
        signUpPanel.add(confirmPword);

        f6 = new JPasswordField(20);
        f6.setFont(new Font("Arial", Font.PLAIN, 16));
        f6.setBounds(230, 230, 220, 30);
        signUpPanel.add(f6);

        JLabel age = new JLabel("Age:");
        age.setFont(new Font("Raleway", Font.BOLD, 20));
        age.setBounds(50, 280, 180, 30);
        signUpPanel.add(age);

        f7 = new JTextField(20);
        f7.setFont(new Font("Arial", Font.PLAIN, 16));
        f7.setBounds(230, 280, 220, 30);
        signUpPanel.add(f7);

        JLabel gender = new JLabel("Gender:");
        gender.setFont(new Font("Raleway", Font.BOLD, 20));
        gender.setBounds(50, 330, 180, 30);
        signUpPanel.add(gender);

        male = new JRadioButton("Male");
        male.setFont(new Font("Arial", Font.PLAIN, 16));
        male.setBounds(230, 330, 70, 30);
        signUpPanel.add(male);

        female = new JRadioButton("Female");
        female.setFont(new Font("Arial", Font.PLAIN, 16));
        female.setBounds(310, 330, 80, 30);
        signUpPanel.add(female);

        other = new JRadioButton("Other");
        other.setFont(new Font("Arial", Font.PLAIN, 16));
        other.setBounds(400, 330, 80, 30);
        signUpPanel.add(other);

        genderGroup = new ButtonGroup();
        genderGroup.add(male);
        genderGroup.add(female);
        genderGroup.add(other);

        JLabel contact = new JLabel("Contact:");
        contact.setFont(new Font("Raleway", Font.BOLD, 20));
        contact.setBounds(50, 380, 180, 30);
        signUpPanel.add(contact);

        f5 = new JTextField(20);
        f5.setFont(new Font("Arial", Font.PLAIN, 16));
        f5.setBounds(230, 380, 220, 30);
        signUpPanel.add(f5);

        // Buttons
        signUp = new JButton("Sign Up");
        signUp.setBackground(Color.BLACK);
        signUp.setForeground(Color.WHITE);
        signUp.setFont(new Font("Arial", Font.BOLD, 20));
        signUp.setBounds(100, 450, 120, 40);
        signUp.addActionListener(this);
        signUpPanel.add(signUp);

        clear = new JButton("Clear");
        clear.setBackground(Color.RED);
        clear.setForeground(Color.WHITE);
        clear.setFont(new Font("Arial", Font.BOLD, 20));
        clear.setBounds(250, 450, 120, 40);
        clear.addActionListener(this);
        signUpPanel.add(clear);
        
        back = new JButton("←"); // Back Button
        back.setBackground(Color.GRAY);
        back.setForeground(Color.WHITE);
        back.setFont(new Font("Arial", Font.BOLD, 10));
        back.setBounds(35, 200, 50, 50);
        back.addActionListener(this);
        add(back);

        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ac) {
        if (ac.getSource() == clear) {
            f1.setText(""); f2.setText(""); f3.setText(""); f4.setText(""); f5.setText(""); f6.setText(""); f7.setText(""); genderGroup.clearSelection();
        }
        else if (ac.getSource() == signUp) {
        	String fn = f1.getText();
        	String ln = f2.getText();
        	String gndr= "";
            
            if (male.isSelected()) {
                gndr = male.getText();
            } else if (female.isSelected()) {
                gndr = female.getText();
            } else if (other.isSelected()) {
                gndr = other.getText();
            }
            
            String password = new String(f4.getPassword());
            String confirmPassword = new String(f6.getPassword());
            String pw = "";

            if (password.equals(confirmPassword)) {
                pw =  password; // Return password if both match
            } else {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                pw = null; // Return null if they don't match
            }
            
            String un = f3.getText();
            String userage = f7.getText();
            String usercontact = f5.getText();
            
            try {
            	if(fn.equals("")) {
            		JOptionPane.showMessageDialog(null, "Name is Required!");
            	}
            	else if(un.equals("")) {
            		JOptionPane.showMessageDialog(null, "User Name is Required!");
            	}
            	else if(pw.equals("")) {
            		JOptionPane.showMessageDialog(null, "Password is Required!");
            	}
            	else if(usercontact.equals("")) {
            		JOptionPane.showMessageDialog(null, "Contact is Required!");
            	}
            	else if(pw.equals(null)) {
            		JOptionPane.showMessageDialog(null, "Password didn't match!");
            	}
            	else {
            		Conn c = new Conn();
            		String query = "INSERT INTO users (first_name, last_name, username, pword, age, gender, contact) VALUES ('" 
                            + fn + "', '" + ln + "', '" + un + "', '" + pw + "', '" + userage + "', '" + gndr + "', '" + usercontact + "')";

            		int rowsaff = c.s.executeUpdate(query);
            		if(rowsaff>0) {
            			JOptionPane.showMessageDialog(this, "Yout account is created successfully!");
            			f1.setText(""); f2.setText(""); f3.setText(""); f4.setText(""); f5.setText(""); f6.setText(""); f7.setText(""); genderGroup.clearSelection();
            			new BuyingPage(this, un);
            		}
            		else {
            			JOptionPane.showMessageDialog(this, "Error in creating you account");
            		}
            		
//            		JOptionPane.showMessageDialog(this, "Sign Up Successful!");
            	}
            } catch(Exception e) {
            	System.out.println(e);
            }
            

        }
        else if (ac.getSource() == back) {
            this.setVisible(false);
            previousFrame.setVisible(true);
        }
    }

    public static void main(String[] args) {
        new SignUp(new JFrame());
    }
}
