package logistics;

import vehicles.*;

public class VehicleFactory {

    //Creates a vehicle instance from a type string.

    public static Vehicle create(String type, String id, String model, double maxSpeed) {
        switch (type) {
            case "Car":
                return new Car(id, model, maxSpeed, 4);
            case "Truck":
                return new Truck(id, model, maxSpeed, 6);
            case "Bus":
                return new Bus(id, model, maxSpeed, 6);
            case "Airplane":
                return new Airplane(id, model, maxSpeed, 10000); //10000 sample altitude
            case "CargoShip":
                return new CargoShip(id, model, maxSpeed, false); // set hasSail as needed
            default:
                System.err.println("Unknown vehicle type: " + type);
                return null;
        }
    }
}
