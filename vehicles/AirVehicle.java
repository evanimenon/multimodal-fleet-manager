package vehicles;

import logistics.InvalidOperationException;

public abstract class AirVehicle extends Vehicle{
    private double maxAltitude;

    // Constructor
    public AirVehicle(String id, String model, double maxSpeed, double maxAltitude) {
        super(id, model, maxSpeed);
        this.maxAltitude = maxAltitude;
    }

    // Override estimateJourneyTime
    @Override
    public double estimateJourneyTime(double distance) {
        double baseTime = distance / getMaxSpeed();
        return baseTime * 0.95; // Reduce 5% for direct paths
    }

    @Override
    public abstract void move(double distance) throws InvalidOperationException;

    @Override
    public abstract double calculateFuelEfficiency();

    public double getMaxAltitude() {
        return maxAltitude;
    }
}
