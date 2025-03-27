package gui;

import model.Ticket;
import model.TicketManager;
import structures.TicketQueue;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class TicketQueuePanel extends JPanel {
    private DefaultListModel<String> queueListModel;
    private JList<String> queueList;

    public TicketQueuePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Ticket Queue"));

        queueListModel = new DefaultListModel<>();
        queueList = new JList<>(queueListModel);
        JScrollPane scrollPane = new JScrollPane(queueList);

        add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh Queue");
        refreshButton.addActionListener(e -> refreshQueue());
        add(refreshButton, BorderLayout.SOUTH);

        refreshQueue();
    }

    public void refreshQueue() {
        queueListModel.clear();
        TicketQueue queue = TicketManager.getInstance().getQueue();
        LinkedList<Ticket> tempQueue = new LinkedList<>();

        while (!queue.isEmpty()) {
            Ticket t = queue.dequeue();
            queueListModel.addElement(t.getId() + " | " + t.getClientName() + " | " + t.getPriority());
            tempQueue.add(t);
        }

        for (Ticket t : tempQueue) {
            queue.enqueue(t);
        }
    }
}
