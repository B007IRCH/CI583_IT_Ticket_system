package structures;

import model.Ticket;


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


    public Ticket dequeue() {
        if (front == null) return null;
        Ticket temp = front.data;
        front = front.next;
        if (front == null) rear = null;
        size--;
        return temp;
    }


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
