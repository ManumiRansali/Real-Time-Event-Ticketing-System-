package com.example.demo.Model;

import java.util.List;

import java.util.List;

public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final int rate; // Ticket purchase rate (in milliseconds)
    private final List<String> logs;

    public Customer(TicketPool ticketPool, int rate, List<String> logs) {
        this.ticketPool = ticketPool;
        this.rate = rate;
        this.logs = logs;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (ticketPool) {
                    if (ticketPool.isEmpty()) {
                        logs.add(Thread.currentThread().getName() + " is waiting for tickets to become available.");
                        ticketPool.wait(); // Wait until tickets are available
                    } else {
                        int ticketsToBuy = 1; // Example number of tickets to buy per cycle
                        ticketPool.removeTickets(ticketsToBuy);
                        logs.add(Thread.currentThread().getName() + " bought " + ticketsToBuy + " ticket(s). Available tickets: " + ticketPool.getTicketsAvailable());
                        ticketPool.notifyAll(); // Notify waiting threads
                    }
                }
                Thread.sleep(rate); // Wait for the specified rate before buying tickets again
            }
        } catch (InterruptedException e) {
            logs.add(Thread.currentThread().getName() + " has finished purchasing tickets.");
            Thread.currentThread().interrupt(); // Handle thread interruption gracefully
        }
    }
}
