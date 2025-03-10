package com.example.demo.Controller;

import com.example.demo.Model.Configuration;
import com.example.demo.Service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/start")
    public ResponseEntity<String> startSystem(@RequestParam int totalTickets,
                                              @RequestParam int maxCapacity,
                                              @RequestParam int vendorReleaseRate,
                                              @RequestParam int customerRetrievalRate,
                                              @RequestParam int vendors,
                                              @RequestParam int customers) {
        try {
            String response = ticketService.startSimulation(totalTickets, maxCapacity, vendorReleaseRate, customerRetrievalRate, vendors, customers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error starting simulation: " + e.getMessage());
        }
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopSystem() {
        try {
            String response = ticketService.stopSimulation();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error stopping simulation: " + e.getMessage());
        }
    }

    @GetMapping("/available")
    public ResponseEntity<Integer> getTicketsAvailable() {
        try {
            int tickets = ticketService.getTicketsAvailable();
            return ResponseEntity.ok(tickets);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<List<String>> getLogs() {
        try {
            List<String> logs = ticketService.getLogs();
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/test")
    public String test(){
        return "Test 1";
    }

    @PostMapping(value = "/testSimulation")
    public String testSimulation(@RequestBody Configuration configuration) {
        return ticketService.startSimulation(configuration.getTotalTickets(), configuration.getMaxTicketCapacity(), configuration.getTicketReleaseRate(), configuration.getCustomerRetrievalRate(), configuration.getVendors(), configuration.getCustomers());

    }

}
