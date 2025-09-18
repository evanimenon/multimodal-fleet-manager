package app;

import logistics.*;
import vehicles.*;
import java.util.*;
import vehicles.interfaces.FuelConsumable;

public class Main {
    private static final FleetManager manager = new FleetManager();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
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
            System.out.print("Enter vehicle type (Car/Truck/Bus/Airplane/CargoShip): ");
            String type = sc.nextLine().trim();
            System.out.print("Enter ID: ");
            String id = sc.nextLine().trim();
            System.out.print("Enter model: ");
            String model = sc.nextLine().trim();
            System.out.print("Enter max speed: ");
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
            System.out.print("Enter vehicle ID to remove: ");
            manager.removeVehicle(sc.nextLine().trim());
            System.out.println("Removed.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void startJourney() {
        try {
            System.out.print("Enter distance (km): ");
            double dist = Double.parseDouble(sc.nextLine().trim());
            manager.startAllJourneys(dist);
        } catch (NumberFormatException e) {
            System.out.println("Invalid distance.");
        }
    }

    private static void refuelAll() {
        try {
            System.out.print("Enter refuel amount (litres) for all fuel-using vehicles: ");
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
        System.out.print("Enter filename to save (e.g., fleet.csv): ");
        manager.saveToFile(sc.nextLine().trim());
    }

    private static void loadFleet() {
        System.out.print("Enter filename to load: ");
        manager.loadFromFile(sc.nextLine().trim());
    }

    private static void searchByType() {
        System.out.print("Enter class name to search (e.g., Car): ");
        String type = sc.nextLine().trim();
        try {
            // dynamic class lookup from vehicles package
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
