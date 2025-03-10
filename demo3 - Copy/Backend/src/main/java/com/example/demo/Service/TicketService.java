package com.example.demo.Service;

import com.example.demo.Model.Customer;
import com.example.demo.Model.TicketPool;
import com.example.demo.Model.Vendor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {
    private TicketPool ticketPool; // TicketPool instance
    private final List<Thread> vendorThreads = new ArrayList<>();
    private final List<Thread> customerThreads = new ArrayList<>();
    private final List<String> logs = new ArrayList<>(); // To store logs of actions

    public String startSimulation(int totalTickets, int maxCapacity, int vendorRate, int customerRate, int vendors, int customers) {
        // Initialize the ticketPool
        ticketPool = new TicketPool(maxCapacity, totalTickets);

        // Start vendor threads
        for (int i = 0; i < vendors; i++) {
            Vendor vendor = new Vendor(ticketPool, vendorRate, logs);
            Thread vendorThread = new Thread(vendor, "Vendor-" + (i + 1));
            vendorThreads.add(vendorThread);
            vendorThread.start();
        }

        // Start customer threads
        for (int i = 0; i < customers; i++) {
            Customer customer = new Customer(ticketPool, customerRate, logs);
            Thread customerThread = new Thread(customer, "Customer-" + (i + 1));
            customerThreads.add(customerThread);
            customerThread.start();
        }

        logs.add("Simulation started with " + vendors + " vendors and " + customers + " customers.");
        return "Simulation started!";
    }

    public String stopSimulation() {
        // Interrupt all threads
        vendorThreads.forEach(Thread::interrupt);
        customerThreads.forEach(Thread::interrupt);
        logs.add("Simulation stopped.");
        return "Simulation stopped!";
    }

    public List<String> getLogs() {
        // Return the list of logs
        return logs;
    }

    public int getTicketsAvailable() {
        // Check if ticketPool is initialized before accessing it
        if (ticketPool == null) {
            throw new IllegalStateException("Simulation has not started. Ticket pool is not initialized.");
        }
        return ticketPool.getTicketsAvailable(); // Delegate to TicketPool
    }
}

