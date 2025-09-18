package vehicles.interfaces;

import logistics.InvalidOperationException;
import logistics.OverloadException;

public interface CargoCarrier {
    void loadCargo(double weight) throws OverloadException;
    void unloadCargo(double weight) throws InvalidOperationException;
    double getCargoCapacity();
    double getCurrentCargo();
}
