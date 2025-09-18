# Transportation Fleet Management System - AP_M25_A1

Features:
To design and implement a Java-based simulation of a multi-modal logistics management system that handles:
- Land, air and water vehicles
- Cargo and passenger transport
- Trip scheduling, fuel consumption, and maintenance tracking
- Reporting and fleet management

How to compile and run 
1. Compile
javac -d out $(find app logistics vehicles -name "*.java")
2. Run
java -cp out app.Main

Demo:
- On launch, creates sample Car, Truck, Bus, Airplane, CargoShip
- Simulates 100 km journey, prints report, saves sample_fleet.csv

CLI Usage:
Menu options:
1. Add Vehicle
2. Remove Vehicle
3. Start Journey
4. Refuel All
5. Perform Maintenance
6. Generate Report
7. Save Fleet
8. Load Fleet
9. Search by Type
10. List Vehicles Needing Maintenance
11. Exit

- Validate numeric inputs, prevents duplicate IDs

Persistence:
- Uses CSV for save/load (sample_fleet.csv provided)

Name: Evani Menon
Roll Number: 2024210
Course: AP (Advanced Programming) 2025
