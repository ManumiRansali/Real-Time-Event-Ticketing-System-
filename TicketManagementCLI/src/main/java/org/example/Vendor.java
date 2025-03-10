package org.example;

public class Vendor implements Runnable {
    private final TicketPool ticketPool; // Shared ticket pool for managing tickets
    private final int ticketReleaseRate; // Rate at which tickets are released (in milliseconds)
    private final int releaseCapacity; // Number of tickets released per batch
    private final int maxTicketCapacity; // Maximum number of tickets allowed in the pool

    // Constructor to initialize Vendor instance with necessary parameters
    public Vendor(TicketPool ticketPool, int ticketReleaseRate, int releaseCapacity, int maxTicketCapacity) {
        // Validate that the release capacity does not exceed the maximum ticket capacity
        if (releaseCapacity > maxTicketCapacity) {
            throw new IllegalArgumentException("Release capacity cannot exceed max ticket capacity.");
        }
        this.ticketPool = ticketPool; // Assign the shared ticket pool
        this.ticketReleaseRate = ticketReleaseRate; // Set the ticket release rate
        this.releaseCapacity = releaseCapacity; // Set the release capacity per batch
        this.maxTicketCapacity = maxTicketCapacity; // Set the maximum ticket capacity
    }

    @Override
    public void run() {
        // Release tickets in batches until the maximum ticket capacity is reached
        while (ticketPool.getTotalTicketsReleased() < maxTicketCapacity) {
            if (!Thread.currentThread().isInterrupted()) { // Check if the thread is interrupted
                ticketPool.addTickets(releaseCapacity); // Release tickets in batches
            } else {
                break; // Exit the loop if the thread is interrupted
            }
            try {
                Thread.sleep(ticketReleaseRate); // Wait before releasing the next batch of tickets
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupt status
                break; // Exit the loop if interrupted
            }
        }
        // Log a message when the vendor reaches the maximum ticket capacity
        System.out.println("Vendor has reached maximum ticket capacity and stops releasing tickets.");
    }
}
