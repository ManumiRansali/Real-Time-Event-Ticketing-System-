package org.example;

import java.util.Scanner;

public class Main {
    private static final String CONFIG_FILE = "config.json"; // Use .json for Gson serialization
    private static Configuration config;
    private static TicketPool ticketPool;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Load Configuration");
            System.out.println("2. Enter New Parameters");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            String userInput = scanner.nextLine().trim();

            switch (userInput) {
                case "1": // Load Configuration
                    config = Configuration.loadConfig(CONFIG_FILE);
                    if (config == null) {
                        System.out.println("No valid configuration file found. Please enter new parameters.");
                        config = promptForConfig(scanner); // Prompt for new parameters
                        Configuration.saveConfig(config, CONFIG_FILE); // Save the new configuration
                    } else {
                        System.out.println("Configuration loaded successfully:");
                        printConfig(config);
                    }
                    // Automatically start the system after loading the configuration
                    initializeAndStartSystem();
                    break;

                case "2": // Enter New Parameters
                    config = promptForConfig(scanner); // Prompt for new parameters
                    Configuration.saveConfig(config, CONFIG_FILE); // Save the new configuration
                    System.out.println("Configuration saved to " + CONFIG_FILE);
                    // Automatically start the system after entering new parameters
                    initializeAndStartSystem();
                    break;

                case "3": // Exit
                    System.out.println("Exiting...");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Utility method to print the configuration
    private static void printConfig(Configuration config) {
        System.out.println("Max Ticket Capacity: " + config.getMaxTicketCapacity());
        System.out.println("Total Tickets: " + config.getTotalTickets());
        System.out.println("Ticket Release Rate: " + config.getTicketReleaseRate() + " ms");
        System.out.println("Customer Retrieval Rate: " + config.getCustomerRetrievalRate() + " ms");
    }

    // Prompt the user for configuration parameters
    private static Configuration promptForConfig(Scanner scanner) {
        int maxTicketCapacity = promptForPositiveInt(scanner, "Enter maximum ticket capacity: ");
        int totalTickets;
        do {
            totalTickets = promptForPositiveInt(scanner, "Enter total number of tickets (must not exceed max capacity): ");
            if (totalTickets > maxTicketCapacity) {
                System.out.println("Error: Total tickets cannot exceed max ticket capacity. Please try again.");
            }
        } while (totalTickets > maxTicketCapacity);

        int ticketReleaseRate = promptForPositiveInt(scanner, "Enter ticket release rate (in milliseconds): ");
        int customerRetrievalRate = promptForPositiveInt(scanner, "Enter customer retrieval rate (in milliseconds): ");

        return new Configuration(maxTicketCapacity, totalTickets, ticketReleaseRate, customerRetrievalRate);
    }

    // Utility method to prompt the user for a positive integer value
    private static int promptForPositiveInt(Scanner scanner, String prompt) {
        int value = -1;
        while (value <= 0) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                if (value <= 0) {
                    System.out.println("Error: Please enter a positive integer.");
                }
            } else {
                System.out.println("Error: Invalid input. Please enter a positive integer.");
                scanner.next(); // Clear invalid input
            }
        }
        scanner.nextLine(); // Consume the newline character
        return value;
    }

    // Initialize and start the system with the provided configuration
    private static void initializeAndStartSystem() {
        System.out.println("Starting system with the following configuration:");
        printConfig(config); // Print the configuration before starting the system

        // Initialize TicketPool
        ticketPool = new TicketPool(config.getMaxTicketCapacity(), config.getTotalTickets());

        // Start vendor and customer threads
        Thread vendorThread = new Thread(new Vendor(ticketPool, config.getTicketReleaseRate(), 5, config.getMaxTicketCapacity()));
        Thread customerThread1 = new Thread(new Customer(ticketPool, config.getCustomerRetrievalRate(), config.getMaxTicketCapacity()));
        Thread customerThread2 = new Thread(new Customer(ticketPool, config.getCustomerRetrievalRate(), config.getMaxTicketCapacity()));
        // Starting threads concurrently
        vendorThread.start();
        customerThread1.start();
        customerThread2.start();

        try {
            vendorThread.join();
            customerThread1.join();
            customerThread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Display the total tickets sold and end the system
        System.out.println("Total tickets sold: " + Customer.getTotalTicketsSold());
        System.out.println("Ticket booking system has completed all operations.");
    }
}
