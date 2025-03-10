package org.example;

import com.google.gson.Gson;
import java.io.*;
import java.util.Scanner;

public class Configuration {
    private int maxTicketCapacity;
    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;

    // Constructor
    public Configuration(int maxTicketCapacity, int totalTickets, int ticketReleaseRate, int customerRetrievalRate) {
        this.maxTicketCapacity = maxTicketCapacity;
        this.totalTickets = totalTickets;
        this.ticketReleaseRate = ticketReleaseRate;
        this.customerRetrievalRate = customerRetrievalRate;
    }

    // Getters and setters
    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    public void setMaxTicketCapacity(int maxTicketCapacity) {
        this.maxTicketCapacity = maxTicketCapacity;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public int getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public void setTicketReleaseRate(int ticketReleaseRate) {
        this.ticketReleaseRate = ticketReleaseRate;
    }

    public int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public void setCustomerRetrievalRate(int customerRetrievalRate) {
        this.customerRetrievalRate = customerRetrievalRate;
    }

    // Save configuration to a JSON file
    public static void saveConfig(Configuration config, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            Gson gson = new Gson();
            gson.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load configuration from a JSON file
    public static Configuration loadConfig(String fileName) {
        try (FileReader reader = new FileReader(fileName)) {
            Gson gson = new Gson();
            return gson.fromJson(reader, Configuration.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null if file not found or error occurs
        }
    }

    // Method to prompt user for configuration values
    public static Configuration promptForConfig() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter maximum ticket capacity: ");
        int maxTicketCapacity = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter total number of tickets (must not exceed max capacity): ");
        int totalTickets = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter ticket release rate (in milliseconds): ");
        int ticketReleaseRate = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter customer retrieval rate (in milliseconds): ");
        int customerRetrievalRate = Integer.parseInt(scanner.nextLine());

        return new Configuration(maxTicketCapacity, totalTickets, ticketReleaseRate, customerRetrievalRate);
    }
}
