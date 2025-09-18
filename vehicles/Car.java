package vehicles;

import logistics.InvalidOperationException;
import logistics.InsufficientFuelException;
import logistics.OverloadException;
import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.PassengerCarrier;
import vehicles.interfaces.Maintainable;

public class Car extends LandVehicle
        implements FuelConsumable, PassengerCarrier, Maintainable {

    private double fuelLevel = 0.0;
    private final int passengerCapacity = 5;
    private int currentPassengers = 0;
    private boolean maintenanceNeeded = false;

    public Car(String id, String model, double maxSpeed, int numWheels) {
        super(id, model, maxSpeed, numWheels);
    }

    @Override
    public void move(double distance) throws InvalidOperationException {
        double requiredFuel = distance / calculateFuelEfficiency();
        if (fuelLevel < requiredFuel) {
            throw new InvalidOperationException("Not enough fuel to travel " + distance + " km");
        }
        fuelLevel -= requiredFuel;
        updateMileage(distance);
        System.out.println("Driving on road for " + distance + " km...");
        if (getCurrentMileage() > 10000) maintenanceNeeded = true;
    }

    @Override
    public double calculateFuelEfficiency() {
        return 15.0; // km per litre
    }

    // FuelConsumable
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) throw new InvalidOperationException("Refuel amount must be positive");
        fuelLevel += amount;
    }

    @Override
    public double getFuelLevel() {
        return fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        double needed = distance / calculateFuelEfficiency();
        if (fuelLevel < needed) throw new InsufficientFuelException("Insufficient fuel");
        fuelLevel -= needed;
        return needed;
    }

    // PassengerCarrier
    @Override
    public void boardPassengers(int count) throws OverloadException {
        if (currentPassengers + count > passengerCapacity)
            throw new OverloadException("Exceeds passenger capacity");
        currentPassengers += count;
    }

    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count > currentPassengers)
            throw new InvalidOperationException("Not enough passengers to disembark");
        currentPassengers -= count;
    }

    @Override
    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    @Override
    public int getCurrentPassengers() {
        return currentPassengers;
    }

    // Maintainable
    @Override
    public void scheduleMaintenance() {
        maintenanceNeeded = true;
    }

    @Override
    public boolean needsMaintenance() {
        return maintenanceNeeded || getCurrentMileage() > 10000;
    }

    @Override
    public void performMaintenance() {
        maintenanceNeeded = false;
        System.out.println("Maintenance completed for Car " + getId());
    }
}
