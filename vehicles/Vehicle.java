package vehicles;
import logistics.InvalidOperationException;


public abstract class Vehicle{
    private String id;
    private String model;
    private double maxSpeed;
    private double currentMileage;

    // Constructor
    public Vehicle(String id, String model, double maxSpeed) {
        if (id == null || id.trim().isEmpty()){
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

    public String getId() { 
        return id;
    }

    // Protected method for subclasses to update mileage
    protected void updateMileage(double distance) {
        this.currentMileage += distance;
    }
}
