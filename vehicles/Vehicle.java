package vehicles;

import logistics.InvalidOperationException;

public abstract class Vehicle implements Comparable<Vehicle> {
    private String id;
    private String model;
    private double maxSpeed;
    private double currentMileage;

    // Constructor
    public Vehicle(String id, String model, double maxSpeed) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        this.id = id;
        this.model = model;
        this.maxSpeed = maxSpeed;
        this.currentMileage = 0.0;
    }

    // Abstract methods
    public abstract void move(double distance) throws InvalidOperationException;
    public abstract double calculateFuelEfficiency();
    public abstract double estimateJourneyTime(double distance);

    // Concrete methods
    public void displayInfo() {
        System.out.println("Vehicle ID: " + id);
        System.out.println("Model: " + model);
        System.out.println("Max Speed: " + maxSpeed + " km/h");
        System.out.println("Total Mileage: " + currentMileage + " km");
    }

    public double getCurrentMileage() {
        return currentMileage;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public void setCurrentMileage(double mileage) {
        if (mileage < 0) {
            throw new IllegalArgumentException("Mileage cannot be negative");
        }
        this.currentMileage = mileage;
    }

    protected void updateMileage(double distance) {
        this.currentMileage += distance;
    }

    // Comparable implementation for sorting by fuel efficiency
    @Override
    public int compareTo(Vehicle other) {
        return Double.compare(this.calculateFuelEfficiency(),
                              other.calculateFuelEfficiency());
    }
}
