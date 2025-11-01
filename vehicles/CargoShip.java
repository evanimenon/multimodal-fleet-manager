package vehicles;

import logistics.InvalidOperationException;
import logistics.InsufficientFuelException;
import logistics.OverloadException;
import vehicles.interfaces.CargoCarrier;
import vehicles.interfaces.Maintainable;
import vehicles.interfaces.FuelConsumable;

public class CargoShip extends WaterVehicle
        implements CargoCarrier, Maintainable, FuelConsumable {

    private final double cargoCapacity = 200000.0; 
    private double currentCargo = 0.0;
    private boolean maintenanceNeeded = false;

    // Fuel tracking (used only if not sailing)
    private double fuelLevel = 0.0;

    public CargoShip(String id, String model, double maxSpeed, boolean hasSail) {
        super(id, model, maxSpeed, hasSail);
    }

    @Override
    public void move(double distance) throws InvalidOperationException {
        double efficiency = calculateFuelEfficiency();

        if (currentCargo > cargoCapacity / 2 && !gethasSail()) {
            efficiency *= 0.9;
        }

        if (!gethasSail()) {
            double requiredFuel = distance / efficiency;
            if (fuelLevel < requiredFuel) {
                throw new InvalidOperationException("Not enough fuel to sail " + distance + " km");
            }
            fuelLevel -= requiredFuel;
        }

        updateMileage(distance);
        System.out.println("Sailing with cargo for " + distance + " km...");

        if (getCurrentMileage() > 10000) maintenanceNeeded = true;
    }

    @Override
    public double calculateFuelEfficiency() {
        return gethasSail() ? 0.0 : 0.5;
    }

    // CargoCarrier
    @Override
    public void loadCargo(double weight) throws OverloadException {
        if (currentCargo + weight > cargoCapacity)
            throw new OverloadException("Exceeds cargo capacity of " + cargoCapacity + " kg");
        currentCargo += weight;
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight > currentCargo)
            throw new InvalidOperationException("Not enough cargo to unload");
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
        System.out.println("Maintenance completed for CargoShip " + getId());
    }

    // FuelConsumable (only if engine-powered)
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (gethasSail())
            throw new InvalidOperationException("This ship uses sails and cannot be refueled.");
        if (amount <= 0) throw new InvalidOperationException("Refuel amount must be positive");
        fuelLevel += amount;
    }

    @Override
    public double getFuelLevel() {
        return gethasSail() ? 0.0 : fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        if (gethasSail())
            throw new InsufficientFuelException("This ship does not consume fuel while sailing.");
        double needed = distance / calculateFuelEfficiency();
        if (fuelLevel < needed)
            throw new InsufficientFuelException("Insufficient fuel for voyage");
        fuelLevel -= needed;
        return needed;
    }
}
