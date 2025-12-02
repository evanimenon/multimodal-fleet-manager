# Transportation Fleet Management System
AP_M25 – Advanced Programming (Assignment A1 + A2 + A3)

Author: Evani Menon  
Roll Number: 2024210

------------------------------------------------------------
Project Overview
------------------------------------------------------------
This project implements a multi-modal transportation fleet management
system using Object-Oriented Programming principles in Java. It supports
managing land, air, and water vehicles, tracking mileage, fuel consumption,
passenger and cargo loads, maintenance requirements, persistence to file,
sorting and reporting through a CLI menu and Multi-threaded Highway Simulator (one thread per vehicle)

------------------------------------------------------------
How to Compile and Run
------------------------------------------------------------
1. Compile:
   javac -d out $(find . -name "*.java")

2. Run A2:
   java -cp out app.Main

   Run A3:
   java -cp "out:lib/*" ui.HighwaySimulatorLauncher

------------------------------------------------------------
Vehicle Types
------------------------------------------------------------
Land Vehicles:
- Car
- Truck
- Bus

Air Vehicles:
- Airplane

Water Vehicles:
- CargoShip

------------------------------------------------------------
Use of Collections
------------------------------------------------------------
The fleet is stored using:
- ArrayList<Vehicle> fleet:
  Used for dynamic resizing, fast iteration, and sorting of vehicle data.

To store distinct model names (no duplicates):
- HashSet<String> modelNames:
  Ensures uniqueness automatically (O(1) lookup and insert).

To provide sorted alphabetical listing of distinct models:
- TreeSet<String> (created from HashSet):
  Produces automatically sorted order without manual sorting.

This satisfies:
- Dynamic storage
- Uniqueness enforcement
- Automatic sorted output

------------------------------------------------------------
File I/O Implementation
------------------------------------------------------------
Fleet data is saved to and loaded from CSV files using standard Java I/O
(with try-with-resources).

Format per line:
Type,ID,Model,MaxSpeed,Mileage

Example:
Car,C1,Sedan,120.0,300.0

Methods:
+ saveToFile(String filename)
+ loadFromFile(String filename)

These allow full persistence of the fleet between program runs.

------------------------------------------------------------
Interfaces Used
------------------------------------------------------------
FuelConsumable:
  refuel(), consumeFuel()

PassengerCarrier:
  boardPassengers(), disembarkPassengers()

CargoCarrier:
  loadCargo(), unloadCargo()

Maintainable:
  needsMaintenance(), scheduleMaintenance(), performMaintenance()

------------------------------------------------------------
Sample Run (Abbreviated)
------------------------------------------------------------

=== Demo: Creating Sample Fleet ===
Car driving 100 km...
Truck hauling cargo 100 km...
Bus transporting passengers 100 km...
Airplane flying 100 km...
CargoShip sailing 100 km...

Fleet Report
Total vehicles: 5
Car C1 Mileage: 100.0 Efficiency: 15.0
Truck T1 Mileage: 100.0 Efficiency: 8.0
Bus B1 Mileage: 100.0 Efficiency: 10.0
Airplane A1 Mileage: 100.0 Efficiency: 2.0
CargoShip S1 Mileage: 100.0 Efficiency: 0.5

------------------------------------------------------------
CLI Menu Options
------------------------------------------------------------
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
11. Board Passengers
12. Disembark Passengers
13. Load Cargo
14. Unload Cargo
15. Sort Vehicles by Speed
16. Sort Vehicles by Model
17. Sort Vehicles by Fuel Efficiency
18. Show Fastest & Slowest Vehicle
19. Show Distinct Vehicle Models
20. Exit

------------------------------------------------------------
End of File
------------------------------------------------------------
