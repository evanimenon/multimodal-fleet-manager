package logistics;

import vehicles.Vehicle;
import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.Maintainable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


/**
 * Collection-based Fleet Manager:
 * - Dynamic storage: ArrayList<Vehicle>
 * - Uniqueness set: HashSet<String> of model names
 * - Ordering/view: TreeSet via getDistinctModelsAlphabetical()
 * - Sorting: comparators for speed/model/efficiency
 * - Persistence: CSV save/load (for A2)
 */
public class FleetManager {

    private final List<Vehicle> fleet = new ArrayList<>();
    private final Set<String> modelNames = new HashSet<>();

    // ---------- CRUD / Lookup ----------

    public void addVehicle(Vehicle v) throws InvalidOperationException {
        for (Vehicle existing : fleet) {
            if (existing.getId().equals(v.getId())) {
                throw new InvalidOperationException("Duplicate vehicle ID: " + v.getId());
            }
        }
        fleet.add(v);
        modelNames.add(v.getModel());
    }

    public void removeVehicle(String id) throws InvalidOperationException {
        boolean removed = fleet.removeIf(v -> v.getId().equals(id));
        if (!removed) throw new InvalidOperationException("Vehicle with ID " + id + " not found");
    }

    public Vehicle searchById(String id) {
        for (Vehicle v : fleet) if (v.getId().equals(id)) return v;
        return null;
    }

    public List<Vehicle> searchByType(Class<?> type) {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : fleet) if (type.isInstance(v)) result.add(v);
        return result;
    }

    // ---------- Simulation / Maintenance ----------

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
            if (v instanceof FuelConsumable fc) {
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
            if (v instanceof Maintainable m && m.needsMaintenance()) {
                m.performMaintenance();
            }
        }
    }

    // ---------- Sorting / Analysis ----------

    public void sortFleetByEfficiency() {
        fleet.sort(Comparator.comparingDouble(Vehicle::calculateFuelEfficiency));
    }

    public List<Vehicle> sortBySpeed() {
        List<Vehicle> sorted = new ArrayList<>(fleet);
        sorted.sort(Comparator.comparingDouble(Vehicle::getMaxSpeed).reversed());
        return sorted;
    }

    public List<Vehicle> sortByModel() {
        List<Vehicle> sorted = new ArrayList<>(fleet);
        sorted.sort(Comparator.comparing(Vehicle::getModel));
        return sorted;
    }

    public List<Vehicle> sortByEfficiency() {
        List<Vehicle> sorted = new ArrayList<>(fleet);
        sorted.sort(Comparator.comparingDouble(Vehicle::calculateFuelEfficiency).reversed());
        return sorted;
    }

    public Vehicle getFastestVehicle() {
        return Collections.max(fleet, Comparator.comparingDouble(Vehicle::getMaxSpeed));
    }

    public Vehicle getSlowestVehicle() {
        return Collections.min(fleet, Comparator.comparingDouble(Vehicle::getMaxSpeed));
    }

    /** Distinct model names, alphabetically ordered via TreeSet view. */
    public Set<String> getDistinctModelsAlphabetical() {
        return new TreeSet<>(modelNames);
    }

    // ---------- Reporting ----------

    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fleet Report\n");
        sb.append("Total vehicles: ").append(fleet.size()).append("\n");
        double totalMileage = 0;
        for (Vehicle v : fleet) {
            totalMileage += v.getCurrentMileage();
            sb.append(v.getClass().getSimpleName())
              .append(" ID: ").append(v.getId())
              .append(", Model: ").append(v.getModel())
              .append(", MaxSpeed: ").append(v.getMaxSpeed())
              .append(", Mileage: ").append(v.getCurrentMileage())
              .append(", Efficiency: ").append(v.calculateFuelEfficiency())
              .append("\n");
        }
        sb.append("Total mileage: ").append(totalMileage).append("\n");
        return sb.toString();
    }

    public List<Vehicle> getVehiclesNeedingMaintenance() {
        List<Vehicle> needing = new ArrayList<>();
        for (Vehicle v : fleet) {
            if (v instanceof Maintainable m && m.needsMaintenance()) needing.add(v);
        }
        return needing;
    }

    // ---------- Persistence (CSV) ----------

    public void saveToFile(String filename) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filename))) {
            for (Vehicle v : fleet) {
                bw.write(v.getClass().getSimpleName() + ","
                        + v.getId() + ","
                        + v.getModel() + ","
                        + v.getMaxSpeed() + ","
                        + v.getCurrentMileage() + ","
                        + v.calculateFuelEfficiency());
                bw.newLine();
            }
            System.out.println("Fleet saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving fleet: " + e.getMessage());
        }
    }

    public void loadFromFile(String filename) {
        fleet.clear();
        modelNames.clear();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filename))) {
            String line;
            int lineNo = 0;

            while ((line = br.readLine()) != null) {
                lineNo++;
                if (line.isBlank()) continue;

                String[] p = line.split(",");
                if (p.length < 5) {
                    System.err.println("Skipping malformed line " + lineNo + ": " + line);
                    continue;
                }
                try {
                    String type     = p[0].trim();
                    String id       = p[1].trim();
                    String model    = p[2].trim();
                    double maxSpeed = Double.parseDouble(p[3].trim());
                    double mileage  = Double.parseDouble(p[4].trim());

                    Vehicle v = VehicleFactory.create(type, id, model, maxSpeed);
                    if (v != null) {
                        v.setCurrentMileage(mileage);
                        fleet.add(v);
                        modelNames.add(model);
                    } else {
                        System.err.println("Unknown vehicle type on line " + lineNo + ": " + type);
                    }
                } catch (NumberFormatException nfe) {
                    System.err.println("Skipping line " + lineNo + " due to number format: " + nfe.getMessage());
                }
            }
            System.out.println("Fleet loaded from " + filename);
        } catch (IOException e) {
            System.err.println("Error loading fleet: " + e.getMessage());
        }
    }
}
