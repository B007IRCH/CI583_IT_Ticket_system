package gui;

import model.Ticket;
import model.TicketManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DashboardPanel extends JPanel {
    private JLabel totalLabel, avgSlaLabel;
    private PieChartPanel pieChartPanel;
    private BarChartPanel barChartPanel;
    private StatusChartPanel statusChartPanel;
    private JComboBox<String> priorityFilter;

    public DashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Ticket Dashboard"));

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        totalLabel = new JLabel();
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        avgSlaLabel = new JLabel();
        avgSlaLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel labelPanel = new JPanel(new GridLayout(2, 1));
        labelPanel.add(totalLabel);
        labelPanel.add(avgSlaLabel);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by Priority:"));
        priorityFilter = new JComboBox<>(new String[]{"All", "Critical", "Medium", "Low"});
        priorityFilter.addActionListener(this::applyFilter);
        filterPanel.add(priorityFilter);

        statsPanel.add(labelPanel);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(filterPanel);

        pieChartPanel = new PieChartPanel();
        barChartPanel = new BarChartPanel();
        statusChartPanel = new StatusChartPanel();

        JScrollPane pieScroll = new JScrollPane(pieChartPanel);
        JScrollPane barScroll = new JScrollPane(barChartPanel);
        JScrollPane statusScroll = new JScrollPane(statusChartPanel);

        JTabbedPane chartTabs = new JTabbedPane(JTabbedPane.LEFT);
        chartTabs.addTab("Status Chart", statusScroll);
        chartTabs.addTab("Priority Pie", pieScroll);
        chartTabs.addTab("SLA Bar", barScroll);

        chartTabs.setPreferredSize(new Dimension(600, 400));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, statsPanel, chartTabs);
        splitPane.setResizeWeight(0.2);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);

        add(splitPane, BorderLayout.CENTER);

        addInteractiveTooltips(pieChartPanel, "priority");
        addInteractiveTooltips(statusChartPanel, "status");
        addInteractiveTooltips(barChartPanel, "sla");

        updateStats();
    }

    private void applyFilter(ActionEvent e) {
        updateStats();
    }

    public void updateStats() {
        List<Ticket> tickets = TicketManager.getInstance().getAllTickets();
        String selectedPriority = (String) priorityFilter.getSelectedItem();

        Predicate<Ticket> filter = t -> selectedPriority.equals("All") || t.getPriority().equalsIgnoreCase(selectedPriority);
        List<Ticket> filteredTickets = tickets.stream().filter(filter).collect(Collectors.toList());

        int total = filteredTickets.size();
        double avgSla = filteredTickets.stream().mapToInt(Ticket::getSlaHours).average().orElse(0);

        Map<String, Long> priorityCount = filteredTickets.stream()
                .collect(Collectors.groupingBy(Ticket::getPriority, Collectors.counting()));

        Map<String, Long> statusCount = filteredTickets.stream()
                .collect(Collectors.groupingBy(Ticket::getStatus, Collectors.counting()));

        Map<String, Long> slaRangeCount = filteredTickets.stream().collect(Collectors.groupingBy(t -> {
            int sla = t.getSlaHours();
            if (sla <= 10) return "0-10";
            else if (sla <= 20) return "11-20";
            else if (sla <= 30) return "21-30";
            else return "30+";
        }, Collectors.counting()));

        totalLabel.setText("Total Tickets: " + total);
        avgSlaLabel.setText("Average SLA: " + String.format("%.2f", avgSla));

        pieChartPanel.setData(priorityCount);
        pieChartPanel.repaint();

        barChartPanel.setData(slaRangeCount);
        barChartPanel.repaint();

        statusChartPanel.setData(statusCount);
        statusChartPanel.repaint();
    }

    private void addInteractiveTooltips(JComponent chart, String type) {
        chart.setToolTipText(null);
        chart.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                if (chart instanceof PieChartPanel && type.equals("priority")) {
                    chart.setToolTipText("Displays ticket priority distribution");
                } else if (chart instanceof StatusChartPanel && type.equals("status")) {
                    chart.setToolTipText("Displays ticket status breakdown");
                } else if (chart instanceof BarChartPanel && type.equals("sla")) {
                    chart.setToolTipText("Displays SLA hours grouped by range");
                }
            }
        });
    }

    class PieChartPanel extends JPanel {
        private Map<String, Long> data;
        public void setData(Map<String, Long> data) { this.data = data; }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g;
            int diameter = Math.min(getWidth(), getHeight()) - 100;
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2 - 20;
            long total = data.values().stream().mapToLong(Long::longValue).sum();
            int startAngle = 0;
            for (Map.Entry<String, Long> entry : data.entrySet()) {
                g2.setColor(getColor(entry.getKey()));
                int arcAngle = (int) Math.round((entry.getValue() * 360.0) / total);
                g2.fill(new Arc2D.Double(x, y, diameter, diameter, startAngle, arcAngle, Arc2D.PIE));
                startAngle += arcAngle;
            }

            int legendY = y + diameter + 10;
            int i = 0;
            for (Map.Entry<String, Long> entry : data.entrySet()) {
                g2.setColor(getColor(entry.getKey()));
                g2.fillRect(x + 100 * i, legendY, 10, 10);
                g2.setColor(Color.BLACK);
                g2.drawString(entry.getKey(), x + 100 * i + 15, legendY + 10);
                i++;
            }
        }
    }

    class BarChartPanel extends JPanel {
        private Map<String, Long> data;
        public void setData(Map<String, Long> data) { this.data = data; }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g;
            int width = getWidth(), height = getHeight(), barWidth = width / data.size();
            long max = data.values().stream().mapToLong(Long::longValue).max().orElse(1);
            int x = 10;
            for (Map.Entry<String, Long> entry : data.entrySet()) {
                int barHeight = (int) ((entry.getValue() * 1.0 / max) * (height - 60));
                g2.setColor(new Color(100, 149, 237));
                g2.fillRect(x, height - barHeight - 20, barWidth - 20, barHeight);
                g2.setColor(Color.BLACK);
                g2.drawString(entry.getKey(), x + 5, height - 5);
                g2.drawString(entry.getValue().toString(), x + 5, height - barHeight - 25);
                x += barWidth;
            }
        }
    }

    class StatusChartPanel extends JPanel {
        private Map<String, Long> data;
        public void setData(Map<String, Long> data) { this.data = data; }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g;
            int diameter = Math.min(getWidth(), getHeight()) - 100;
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2 - 20;
            long total = data.values().stream().mapToLong(Long::longValue).sum();
            int startAngle = 0;
            for (Map.Entry<String, Long> entry : data.entrySet()) {
                g2.setColor(getColor(entry.getKey()));
                int arcAngle = (int) Math.round((entry.getValue() * 360.0) / total);
                g2.fill(new Arc2D.Double(x, y, diameter, diameter, startAngle, arcAngle, Arc2D.PIE));
                startAngle += arcAngle;
            }

            int legendY = y + diameter + 10;
            int i = 0;
            for (Map.Entry<String, Long> entry : data.entrySet()) {
                g2.setColor(getColor(entry.getKey()));
                g2.fillRect(x + 100 * i, legendY, 10, 10);
                g2.setColor(Color.BLACK);
                g2.drawString(entry.getKey(), x + 100 * i + 15, legendY + 10);
                i++;
            }
        }
    }

    private Color getColor(String label) {
        switch (label.toLowerCase()) {
            case "critical": case "closed": return Color.RED;
            case "medium": return Color.YELLOW;
            case "low": return Color.GREEN;
            case "open": return Color.BLUE;
            case "pending": return Color.ORANGE;
            default: return Color.GRAY;
        }
    }
}
