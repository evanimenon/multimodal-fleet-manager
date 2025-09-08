# Transportation Fleet Management System - AP_M25_A1

Objective:
To design and implement a Java-based simulation of a multi-modal logistics management system that handles:
- Land, air and water vehicles
- Cargo and passenger transport
- Trip scheduling, fuel consumption, and maintenance tracking
- Reporting and fleet management


How to compile and run 
1. Compile
javac vehicles/*.java logistics/*.java Main.java
2. Run
java main

Sample Test Case in Main.java:
LandVehicle truck = new LandVehicle("LV01", "Tata Truck", 80, 6);
FleetManager manager = new FleetManager();
manager.addVehicle(truck);

Trip trip = new Trip(truck, 150, "Chennai");
Scheduler scheduler = new Scheduler();
scheduler.scheduleTrip(trip);

Name: Evani Menon
Roll Number: 2024210
Course: AP (Advanced Programming) 2025
