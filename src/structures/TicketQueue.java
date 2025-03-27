package structures;

import model.Ticket;

/**
 * A custom FIFO queue to manage ticket intake order.
 * Demonstrates abstract data structure implementation.
 */
public class TicketQueue {
    private static class Node {
        Ticket data;
        Node next;

        Node(Ticket data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node front, rear;
    private int size;

    public TicketQueue() {
        this.front = this.rear = null;
        this.size = 0;
    }

    /**
     * Adds a ticket to the back of the queue. O(1)
     */
    public void enqueue(Ticket ticket) {
        Node node = new Node(ticket);
        if (rear == null) {
            front = rear = node;
        } else {
            rear.next = node;
            rear = node;
        }
        size++;
    }

    /**
     * Removes and returns the ticket at the front. O(1)
     */
    public Ticket dequeue() {
        if (front == null) return null;
        Ticket temp = front.data;
        front = front.next;
        if (front == null) rear = null;
        size--;
        return temp;
    }

    /**
     * Returns the ticket at the front without removing it. O(1)
     */
    public Ticket peek() {
        return (front != null) ? front.data : null;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void clear() {
        front = rear = null;
        size = 0;
    }
}
