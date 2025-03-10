package org.example;

public class Customer implements Runnable {
    private final TicketPool ticketPool; // Shared resource for ticket management
    private final int customerRetrievalRate; // Delay between each ticket purchase (in milliseconds)
    private static int totalTicketsSold = 0; // Tracks the total number of tickets sold across all customers
    private final int maxTicketCapacity; // Maximum ticket capacity of the ticket pool
    private static int customerCounter = 1; // Counter to assign unique IDs to customers

    public Customer(TicketPool ticketPool, int customerRetrievalRate, int maxTicketCapacity) {
        this.ticketPool = ticketPool; // Assign the shared ticket pool
        this.customerRetrievalRate = customerRetrievalRate; // Set the delay between ticket purchases
        this.maxTicketCapacity = maxTicketCapacity; // Set the maximum ticket capacity
    }

    @Override
    public void run() {
        int ticketsBought = 0; // Tracks the number of tickets bought by this customer
        int customerId = customerCounter++; // Assign a unique ID to this customer

        // Continue purchasing tickets until the ticket pool is empty
        while (ticketPool.canSellTicket()) {
            if (ticketPool.getAvailableTickets() > 0) { // Check if tickets are available
                Ticket ticket = ticketPool.removeTicket(); // Attempt to purchase a ticket
                if (ticket != null) { // If the ticket was successfully purchased
                    ticketsBought++; // Increment tickets bought by this customer
                    totalTicketsSold++; // Increment global ticket sales count
                    System.out.println("Customer-" + customerId + " bought 1 ticket. Available tickets: " + ticketPool.getAvailableTickets());
                }
            }

            try {
                Thread.sleep(customerRetrievalRate); // Simulate delay between ticket purchases
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status and exit gracefully
                break;
            }
        }
        // Log when the customer finishes purchasing tickets
        System.out.println("Customer-" + customerId + " has finished purchasing tickets.");
    }

    // Getter to retrieve the total number of tickets sold across all customers
    public static int getTotalTicketsSold() {
        return totalTicketsSold;
    }
}
