package structures;

import model.Ticket;

/**
 * AVL Tree implementation for storing and searching Tickets by SLA.
 */
public class AVLTree {
    class Node {
        Ticket ticket;
        int height;
        Node left, right;

        Node(Ticket ticket) {
            this.ticket = ticket;
            this.height = 1;
        }
    }

    private Node root;

    public void insert(Ticket ticket) {
        root = insertRec(root, ticket);
    }

    private Node insertRec(Node node, Ticket ticket) {
        if (node == null) return new Node(ticket);

        if (ticket.getSlaHours() < node.ticket.getSlaHours())
            node.left = insertRec(node.left, ticket);
        else
            node.right = insertRec(node.right, ticket);

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        // LL
        if (balance > 1 && ticket.getSlaHours() < node.left.ticket.getSlaHours())
            return rotateRight(node);

        // RR
        if (balance < -1 && ticket.getSlaHours() >= node.right.ticket.getSlaHours())
            return rotateLeft(node);

        // LR
        if (balance > 1 && ticket.getSlaHours() >= node.left.ticket.getSlaHours()) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // RL
        if (balance < -1 && ticket.getSlaHours() < node.right.ticket.getSlaHours()) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    public Ticket search(int sla) {
        return searchRec(root, sla);
    }

    private Ticket searchRec(Node node, int sla) {
        if (node == null) return null;
        if (sla == node.ticket.getSlaHours()) return node.ticket;
        if (sla < node.ticket.getSlaHours()) return searchRec(node.left, sla);
        return searchRec(node.right, sla);
    }

    private int height(Node node) {
        return (node == null) ? 0 : node.height;
    }

    private int getBalance(Node node) {
        return (node == null) ? 0 : height(node.left) - height(node.right);
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }
}
