package org.example;

import java.util.LinkedList;
import java.util.List;

public class TicketPool {
    private final List<Ticket> tickets; // List to store the tickets in the pool
    private final int maxTicketCapacity; // Maximum number of tickets that can be held in the pool
    private final int totalTickets; // Initial number of tickets added to the pool
    private int totalTicketsSold = 0; // Tracks the total number of tickets sold
    private int totalTicketsReleased = 0; // Tracks the total number of tickets released

    // Constructor to initialize the TicketPool with given capacity and total tickets
    public TicketPool(int maxTicketCapacity, int totalTickets) {
        // Validate that total tickets do not exceed the maximum ticket capacity
        if (totalTickets > maxTicketCapacity) {
            throw new IllegalArgumentException("Total tickets cannot exceed max ticket capacity.");
        }
        // Validate that capacities are greater than zero
        if (maxTicketCapacity <= 0 || totalTickets <= 0) {
            throw new IllegalArgumentException("Ticket capacities must be greater than 0.");
        }

        this.maxTicketCapacity = maxTicketCapacity; // Set the maximum ticket capacity
        this.totalTickets = totalTickets; // Set the initial number of tickets
        this.tickets = new LinkedList<>(); // Initialize the ticket pool

        // Add the initial tickets to the pool (up to totalTickets)
        for (int i = 0; i < totalTickets; i++) {
            tickets.add(new Ticket());
        }
        totalTicketsReleased = totalTickets; // Initialize the released ticket count
    }

    // Adds tickets to the pool (synchronized to prevent race conditions)
    public synchronized void addTickets(int numTickets) {
        int availableSpace = maxTicketCapacity - tickets.size(); // Calculate available space in the pool
        if (availableSpace >= numTickets) {
            // Add the requested number of tickets
            for (int i = 0; i < numTickets; i++) {
                tickets.add(new Ticket());
            }
            totalTicketsReleased += numTickets; // Update the released ticket count
        } else {
            // Add only the available space if the requested tickets exceed the capacity
            for (int i = 0; i < availableSpace; i++) {
                tickets.add(new Ticket());
            }
            totalTicketsReleased += availableSpace; // Update with the actual number of tickets added
        }
        // Log the action
        System.out.println("Vendor released " + numTickets + " tickets. Available tickets: " + tickets.size());
    }

    // Removes a ticket from the pool (synchronized to prevent race conditions)
    public synchronized Ticket removeTicket() {
        if (!tickets.isEmpty()) { // Check if tickets are available
            totalTicketsSold++; // Increment the total tickets sold count
            return tickets.remove(0); // Remove and return the first ticket from the pool
        }
        return null; // Return null if no tickets are available
    }

    // Returns the number of tickets currently available in the pool
    public synchronized int getAvailableTickets() {
        return tickets.size();
    }

    // Returns the total number of tickets sold so far
    public synchronized int getTotalTicketsSold() {
        return totalTicketsSold;
    }

    // Returns the total number of tickets released so far
    public synchronized int getTotalTicketsReleased() {
        return totalTicketsReleased;
    }

    // Checks if tickets can still be sold (based on max capacity)
    public synchronized boolean canSellTicket() {
        return totalTicketsSold < maxTicketCapacity; // Returns true if tickets are still sellable
    }
}
