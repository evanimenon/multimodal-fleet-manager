package logistics;

import vehicles.Vehicle;
import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.Maintainable;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FleetManager {

    private final List<Vehicle> fleet = new ArrayList<>();
//add vehicle
    public void addVehicle(Vehicle v) throws InvalidOperationException {
        for (Vehicle existing : fleet) {
            if (existing.getId().equals(v.getId())) {
                throw new InvalidOperationException("Duplicate vehicle ID: " + v.getId());
            }
        }
        fleet.add(v);
    }
//remove vehicle
    public void removeVehicle(String id) throws InvalidOperationException {
        boolean removed = fleet.removeIf(v -> v.getId().equals(id));
        if (!removed) {
            throw new InvalidOperationException("Vehicle with ID " + id + " not found");
        }
    }
//start all journey
    public void startAllJourneys(double distance) {
        for (Vehicle v : fleet) {
            try {
                v.move(distance);
            } catch (Exception e) {
                System.err.println("Journey failed for " + v.getId() + ": " + e.getMessage());
            }
        }
    }

    public double getTotalFuelConsumption(double distance) {
        double total = 0.0;
        for (Vehicle v : fleet) {
            if (v instanceof FuelConsumable) {
                FuelConsumable fc = (FuelConsumable) v;
                try {
                    total += fc.consumeFuel(distance);
                } catch (Exception e) {
                    System.err.println("Fuel calc failed for " + v.getId() + ": " + e.getMessage());
                }
            }
        }
        return total;
    }

    public void maintainAll() {
        for (Vehicle v : fleet) {
            if (v instanceof Maintainable) {
                Maintainable m = (Maintainable) v;
                if (m.needsMaintenance()) {
                    m.performMaintenance();
                }
            }
        }
    }

    //search by type
    public List<Vehicle> searchByType(Class<?> type) {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : fleet) {
            if (type.isInstance(v)) {
                result.add(v);
            }
        }
        return result;
    }

    public void sortFleetByEfficiency() {
        Collections.sort(fleet, Comparator.comparingDouble(Vehicle::calculateFuelEfficiency));
    }

    //generate report that includes total mileage and efficiency of each vehicle
    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fleet Report\n");
        sb.append("Total vehicles: ").append(fleet.size()).append("\n");
        double totalMileage = 0;
        for (Vehicle v : fleet) {
            totalMileage += v.getCurrentMileage();
            sb.append(v.getClass().getSimpleName())
                    .append(" ID: ").append(v.getId())
                    .append(", Mileage: ").append(v.getCurrentMileage())
                    .append(", Efficiency: ").append(v.calculateFuelEfficiency())
                    .append("\n");
        }
        sb.append("Total mileage: ").append(totalMileage).append("\n");
        return sb.toString();
    }

    //list of vehicles needing maintenance
    public List<Vehicle> getVehiclesNeedingMaintenance() {
        List<Vehicle> needing = new ArrayList<>();
        for (Vehicle v : fleet) {
            if (v instanceof Maintainable) {
                Maintainable m = (Maintainable) v;
                if (m.needsMaintenance()) {
                    needing.add(v);
                }
            }
        }
        return needing;
    }

    /////// Persistence //////

    public void saveToFile(String filename) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filename))) {
            for (Vehicle v : fleet) {
                // Basic CSV: ClassName,id,model,maxSpeed,currentMileage
                bw.write(v.getClass().getSimpleName() + ","
                        + v.getId() + ","
                        + v.getModel() + ","
                        + v.getMaxSpeed() + ","
                        + v.getCurrentMileage());
                bw.newLine();
            }
            System.out.println("Fleet saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving fleet: " + e.getMessage());
        }
    }

    public void loadFromFile(String filename) {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5)
                    continue;

                // Parse fields
                String type = parts[0];
                String id = parts[1];
                String model = parts[2];
                double maxSpeed = Double.parseDouble(parts[3]);
                double mileage = Double.parseDouble(parts[4]);

                // Create vehicle via factory
                Vehicle v = VehicleFactory.create(type, id, model, maxSpeed);
                if (v != null) {
                    v.setCurrentMileage(mileage); // - applies the value read from CSV
                    fleet.add(v);
                }

            }
            System.out.println("Fleet loaded from " + filename);
        } catch (IOException e) {
            System.err.println("Error loading fleet: " + e.getMessage());
        }
    }
}
