package app;
import vehicles.*;
import logistics.*;

public class Main {
    public static void main(String[] args) {
        FleetManager manager = new FleetManager();
        Vehicle car = new LandVehicle("Toyota", "Corolla", 2020);
        Vehicle bike = new LandVehicle("Yamaha", "YZF-R3", 2021);
        Vehicle truck = new LandVehicle("Ford", "F-150", 2019);

        manager.addVehicle(car);
        manager.addVehicle(bike);
        manager.addVehicle(truck);

        Trip trip1 = new Trip("City A", "City B", 150);

        manager.scheduleTrip(trip1, car);

        manager.generateReport();
    }
}
