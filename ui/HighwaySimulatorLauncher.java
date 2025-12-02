package ui;

import javax.swing.*;

public class HighwaySimulatorLauncher {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FleetHighwaySimulator sim = new FleetHighwaySimulator();
            sim.setVisible(true);
        });
    }
}
