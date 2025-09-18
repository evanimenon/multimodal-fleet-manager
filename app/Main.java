package app;

import logistics.*;
import vehicles.*;
import vehicles.interfaces.FuelConsumable;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final FleetManager manager = new FleetManager();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        runDemo(); 
        runCLI();  //to launch CLI
    }

    ///////DEMO??????//////
    private static void runDemo() {
        System.out.println("=== Demo: Creating Sample Fleet ===");
        try {
            Vehicle car      = new Car("C1", "Sedan", 120, 4);
            Vehicle truck    = new Truck("T1", "Hauler", 90, 6);
            Vehicle bus      = new Bus("B1", "CityBus", 80, 6);
            Vehicle airplane = new Airplane("A1", "Boeing", 850, 10000);
            Vehicle ship     = new CargoShip("S1", "Freighter", 40, false);

            manager.addVehicle(car);
            manager.addVehicle(truck);
            manager.addVehicle(bus);
            manager.addVehicle(airplane);
            manager.addVehicle(ship);

            // refuel all fuel-using vehicles
            for (Vehicle v : manager.searchByType(Vehicle.class)) {
                if (v instanceof FuelConsumable) {
                    ((FuelConsumable) v).refuel(200);
                }
            }

            // simulate a 100 km journey
            manager.startAllJourneys(100);

            // generate and print report
            String report = manager.generateReport();
            System.out.println(report);

            // save to CSV for testing load/save
            manager.saveToFile("sample_fleet.csv");
            System.out.println("Demo fleet saved to sample_fleet.csv\n");
        } catch (Exception e) {
            System.err.println("Demo setup failed: " + e.getMessage());
        }
    }

    ////// CLI -///////
    private static void runCLI() {
        while (true) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": addVehicle(); break;
                case "2": removeVehicle(); break;
                case "3": startJourney(); break;
                case "4": refuelAll(); break;
                case "5": manager.maintainAll(); break;
                case "6": System.out.println(manager.generateReport()); break;
                case "7": saveFleet(); break;
                case "8": loadFleet(); break;
                case "9": searchByType(); break;
                case "10": listNeedingMaintenance(); break;
                case "11": System.out.println("Exiting."); return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Fleet Manager Menu ---");
        System.out.println("1. Add Vehicle");
        System.out.println("2. Remove Vehicle");
        System.out.println("3. Start Journey");
        System.out.println("4. Refuel All");
        System.out.println("5. Perform Maintenance");
        System.out.println("6. Generate Report");
        System.out.println("7. Save Fleet");
        System.out.println("8. Load Fleet");
        System.out.println("9. Search by Type");
        System.out.println("10. List Vehicles Needing Maintenance");
        System.out.println("11. Exit");
        System.out.print("Enter choice: ");
    }

    private static void addVehicle() {
        try {
            System.out.print("Type (Car/Truck/Bus/Airplane/CargoShip): ");
            String type = sc.nextLine().trim();
            System.out.print("ID: ");
            String id = sc.nextLine().trim();
            System.out.print("Model: ");
            String model = sc.nextLine().trim();
            System.out.print("Max speed: ");
            double speed = Double.parseDouble(sc.nextLine().trim());

            Vehicle v = VehicleFactory.create(type, id, model, speed);
            if (v != null) {
                manager.addVehicle(v);
                System.out.println("Vehicle added.");
            } else {
                System.out.println("Unknown vehicle type.");
            }
        } catch (Exception e) {
            System.out.println("Error adding vehicle: " + e.getMessage());
        }
    }

    private static void removeVehicle() {
        try {
            System.out.print("Vehicle ID to remove: ");
            manager.removeVehicle(sc.nextLine().trim());
            System.out.println("Removed.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void startJourney() {
        try {
            System.out.print("Distance (km): ");
            double dist = Double.parseDouble(sc.nextLine().trim());
            manager.startAllJourneys(dist);
        } catch (NumberFormatException e) {
            System.out.println("Invalid distance.");
        }
    }

    private static void refuelAll() {
        try {
            System.out.print("Refuel amount (litres): ");
            double amt = Double.parseDouble(sc.nextLine().trim());
            for (Vehicle v : manager.searchByType(Vehicle.class)) {
                if (v instanceof FuelConsumable) {
                    ((FuelConsumable) v).refuel(amt);
                }
            }
            System.out.println("All fuel-using vehicles refueled.");
        } catch (Exception e) {
            System.out.println("Error refueling: " + e.getMessage());
        }
    }

    private static void saveFleet() {
        System.out.print("Filename to save (e.g., fleet.csv): ");
        manager.saveToFile(sc.nextLine().trim());
    }

    private static void loadFleet() {
        System.out.print("Filename to load: ");
        manager.loadFromFile(sc.nextLine().trim());
    }

    private static void searchByType() {
        System.out.print("Class name to search (e.g., Car): ");
        String type = sc.nextLine().trim();
        try {
            Class<?> clazz = Class.forName("vehicles." + type);
            List<Vehicle> results = manager.searchByType(clazz);
            results.forEach(v -> System.out.println(v.getClass().getSimpleName() + " " + v.getId()));
        } catch (ClassNotFoundException e) {
            System.out.println("Unknown class.");
        }
    }

    private static void listNeedingMaintenance() {
        List<Vehicle> list = manager.getVehiclesNeedingMaintenance();
        if (list.isEmpty()) System.out.println("No vehicles need maintenance.");
        else list.forEach(v -> System.out.println(v.getId() + " needs maintenance."));
    }
}
