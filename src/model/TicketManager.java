package model;

import structures.TicketQueue;
import java.util.ArrayList;
import java.util.List;

public class TicketManager {
    private static TicketManager instance;
    private final List<Ticket> tickets;
    private final TicketQueue queue;

    private TicketManager() {
        tickets = new ArrayList<>();
        queue = new TicketQueue();
    }

    public static TicketManager getInstance() {
        if (instance == null) {
            instance = new TicketManager();
        }
        return instance;
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
        queue.enqueue(ticket);
    }

    public List<Ticket> getAllTickets() {
        return new ArrayList<>(tickets); // return copy
    }

    public TicketQueue getQueue() {
        return queue;
    }

    public void clearTickets() {
        tickets.clear();
        queue.clear();
    }
}