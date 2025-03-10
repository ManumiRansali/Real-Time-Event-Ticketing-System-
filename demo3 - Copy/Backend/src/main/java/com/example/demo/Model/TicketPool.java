package com.example.demo.Model;

import java.util.LinkedList;
import java.util.List;

import java.util.LinkedList;
import java.util.List;

public class TicketPool {
    private final List<Integer> tickets = new LinkedList<>();
    private final int maxCapacity;

    public TicketPool(int maxCapacity, int totalTickets) {
        this.maxCapacity = maxCapacity;
        for (int i = 0; i < totalTickets; i++) {
            tickets.add(1); // Initialize pool with tickets
        }
    }

    public synchronized void addTickets(int count) {
        while (tickets.size() + count > maxCapacity) {
            try {
                wait(); // Wait until space is available
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        for (int i = 0; i < count; i++) {
            tickets.add(1);
        }
        notifyAll(); // Notify all waiting threads
    }

    public synchronized void removeTickets(int count) {
        while (tickets.size() < count) {
            try {
                wait(); // Wait until tickets are available
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        for (int i = 0; i < count; i++) {
            tickets.remove(0);
        }
        notifyAll(); // Notify all waiting threads
    }

    public synchronized int getTicketsAvailable() {
        return tickets.size();
    }

    public synchronized boolean isFull() {
        return tickets.size() >= maxCapacity;
    }

    public synchronized boolean isEmpty() {
        return tickets.isEmpty();
    }
}
