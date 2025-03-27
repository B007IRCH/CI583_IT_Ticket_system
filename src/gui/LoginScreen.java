package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame {

    public LoginScreen() {
        setTitle("IT Ticketing System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 200);
        setLocationRelativeTo(null); // Center on screen

        // Main panel
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        // Title
        JLabel title = new JLabel("Welcome to the IT Ticketing System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        panel.add(title, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setLayout(new FlowLayout());

        JButton clientLogin = new JButton("Client Login");
        JButton techLogin = new JButton("Technician Login");

        // Button styles
        clientLogin.setFocusPainted(false);
        techLogin.setFocusPainted(false);
        clientLogin.setPreferredSize(new Dimension(140, 30));
        techLogin.setPreferredSize(new Dimension(140, 30));

        buttonPanel.add(clientLogin);
        buttonPanel.add(techLogin);

        panel.add(buttonPanel, BorderLayout.CENTER);
        add(panel);

        // Action listeners
        clientLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ClientPanel();
                dispose();
            }
        });

        techLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TechnicianPanel();
                dispose();
            }
        });

        setVisible(true);
    }
}