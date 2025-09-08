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
        double baseTime = distance / getMaxSpeed();
        return baseTime * 1.1; // Add 10% for traffic
    }

    @Override
    public abstract void move(double distance) throws InvalidOperationException;

    @Override
    public abstract double calculateFuelEfficiency();

    public int getNumWheels() {
        return numWheels;
    }
}
