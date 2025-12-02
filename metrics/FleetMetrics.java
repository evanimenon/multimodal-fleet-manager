package metrics;

public class FleetMetrics {

    private int totalJourneys;
    private int totalVehicles;
    private double totalFuelUsed;

    public synchronized void incrementJourneyCount() {
        totalJourneys++;
    }

    public synchronized void incrementVehicleCount() {
        totalVehicles++;
    }

    public synchronized void addFuelUsed(double fuel) {
        if (fuel > 0.0) {
            totalFuelUsed += fuel;
        }
    }

    public synchronized int getTotalJourneys() {
        return totalJourneys;
    }

    public synchronized int getTotalVehicles() {
        return totalVehicles;
    }

    public synchronized double getTotalFuelUsed() {
        return totalFuelUsed;
    }

    public synchronized void reset() {
        totalJourneys = 0;
        totalVehicles = 0;
        totalFuelUsed = 0.0;
    }
}
