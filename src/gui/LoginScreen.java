package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginScreen extends JFrame {

    public LoginScreen() {
        setTitle("IT Ticketing System - Login");
        setSize(400, 200);
        setLocationRelativeTo(null); // center on screen
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Welcome to the IT Ticketing System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton clientButton = new JButton("Client Login");
        JButton techButton = new JButton("Technician Login");

        clientButton.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(this, "Client Panel Coming Soon!");
        });

        techButton.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(this, "Technician Panel Coming Soon!");
        });

        buttonPanel.add(clientButton);
        buttonPanel.add(techButton);
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}
