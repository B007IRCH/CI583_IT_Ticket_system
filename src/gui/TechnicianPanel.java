package gui;

import algorithms.TicketSorter;
import model.Ticket;
import model.TicketManager;
import structures.AVLTree;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

public class TechnicianPanel extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private AVLTree avlTree;

    public TechnicianPanel() {
        setTitle("Technician - View Tickets");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 550);
        setLocationRelativeTo(null);

        avlTree = new AVLTree();
        TicketManager.getInstance().getAllTickets().forEach(avlTree::insert);

        // Ticket Queue Sidebar
        TicketQueuePanel queuePanel = new TicketQueuePanel();
        queuePanel.setPreferredSize(new Dimension(200, 0));
        add(queuePanel, BorderLayout.EAST);

        String[] columnNames = {"ID", "Client Name", "Priority", "Status", "SLA (hrs)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setDefaultRenderer(Object.class, new PriorityCellRenderer());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");
        JButton sortButton = new JButton("Sort by SLA & Priority");

        JComboBox<String> avlSearchBox = new JComboBox<>();
        for (Ticket ticket : TicketManager.getInstance().getAllTickets()) {
            avlSearchBox.addItem(String.valueOf(ticket.getSlaHours()));
        }
        JButton avlSearchButton = new JButton("Search SLA (AVL)");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(searchButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(sortButton);
        buttonPanel.add(avlSearchBox);
        buttonPanel.add(avlSearchButton);

        topPanel.add(new JLabel("Search by ID or Name:"), BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JButton backButton = new JButton("Back to Login");
        add(backButton, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            new LoginScreen();
            dispose();
        });

        refreshButton.addActionListener(e -> {
            refreshTable();
            queuePanel.refreshQueue();
        });

        sortButton.addActionListener(e -> {
            List<Ticket> sorted = TicketManager.getInstance().getAllTickets();
            TicketSorter.mergeSort(sorted);
            populateTable(sorted);
        });

        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim().toLowerCase();
            List<Ticket> filtered = TicketManager.getInstance().getAllTickets().stream()
                    .filter(t -> t.getId().toLowerCase().contains(keyword)
                            || t.getClientName().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
            populateTable(filtered);
        });

        avlSearchButton.addActionListener(e -> {
            int sla = Integer.parseInt((String) avlSearchBox.getSelectedItem());
            Ticket found = avlTree.search(sla);
            if (found != null) {
                JOptionPane.showMessageDialog(this,
                        "Ticket Found via AVL:\n" +
                                "ID: " + found.getId() + "\n" +
                                "Client: " + found.getClientName() + "\n" +
                                "SLA: " + found.getSlaHours());
            } else {
                JOptionPane.showMessageDialog(this, "No ticket found with that SLA value.");
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        String id = table.getValueAt(row, 0).toString();
                        Ticket ticket = TicketManager.getInstance().getAllTickets().stream()
                                .filter(t -> t.getId().equals(id))
                                .findFirst().orElse(null);
                        if (ticket != null) {
                            new TicketDetailView(ticket, TechnicianPanel.this::refreshTable);
                        }
                    }
                }
            }
        });

        refreshTable();
        setVisible(true);
    }

    private void refreshTable() {
        List<Ticket> tickets = TicketManager.getInstance().getAllTickets();
        populateTable(tickets);
    }

    private void populateTable(List<Ticket> tickets) {
        tableModel.setRowCount(0);
        for (Ticket ticket : tickets) {
            tableModel.addRow(new Object[]{
                    ticket.getId(),
                    ticket.getClientName(),
                    ticket.getPriority(),
                    ticket.getStatus(),
                    ticket.getSlaHours()
            });
        }
    }

    static class PriorityCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String priority = table.getValueAt(row, 2).toString();

            if (column == 2) { // Only color the priority column
                if (priority.equalsIgnoreCase("Critical")) {
                    c.setBackground(new Color(255, 102, 102)); // Red
                } else if (priority.equalsIgnoreCase("Medium")) {
                    c.setBackground(new Color(255, 255, 153)); // Yellow
                } else if (priority.equalsIgnoreCase("Low")) {
                    c.setBackground(new Color(153, 255, 153)); // Green
                } else {
                    c.setBackground(Color.WHITE);
                }
                c.setForeground(Color.BLACK);
            } else {
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
            }

            return c;
        }
    }
}