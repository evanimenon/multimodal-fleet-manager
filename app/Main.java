package app;

import logistics.*;
import vehicles.*;
import vehicles.interfaces.CargoCarrier;
import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.PassengerCarrier;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final FleetManager manager = new FleetManager();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        runDemo();   // quick demo
        runCLI();   
    }

    // -------- Demo --------
    private static void runDemo() {
        System.out.println("=== Demo: Creating Sample Fleet ===");
        try {
            Vehicle car      = new Car("C1", "Sedan",     120, 4);
            Vehicle truck    = new Truck("T1", "Hauler",   90, 6);
            Vehicle bus      = new Bus("B1", "CityBus",    80, 6);
            Vehicle airplane = new Airplane("A1", "Boeing",850, 10000);
            Vehicle ship     = new CargoShip("S1", "Freighter", 40, false);

            manager.addVehicle(car);
            manager.addVehicle(truck);
            manager.addVehicle(bus);
            manager.addVehicle(airplane);
            manager.addVehicle(ship);

            // Refuel all fuel-using vehicles
            for (Vehicle v : manager.searchByType(Vehicle.class)) {
                if (v instanceof FuelConsumable fc) fc.refuel(200);
            }

            // Simulate 100 km
            manager.startAllJourneys(100);
            System.out.println(manager.generateReport());
            manager.saveToFile("sample_fleet.csv");
            System.out.println("Demo fleet saved to sample_fleet.csv\n");
        } catch (Exception e) {
            System.err.println("Demo setup failed: " + e.getMessage());
        }
    }

    // -------- CLI --------
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
                case "11": boardPassengers(); break;
                case "12": disembarkPassengers(); break;
                case "13": loadCargo(); break;
                case "14": unloadCargo(); break;
                case "15": sortBySpeed(); break;
                case "16": sortByModel(); break;
                case "17": sortByEfficiency(); break;
                case "18": showFastestSlowest(); break;
                case "19": showDistinctModels(); break;
                case "20": System.out.println("Exiting."); return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Fleet Manager Menu ---");
        System.out.println("1.  Add Vehicle");
        System.out.println("2.  Remove Vehicle");
        System.out.println("3.  Start Journey");
        System.out.println("4.  Refuel All");
        System.out.println("5.  Perform Maintenance");
        System.out.println("6.  Generate Report");
        System.out.println("7.  Save Fleet");
        System.out.println("8.  Load Fleet");
        System.out.println("9.  Search by Type");
        System.out.println("10. List Vehicles Needing Maintenance");
        System.out.println("11. Board Passengers");
        System.out.println("12. Disembark Passengers");
        System.out.println("13. Load Cargo");
        System.out.println("14. Unload Cargo");
        System.out.println("15. Sort Vehicles by Speed");
        System.out.println("16. Sort Vehicles by Model");
        System.out.println("17. Sort Vehicles by Fuel Efficiency");
        System.out.println("18. Show Fastest & Slowest Vehicle");
        System.out.println("19. Show Distinct Vehicle Models");
        System.out.println("20. Exit");
        System.out.print("Enter choice: ");
    }

    // -------- Actions --------

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
                if (v instanceof FuelConsumable fc) fc.refuel(amt);
            }
            System.out.println("All fuel-using vehicles refueled.");
        } catch (Exception e) {
            System.out.println("Error refueling: " + e.getMessage());
        }
    }

    private static void saveFleet() {
        System.out.print("Filename to save: ");
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

    private static void boardPassengers() {
        System.out.print("Vehicle ID: ");
        Vehicle v = manager.searchById(sc.nextLine().trim());
        if (v instanceof PassengerCarrier pc) {
            System.out.print("Passengers to board: ");
            int count = Integer.parseInt(sc.nextLine().trim());
            try {
                pc.boardPassengers(count);
                System.out.println("Boarded " + count + " passengers.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else System.out.println("This vehicle does not carry passengers.");
    }

    private static void disembarkPassengers() {
        System.out.print("Vehicle ID: ");
        Vehicle v = manager.searchById(sc.nextLine().trim());
        if (v instanceof PassengerCarrier pc) {
            System.out.print("Passengers to disembark: ");
            int count = Integer.parseInt(sc.nextLine().trim());
            try {
                pc.disembarkPassengers(count);
                System.out.println("Disembarked " + count + " passengers.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else System.out.println("This vehicle does not carry passengers.");
    }

    private static void loadCargo() {
        System.out.print("Vehicle ID: ");
        Vehicle v = manager.searchById(sc.nextLine().trim());
        if (v instanceof CargoCarrier cc) {
            System.out.print("Cargo weight (kg): ");
            double w = Double.parseDouble(sc.nextLine().trim());
            try {
                cc.loadCargo(w);
                System.out.println("Loaded " + w + " kg.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else System.out.println("This vehicle does not carry cargo.");
    }

    private static void unloadCargo() {
        System.out.print("Vehicle ID: ");
        Vehicle v = manager.searchById(sc.nextLine().trim());
        if (v instanceof CargoCarrier cc) {
            System.out.print("Cargo weight (kg): ");
            double w = Double.parseDouble(sc.nextLine().trim());
            try {
                cc.unloadCargo(w);
                System.out.println("Unloaded " + w + " kg.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else System.out.println("This vehicle does not carry cargo.");
    }

    private static void sortBySpeed() {
        manager.sortBySpeed().forEach(v ->
                System.out.println(v.getId() + " - " + v.getMaxSpeed() + " km/h"));
    }

    private static void sortByModel() {
        manager.sortByModel().forEach(v ->
                System.out.println(v.getId() + " - " + v.getModel()));
    }

    private static void sortByEfficiency() {
        manager.sortByEfficiency().forEach(v ->
                System.out.println(v.getId() + " - " + v.calculateFuelEfficiency() + " km/l"));
    }

    private static void showFastestSlowest() {
        System.out.println("Fastest: " + manager.getFastestVehicle().getId());
        System.out.println("Slowest: " + manager.getSlowestVehicle().getId());
    }

    private static void showDistinctModels() {
        System.out.println("Distinct Models: " + manager.getDistinctModelsAlphabetical());
    }
}
