package com.example.demo.Model;

import java.util.List;

public class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final int rate; // Ticket release rate (in milliseconds)
    private final List<String> logs;

    public Vendor(TicketPool ticketPool, int rate, List<String> logs) {
        this.ticketPool = ticketPool;
        this.rate = rate;
        this.logs = logs;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (ticketPool) {
                    if (ticketPool.isFull()) {
                        logs.add(Thread.currentThread().getName() + " has reached maximum ticket capacity and stops releasing tickets.");
                        ticketPool.wait(); // Wait until space becomes available
                    } else {
                        int ticketsToAdd = 5; // Example number of tickets to add per cycle
                        ticketPool.addTickets(ticketsToAdd);
                        logs.add(Thread.currentThread().getName() + " released " + ticketsToAdd + " tickets. Available tickets: " + ticketPool.getTicketsAvailable());
                        ticketPool.notifyAll(); // Notify waiting threads
                    }
                }
                Thread.sleep(rate); // Wait for the specified rate before releasing tickets again
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle thread interruption gracefully
        }
    }
}
