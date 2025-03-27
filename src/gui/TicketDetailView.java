package gui;

import model.Ticket;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TicketDetailView extends JFrame {
    public TicketDetailView(Ticket ticket, Runnable onCloseCallback) {
        setTitle("Ticket Detail - " + ticket.getId());
        setSize(450, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("ID: " + ticket.getId()));
        panel.add(new JLabel("Client: " + ticket.getClientName()));
        panel.add(new JLabel("Department: " + ticket.getDepartment()));
        panel.add(new JLabel("Issue: " + ticket.getIssue()));
        panel.add(new JLabel("Priority: " + ticket.getPriority()));
        panel.add(new JLabel("Status: " + ticket.getStatus()));
        panel.add(new JLabel("SLA (hrs): " + ticket.getSlaHours()));

        String submissionTime = ticket.getSubmissionTime() != null
                ? ticket.getSubmissionTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : "Not recorded";
        panel.add(new JLabel("Submitted At: " + submissionTime));

        // Show existing comments
        panel.add(new JLabel("Comments:"));
        JTextArea commentsDisplay = new JTextArea(5, 20);
        commentsDisplay.setEditable(false);
        for (String comment : ticket.getComments()) {
            commentsDisplay.append("- " + comment + "\n");
        }
        panel.add(new JScrollPane(commentsDisplay));

        // Add comment area
        JTextArea commentArea = new JTextArea(3, 20);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        panel.add(new JLabel("Add Comment:"));
        panel.add(new JScrollPane(commentArea));

        JButton closeTicketButton = new JButton("Close Ticket");
        JButton reopenTicketButton = new JButton("Reopen Ticket");
        JButton addCommentButton = new JButton("Add Comment");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeTicketButton);
        buttonPanel.add(reopenTicketButton);
        buttonPanel.add(addCommentButton);
        panel.add(buttonPanel);

        closeTicketButton.addActionListener(e -> {
            ticket.setStatus("Closed");
            JOptionPane.showMessageDialog(this, "Ticket marked as Closed.");
            if (onCloseCallback != null) onCloseCallback.run();
            dispose();
        });

        reopenTicketButton.addActionListener(e -> {
            ticket.setStatus("Open");
            JOptionPane.showMessageDialog(this, "Ticket reopened.");
            if (onCloseCallback != null) onCloseCallback.run();
            dispose();
        });

        addCommentButton.addActionListener(e -> {
            String comment = commentArea.getText().trim();
            if (!comment.isEmpty()) {
                ticket.addComment(comment);
                commentsDisplay.append("- " + comment + "\n");
                JOptionPane.showMessageDialog(this, "Comment added to ticket.");
                commentArea.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Comment cannot be empty.");
            }
        });

        add(panel);
        setVisible(true);
    }
}
