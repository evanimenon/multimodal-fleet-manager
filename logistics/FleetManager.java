package logistics;

import vehicles.Vehicle;
import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.Maintainable;

import indexing.VehicleHashTable;
import metrics.FleetMetrics;

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
 * - A3: Hash-table index + global metrics
 */
public class FleetManager {

    private final List<Vehicle> fleet = new ArrayList<>();
    private final Set<String> modelNames = new HashSet<>();

    // A3 additions
    private final VehicleHashTable index = new VehicleHashTable();
    private final FleetMetrics metrics = new FleetMetrics();

    // --- A3 helpers for GUI / metrics ---

    public FleetMetrics getMetrics() {
        return metrics;
    }

    /** Read-only view of all vehicles, useful for GUI listing. */
    public List<Vehicle> getAllVehicles() {
        return Collections.unmodifiableList(fleet);
    }

    // ---------- CRUD / Lookup ----------

    public void addVehicle(Vehicle v) throws InvalidOperationException {
        for (Vehicle existing : fleet) {
            if (existing.getId().equals(v.getId())) {
                throw new InvalidOperationException("Duplicate vehicle ID: " + v.getId());
            }
        }
        fleet.add(v);
        modelNames.add(v.getModel());

        // A3: keep hash index + metrics in sync
        index.put(v.getId(), v);
        metrics.incrementVehicleCount();
    }

    public void removeVehicle(String id) throws InvalidOperationException {
        boolean removed = fleet.removeIf(v -> {
            if (v.getId().equals(id)) {
                index.remove(id);          // A3: remove from hash index too
                return true;
            }
            return false;
        });
        if (!removed) {
            throw new InvalidOperationException("Vehicle with ID " + id + " not found");
        }
    }

    public Vehicle searchById(String id) {
        // Prefer fast hash-table lookup
        Vehicle v = index.get(id);
        if (v != null) return v;

        // Fallback linear scan (defensive)
        for (Vehicle veh : fleet) {
            if (veh.getId().equals(id)) return veh;
        }
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
                // A3: count journeys globally
                metrics.incrementJourneyCount();
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

    /** A2 method your Main is calling – kept exactly the same. */
    public List<Vehicle> getVehiclesNeedingMaintenance() {
        List<Vehicle> needing = new ArrayList<>();
        for (Vehicle v : fleet) {
            if (v instanceof Maintainable m && m.needsMaintenance()) {
                needing.add(v);
            }
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
        index.clear();
        metrics.reset();

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

                        // A3: keep index + metrics current
                        index.put(id, v);
                        metrics.incrementVehicleCount();
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
