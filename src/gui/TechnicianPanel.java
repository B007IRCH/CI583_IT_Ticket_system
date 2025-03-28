// Combined and Enhanced Technician Panel
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
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.stream.Collectors;

public class TechnicianPanel extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private AVLTree avlTree;
    private Stack<Ticket> historyStack = new Stack<>();

    public TechnicianPanel() {
        setTitle("Technician - View Tickets");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 600);
        setLocationRelativeTo(null);

        avlTree = new AVLTree();
        TicketManager.getInstance().getAllTickets().forEach(avlTree::insert);

        DashboardPanel dashboard = new DashboardPanel();
        dashboard.setMinimumSize(new Dimension(200, 300));

        TicketQueuePanel queuePanel = new TicketQueuePanel();
        queuePanel.setMinimumSize(new Dimension(200, 300));

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
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setMinimumSize(new Dimension(600, 300));

        JSplitPane leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, dashboard, tableScrollPane);
        leftSplit.setResizeWeight(0.15);
        leftSplit.setContinuousLayout(true);
        leftSplit.setOneTouchExpandable(true);
        leftSplit.setDividerLocation(250);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplit, queuePanel);
        mainSplit.setResizeWeight(0.85);
        mainSplit.setContinuousLayout(true);
        mainSplit.setOneTouchExpandable(true);
        mainSplit.setDividerLocation(1100);

        JPanel topPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");
        JButton sortButton = new JButton("Sort by SLA & Priority");
        JButton exportButton = new JButton("Export Tickets");
        JButton testDataButton = new JButton("Generate Test Data");

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
        buttonPanel.add(exportButton);
        buttonPanel.add(testDataButton);

        topPanel.add(new JLabel("Search by ID or Name:"), BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> {
            new LoginScreen();
            dispose();
        });

        refreshButton.addActionListener(e -> {
            refreshTable();
            queuePanel.refreshQueue();
            dashboard.updateStats();
        });

        sortButton.addActionListener(e -> {
            List<Ticket> sorted = TicketManager.getInstance().getAllTickets();
            TicketSorter.mergeSort(sorted);
            populateTable(sorted);
        });

        exportButton.addActionListener(e -> {
            try (FileWriter writer = new FileWriter("tickets_export.csv")) {
                writer.write("ID,Client,Priority,Status,SLA\n");
                for (Ticket t : TicketManager.getInstance().getAllTickets()) {
                    writer.write(t.getId() + "," + t.getClientName() + "," + t.getPriority() + "," + t.getStatus() + "," + t.getSlaHours() + "\n");
                }
                JOptionPane.showMessageDialog(this, "Tickets exported successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
            }
        });

        testDataButton.addActionListener(e -> generateTestData(dashboard, queuePanel));

        searchButton.addActionListener(e -> searchTickets());

        avlSearchButton.addActionListener(e -> avlSearch(avlSearchBox));

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        String id = table.getValueAt(row, 0).toString();
                        Ticket ticket = TicketManager.getInstance().getAllTickets().stream()
                                .filter(t -> t.getId().equals(id)).findFirst().orElse(null);
                        if (ticket != null) {
                            historyStack.push(ticket);
                            new TicketDetailView(ticket, TechnicianPanel.this::refreshTable);
                        }
                    }
                }
            }
        });

        add(topPanel, BorderLayout.NORTH);
        add(mainSplit, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);

        refreshTable();
        setVisible(true);
    }

    private void refreshTable() {
        populateTable(TicketManager.getInstance().getAllTickets());
    }

    private void populateTable(List<Ticket> tickets) {
        tableModel.setRowCount(0);
        for (Ticket ticket : tickets) {
            tableModel.addRow(new Object[]{ticket.getId(), ticket.getClientName(), ticket.getPriority(), ticket.getStatus(), ticket.getSlaHours()});
        }
    }

    private void generateTestData(DashboardPanel dashboard, TicketQueuePanel queuePanel) {
        String[] names = {"Alice", "Bob", "Charlie", "Dana", "Eli", "Faye", "George"};
        String[] priorities = {"Critical", "Medium", "Low"};
        String[] statuses = {"Open", "Pending", "Closed"};
        Random rand = new Random();

        for (int i = 0; i < 10; i++) {
            String name = names[rand.nextInt(names.length)];
            String issue = "Issue description " + (i + 1);
            String priority = priorities[rand.nextInt(priorities.length)];
            String status = statuses[rand.nextInt(statuses.length)];
            int sla = rand.nextInt(48) + 1;
            Ticket ticket = new Ticket(name, issue, priority, sla);
            ticket.setStatus(status);
            TicketManager.getInstance().addTicket(ticket);
            avlTree.insert(ticket);
        }
        refreshTable();
        queuePanel.refreshQueue();
        dashboard.updateStats();
    }

    private void searchTickets() {
        String keyword = searchField.getText().trim().toLowerCase();
        List<Ticket> filtered = TicketManager.getInstance().getAllTickets().stream()
                .filter(t -> t.getId().toLowerCase().contains(keyword) || t.getClientName().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
        populateTable(filtered);
    }

    private void avlSearch(JComboBox<String> avlSearchBox) {
        int sla = Integer.parseInt((String) avlSearchBox.getSelectedItem());
        Ticket found = avlTree.search(sla);
        if (found != null) {
            JOptionPane.showMessageDialog(this, "Ticket Found via AVL:\nID: " + found.getId() + "\nClient: " + found.getClientName() + "\nSLA: " + found.getSlaHours());
        } else {
            JOptionPane.showMessageDialog(this, "No ticket found with that SLA value.");
        }
    }

    static class PriorityCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String priority = table.getValueAt(row, 2).toString();

            if (column == 2) {
                if (priority.equalsIgnoreCase("Critical")) {
                    c.setBackground(new Color(255, 102, 102));
                } else if (priority.equalsIgnoreCase("Medium")) {
                    c.setBackground(new Color(255, 255, 153));
                } else if (priority.equalsIgnoreCase("Low")) {
                    c.setBackground(new Color(153, 255, 153));
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
