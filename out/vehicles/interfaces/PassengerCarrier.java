package out.vehicles.interfaces;

import logistics.InvalidOperationException;
import logistics.OverloadException;

public interface PassengerCarrier {

    void boardPassengers(int count) throws OverloadException;

    void disembarkPassengers(int count) throws InvalidOperationException;

    int getPassengerCapacity();

    int getCurrentPassengers();
}
