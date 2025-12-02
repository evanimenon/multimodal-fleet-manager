package ui;

import logistics.InvalidOperationException;
import vehicles.Vehicle;
import vehicles.interfaces.FuelConsumable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;


public class VehicleStatusPanel extends JPanel {

    private final Vehicle vehicle;

    private final JLabel mileageLabel   = new JLabel();
    private final JLabel fuelLabel      = new JLabel();
    private final JLabel statusLabel    = new JLabel("Idle");

    public VehicleStatusPanel(Vehicle vehicle) {
        this.vehicle = vehicle;

        setLayout(new BorderLayout(8, 4));

        JLabel idLabel    = new JLabel(vehicle.getId());
        JLabel modelLabel = new JLabel(vehicle.getModel());
        idLabel.setFont(idLabel.getFont().deriveFont(Font.BOLD, 14f));

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.add(idLabel);
        header.add(modelLabel);

        JPanel stats = new JPanel(new GridLayout(3, 1));
        stats.add(mileageLabel);
        stats.add(fuelLabel);
        stats.add(statusLabel);

        JButton refuelButton = new JButton("Refuel +50");
        refuelButton.addActionListener(e -> refuel());

        JPanel right = new JPanel(new BorderLayout());
        right.add(refuelButton, BorderLayout.CENTER);

        add(header, BorderLayout.WEST);
        add(stats, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);

        setBorder(new TitledBorder(""));
        refreshFromVehicle("Idle");
    }

    public void refreshFromVehicle(String statusText) {
        mileageLabel.setText(String.format("Mileage: %.1f km", vehicle.getCurrentMileage()));

        if (vehicle instanceof FuelConsumable fc) {
            fuelLabel.setText(String.format("Fuel: %.1f L", fc.getFuelLevel()));
        } else {
            fuelLabel.setText("Fuel: N/A");
        }

        statusLabel.setText("Status: " + statusText);
    }

    private void refuel() {
        if (vehicle instanceof FuelConsumable fc) {
            try {
                fc.refuel(10.0); 
                refreshFromVehicle("Refueled");
            } catch (InvalidOperationException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Cannot refuel: " + ex.getMessage(),
                        "Refuel error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "This vehicle type does not support refuelling.",
                    "Not fuel-based",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
}
