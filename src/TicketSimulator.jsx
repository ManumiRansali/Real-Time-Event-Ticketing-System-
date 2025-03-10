import React, { useState, useEffect } from 'react';
import axios from 'axios';

function TicketSimulator() {
  const [form, setForm] = useState({
    maxTicketCapacity: '',
    totalTickets: '',
    ticketReleaseRate: '',
    customerRetrievalRate: '',
    vendors: '',
    customers: '',
  });
  const [logs, setLogs] = useState([]);
  const [ticketsAvailable, setTicketsAvailable] = useState(null);
  const [isSimulationRunning, setIsSimulationRunning] = useState(false);
  const [notification, setNotification] = useState(null); // To store validation/error messages

  // Handle form input changes
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  // Helper: Show Notification
  {notification && <div className="notification">{notification}</div>}

  const showNotification = (message, duration = 3000) => {
    setNotification(message);
    setTimeout(() => setNotification(null), duration);
  };

  // Validate Inputs
  const validateInputs = () => {
    const {
      maxTicketCapacity,
      totalTickets,
      ticketReleaseRate,
      customerRetrievalRate,
      vendors,
      customers,
    } = form;

    if (
      !maxTicketCapacity ||
      !totalTickets ||
      !ticketReleaseRate ||
      !customerRetrievalRate ||
      !vendors ||
      !customers
    ) {
      showNotification('All fields are required!');
      return false;
    }

    if (parseInt(totalTickets) > parseInt(maxTicketCapacity)) {
      showNotification('Total Tickets cannot exceed Max Ticket Capacity!');
      return false;
    }

    if (parseInt(totalTickets) <= 0 || parseInt(maxTicketCapacity) <= 0) {
      showNotification('Total Tickets and Max Ticket Capacity must be positive numbers!');
      return false;
    }

    return true;
  };

  // Start Simulation
  const startSimulation = async () => {
    if (!validateInputs()) return; // Stop if validation fails

    try {
      const simulationData = {
        maxTicketCapacity: parseInt(form.maxTicketCapacity, 10),
        totalTickets: parseInt(form.totalTickets, 10),
        ticketReleaseRate: parseInt(form.ticketReleaseRate, 10),
        customerRetrievalRate: parseInt(form.customerRetrievalRate, 10),
        vendors: parseInt(form.vendors, 10),
        customers: parseInt(form.customers, 10),
      };

      await axios.post('http://localhost:8080/api/tickets/testSimulation', simulationData);
      setIsSimulationRunning(true);
      showNotification('Simulation started successfully!');
    } catch (error) {
      console.error('Error starting simulation:', error);
      showNotification('Failed to start simulation. Check backend connection.');
    }
  };

  // Stop Simulation
  const stopSimulation = async () => {
    try {
      await axios.post('http://localhost:8080/api/tickets/stop');
      setIsSimulationRunning(false);
      showNotification('Simulation stopped successfully!');
    } catch (error) {
      console.error('Error stopping simulation:', error);
      showNotification('Failed to stop simulation. Check backend connection.');
    }
  };

  // Fetch Tickets Available
  const fetchTickets = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/tickets/available');
      setTicketsAvailable(response.data.ticketsAvailable);
    } catch (error) {
      console.error('Error fetching tickets:', error);
    }
  };

  // Fetch Logs
  const fetchLogs = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/tickets/logs');
      setLogs(response.data);
    } catch (error) {
      console.error('Error fetching logs:', error);
    }
  };

  // Periodically fetch tickets and logs when the simulation is running
  useEffect(() => {
    let interval;
    if (isSimulationRunning) {
      interval = setInterval(() => {
        fetchTickets();
        fetchLogs();
      }, 1000); // Fetch every second
    }
    return () => clearInterval(interval); // Cleanup on unmount or when simulation stops
  }, [isSimulationRunning]);

  return (
    <div className="dashboard-container">
      <h1>🎟️ Ticket Management System </h1>

      {/* Notification Section */}
      {notification && <div className="notification">{notification}</div>}

      {/* Form to Input Parameters */}
      <div className="form-container">
        <form>
          {Object.keys(form).map((key) => (
            <div key={key} className="form-group">
              <label>{key.replace(/([A-Z])/g, ' $1')}</label>
              <input
                type="number"
                name={key}
                value={form[key]}
                onChange={handleChange}
                placeholder={`Enter ${key.replace(/([A-Z])/g, ' $1')}`}
              />
            </div>
          ))}
        </form>
        {/* Button Container below the form */}
        <div className="button-container">
          <button onClick={startSimulation} disabled={isSimulationRunning} className="start-btn">
            Start Simulation
          </button>
          <button onClick={stopSimulation} disabled={!isSimulationRunning} className="stop-btn">
            Stop Simulation
          </button>
        </div>
      </div>

      {/* Status and Logs */}
      <div className="status-container">
        <div className="card">
          <h3>Simulation Status</h3>
          <p>Tickets Available: {ticketsAvailable !== null ? ticketsAvailable : 'N/A'}</p>
          <p>Status: {isSimulationRunning ? 'Running' : 'Stopped'}</p>
        </div>
        <div className="card">
          <h3>Logs</h3>
          <div className="logs-container">
            {logs.length > 0 ? (
              logs.map((log, index) => (
                <div key={index} className="log-item">
                  {log}
                </div>
              ))
            ) : (
              <p>No logs available yet.</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default TicketSimulator;
