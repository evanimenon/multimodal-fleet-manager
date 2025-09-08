package vehicles;

import logistics.InvalidOperationException;

public abstract class LandVehicle extends Vehicle{
    private int numWheels;

    // Constructor
    public LandVehicle(String id, String model, double maxSpeed, int numWheels) {
        super(id, model, maxSpeed);
        this.numWheels = numWheels;
    }

    // Override estimateJourneyTime
    @Override
    public double estimateJourneyTime(double distance) {
        double baseTime = distance / super.maxSpeed;
        return baseTime * 1.1; // Add 10% for traffic
    }

    // Keep abstract as per spec
    @Override
    public abstract void move(double distance) throws InvalidOperationException;

    @Override
    public abstract double calculateFuelEfficiency();

    // Optional: Add getter if needed
    public int getNumWheels() {
        return numWheels;
    }
}
