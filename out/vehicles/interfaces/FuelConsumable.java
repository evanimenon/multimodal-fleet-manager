package out.vehicles.interfaces;

import logistics.InsufficientFuelException;
import logistics.InvalidOperationException;

public interface FuelConsumable {

    void refuel(double amount) throws InvalidOperationException;

    double getFuelLevel();

    double consumeFuel(double distance) throws InsufficientFuelException;
}
