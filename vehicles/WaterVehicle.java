package vehicles;

import logistics.InvalidOperationException;

public abstract class WaterVehicle extends Vehicle{
    private boolean hasSail;

    // Constructor
    public WaterVehicle(String id, String model, double maxSpeed, boolean hasSail) {
        super(id, model, maxSpeed);
        this.hasSail = hasSail;
    }

    // Override estimateJourneyTime
    @Override
    public double estimateJourneyTime(double distance) {
        double baseTime = distance / getMaxSpeed();
        return baseTime * 1.15; // Add 15% for currents
    }

    @Override
    public abstract void move(double distance) throws InvalidOperationException;

    @Override
    public abstract double calculateFuelEfficiency();

    public boolean gethasSail() {
        return hasSail;
    }
}
