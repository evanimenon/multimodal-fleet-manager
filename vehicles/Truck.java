package vehicles;

import logistics.InsufficientFuelException;
import logistics.InvalidOperationException;
import logistics.OverloadException;
import vehicles.interfaces.CargoCarrier;
import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.Maintainable;

public class Truck extends LandVehicle implements FuelConsumable, CargoCarrier, Maintainable {

    private double fuelLevel = 0.0;
    private final double cargoCapacity = 2000.0; 
    private double currentCargo = 0.0;
    private boolean maintenanceNeeded = false;

    public Truck(String id, String model, double maxSpeed, int numWheels) {
        super(id, model, maxSpeed, numWheels);
    }

    @Override
    public void move(double distance) throws InvalidOperationException {
        double efficiency = calculateFuelEfficiency();

        // Reduce fuel efficiency if heavily loaded (> 50% cargo)
        if (currentCargo > cargoCapacity / 2) {
            efficiency *= 0.9;
        }

        double requiredFuel = distance / efficiency;
        if (fuelLevel < requiredFuel) {
            throw new InvalidOperationException("Not enough fuel to haul cargo for " + distance + " km");
        }

        fuelLevel -= requiredFuel;
        updateMileage(distance);

        System.out.println("Hauling cargo for " + distance + " km...");

        // Maintenance check
        if (getCurrentMileage() > 10000) {
            maintenanceNeeded = true;
        }
    }

    @Override
    public double calculateFuelEfficiency() {
        return 8.0; // km per litre
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
        double efficiency = calculateFuelEfficiency();
        if (currentCargo > cargoCapacity / 2) {
            efficiency *= 0.9;
        }

        double required = distance / efficiency;
        if (fuelLevel < required) throw new InsufficientFuelException("Insufficient fuel for trip");
        fuelLevel -= required;
        return required;
    }

    // CargoCarrier
    @Override
    public void loadCargo(double weight) throws OverloadException {
        if (currentCargo + weight > cargoCapacity) {
            throw new OverloadException("Exceeds cargo capacity of " + cargoCapacity + " kg");
        }
        currentCargo += weight;
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight > currentCargo) {
            throw new InvalidOperationException("Not enough cargo to unload");
        }
        currentCargo -= weight;
    }

    @Override
    public double getCargoCapacity() {
        return cargoCapacity;
    }

    @Override
    public double getCurrentCargo() {
        return currentCargo;
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
        System.out.println("Maintenance completed for Truck " + getId());
    }
}
