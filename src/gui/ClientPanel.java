package gui;

import model.Ticket;
import model.TicketManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientPanel extends JFrame {
    public ClientPanel() {
        setTitle("Client - Submit Ticket");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Issue Description:"));
        JTextField issueField = new JTextField();
        panel.add(issueField);

        panel.add(new JLabel("Priority:"));
        String[] priorities = {"Low", "Medium", "Critical"};
        JComboBox<String> priorityBox = new JComboBox<>(priorities);
        panel.add(priorityBox);

        panel.add(new JLabel("SLA (hours):"));
        JTextField slaField = new JTextField();
        panel.add(slaField);

        panel.add(new JLabel("Submission Time:"));
        JLabel timeLabel = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        panel.add(timeLabel);

        JButton submitButton = new JButton("Submit Ticket");
        panel.add(submitButton);

        JButton backButton = new JButton("Back to Login");
        panel.add(backButton);

        add(panel);

        submitButton.addActionListener(e -> {
            String name = nameField.getText();
            String issue = issueField.getText();
            String priority = (String) priorityBox.getSelectedItem();
            String slaText = slaField.getText();

            if (name.isEmpty() || issue.isEmpty() || slaText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int slaHours = Integer.parseInt(slaText);
                Ticket ticket = new Ticket(name, issue, priority, slaHours);
                TicketManager.getInstance().addTicket(ticket);
                JOptionPane.showMessageDialog(this, "Ticket submitted successfully!");

                // Clear fields after submission
                nameField.setText("");
                issueField.setText("");
                slaField.setText("");
                priorityBox.setSelectedIndex(0);
                timeLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "SLA must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            new LoginScreen();
            dispose();
        });

        setVisible(true);
    }
}
