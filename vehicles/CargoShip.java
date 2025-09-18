package vehicles;

import logistics.InvalidOperationException;
import logistics.InsufficientFuelException;
import logistics.OverloadException;
import vehicles.interfaces.CargoCarrier;
import vehicles.interfaces.Maintainable;
import vehicles.interfaces.FuelConsumable;

public class CargoShip extends WaterVehicle
        implements CargoCarrier, Maintainable, FuelConsumable {

    private final double cargoCapacity = 50000.0; // kg
    private double currentCargo = 0.0;
    private boolean maintenanceNeeded = false;

    // fuel-related only if hasSail == false
    private double fuelLevel = 0.0;

    public CargoShip(String id, String model, double maxSpeed, boolean hasSail) {
        super(id, model, maxSpeed, hasSail);
    }

    @Override
    public void move(double distance) throws InvalidOperationException {
        if (!gethasSail()) {
            double requiredFuel = distance / calculateFuelEfficiency();
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
        // 4 km/l if engine powered, else 0 (sail only)
        return gethasSail() ? 0.0 : 4.0;
    }

    // CargoCarrier
    @Override
    public void loadCargo(double weight) throws OverloadException {
        if (currentCargo + weight > cargoCapacity)
            throw new OverloadException("Exceeds cargo capacity");
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

    // FuelConsumable (only meaningful if hasSail == false)
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (gethasSail())
            throw new InvalidOperationException("This ship uses sails and cannot be refueled");
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
            throw new InsufficientFuelException("Sailing ship does not use fuel");
        double needed = distance / calculateFuelEfficiency();
        if (fuelLevel < needed)
            throw new InsufficientFuelException("Insufficient fuel for voyage");
        fuelLevel -= needed;
        return needed;
    }
}
