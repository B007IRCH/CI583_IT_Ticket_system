package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Ticket {
    private static int count = 1;

    private String id;
    private String clientName;
    private String issue;
    private String priority;
    private String status;
    private int slaHours;
    private String department;
    private LocalDateTime submissionTime;
    private List<String> comments;

    public Ticket(String clientName, String issue, String priority, int slaHours) {
        this.id = String.format("#%03d", count++);
        this.clientName = clientName;
        this.issue = issue;
        this.priority = priority;
        this.status = "Open";
        this.slaHours = slaHours;
        this.comments = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getClientName() {
        return clientName;
    }

    public String getIssue() {
        return issue;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public int getSlaHours() {
        return slaHours;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDateTime getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(LocalDateTime submissionTime) {
        this.submissionTime = submissionTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

    public List<String> getComments() {
        return comments;
    }

    @Override
    public String toString() {
        return id + " - " + clientName + " - " + priority + " - " + status;
    }
}
