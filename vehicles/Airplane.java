package vehicles;

import logistics.InvalidOperationException;
import logistics.InsufficientFuelException;
import logistics.OverloadException;
import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.PassengerCarrier;
import vehicles.interfaces.CargoCarrier;
import vehicles.interfaces.Maintainable;

public class Airplane extends AirVehicle
        implements FuelConsumable, PassengerCarrier, CargoCarrier, Maintainable {

    private double fuelLevel = 0.0;
    private final int passengerCapacity = 200;
    private int currentPassengers = 0;
    private final double cargoCapacity = 10000.0; 
    private double currentCargo = 0.0;
    private boolean maintenanceNeeded = false;

    public Airplane(String id, String model, double maxSpeed, double maxAltitude) {
        super(id, model, maxSpeed, maxAltitude);
    }

    @Override
    public void move(double distance) throws InvalidOperationException {
        double requiredFuel = distance / calculateFuelEfficiency();
        if (fuelLevel < requiredFuel) {
            throw new InvalidOperationException(
                "Not enough fuel to fly " + distance + " km at altitude " + getMaxAltitude()
            );
        }
        fuelLevel -= requiredFuel;
        updateMileage(distance);
        System.out.println("Flying at " + getMaxAltitude() + " meters for " + distance + " km...");
        if (getCurrentMileage() > 10000) maintenanceNeeded = true;
    }

    @Override
    public double calculateFuelEfficiency() {
        return 5.0; // km per litre
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
        System.out.println("Maintenance completed for Airplane " + getId());
    }
}
