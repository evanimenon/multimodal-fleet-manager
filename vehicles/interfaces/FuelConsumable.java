package vehicles.interfaces;

import logistics.InvalidOperationException;
import logistics.InsufficientFuelException;


public interface FuelConsumable {
    void refuel(double amount) throws InvalidOperationException;
    double getFuelLevel();
    double consumeFuel(double distance) throws InsufficientFuelException;
}
