package ui;

import logistics.FleetManager;
import vehicles.Vehicle;
import vehicles.interfaces.FuelConsumable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


public class FleetHighwaySimulator extends JFrame {

    // --- Model / controller ---
    private final FleetManager fleetManager = new FleetManager();
    private final Map<String, VehicleStatusPanel> panelById = new HashMap<>();

    private final HighwayState highwayState = new HighwayState();

    private final Map<String, Thread> workers = new HashMap<>();

    private final JButton startButton   = new JButton("Start");
    private final JButton pauseButton   = new JButton("Pause");
    private final JButton resumeButton  = new JButton("Resume");
    private final JButton stopButton    = new JButton("Stop & Reset");
    private final JCheckBox lockCheck   = new JCheckBox("Use lock (fix race condition)");

    private final JLabel highwayLabel   = new JLabel("Highway distance: 0.0 km");
    private final JLabel sumLabel       = new JLabel("Sum of individual mileages: 0.0 km");

    private final JPanel vehicleListPanel = new JPanel();

    public FleetHighwaySimulator() {
        super("Fleet Highway Simulator");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        try {
            fleetManager.loadFromFile("sample_fleet.csv");
        } catch (Exception ex) {
            System.err.println("Could not load sample_fleet.csv: " + ex.getMessage());
        }

        buildUi();
        refreshAllVehiclePanels("Idle");
        updateSummary();

        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    private void buildUi() {
        // Top control bar
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(startButton);
        controls.add(pauseButton);
        controls.add(resumeButton);
        controls.add(stopButton);
        controls.add(lockCheck);

        // center: vehicle cards in scroll pane
        vehicleListPanel.setLayout(new BoxLayout(vehicleListPanel, BoxLayout.Y_AXIS));
        populateVehiclePanels();

        JScrollPane scroll = new JScrollPane(
                vehicleListPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setBorder(BorderFactory.createTitledBorder("Fleet vehicles"));

        // Right summary panel
        JPanel summary = new JPanel();
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        highwayLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sumLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summary.add(highwayLabel);
        summary.add(Box.createVerticalStrut(8));
        summary.add(sumLabel);

        // Layout main content
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                scroll,
                summary
        );
        split.setResizeWeight(0.7);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(controls, BorderLayout.NORTH);
        getContentPane().add(split, BorderLayout.CENTER);

        // --- Wire actions ---
        startButton.addActionListener(e -> onStart());
        pauseButton.addActionListener(e -> onPause());
        resumeButton.addActionListener(e -> onResume());
        stopButton.addActionListener(e -> onStopAndReset());
        lockCheck.addActionListener(e -> highwayState.useLock = lockCheck.isSelected());
    }

    private void populateVehiclePanels() {
        vehicleListPanel.removeAll();
        panelById.clear();

        List<Vehicle> vehicles = fleetManager.getAllVehicles();
        for (Vehicle v : vehicles) {
            VehicleStatusPanel panel = new VehicleStatusPanel(v);
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
            vehicleListPanel.add(panel);
            vehicleListPanel.add(Box.createVerticalStrut(8));
            panelById.put(v.getId(), panel);
        }

        vehicleListPanel.revalidate();
        vehicleListPanel.repaint();
    }

    private void onStart() {
        if (highwayState.running) {
            return; // already running
        }

        // reset state
        highwayState.running = true;
        highwayState.paused  = false;
        highwayState.stopped = false;
        highwayState.highwayDistance = 0.0;

        lockCheck.setEnabled(true); // allow choice before race

        for (Vehicle v : fleetManager.getAllVehicles()) {
            v.setCurrentMileage(0.0);
            if (v instanceof FuelConsumable fc) {

            }
        }
        refreshAllVehiclePanels("Running");
        updateSummary();

        // spawn worker threads
        workers.clear();
        for (Vehicle v : fleetManager.getAllVehicles()) {
            VehicleWorker worker = new VehicleWorker(v);
            Thread t = new Thread(worker, "VehicleWorker-" + v.getId());
            workers.put(v.getId(), t);
            t.start();
        }

        // button states
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void onPause() {
        if (!highwayState.running) return;
        highwayState.paused = true;

        pauseButton.setEnabled(false);
        resumeButton.setEnabled(true);
        refreshAllVehiclePanels("Paused");
    }

    private void onResume() {
        if (!highwayState.running) return;
        highwayState.paused = false;

        pauseButton.setEnabled(true);
        resumeButton.setEnabled(false);
        refreshAllVehiclePanels("Running");
    }

    private void onStopAndReset() {
        highwayState.stopped = true;
        highwayState.running = false;
        highwayState.paused  = false;

        // let worker threads exit gracefully
        for (Thread t : workers.values()) {
            t.interrupt();
        }
        workers.clear();

        // reset metrics & vehicles
        highwayState.highwayDistance = 0.0;
        for (Vehicle v : fleetManager.getAllVehicles()) {
            v.setCurrentMileage(0.0);
        }
        refreshAllVehiclePanels("Idle");
        updateSummary();

        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    private void refreshAllVehiclePanels(String status) {
        for (VehicleStatusPanel panel : panelById.values()) {
            panel.refreshFromVehicle(status);
        }
    }

    private void updateSummary() {
        double sum = 0.0;
        for (Vehicle v : fleetManager.getAllVehicles()) {
            sum += v.getCurrentMileage();
        }

        highwayLabel.setText(String.format("Highway distance: %.1f km", highwayState.highwayDistance));
        sumLabel.setText(String.format("Sum of individual mileages: %.1f km", sum));
    }

    private static class HighwayState {
        volatile boolean running = false;
        volatile boolean paused  = false;
        volatile boolean stopped = false;

        volatile boolean useLock = false;

        double highwayDistance = 0.0;
        final ReentrantLock lock = new ReentrantLock();

        void addDistance(double delta) {
            if (useLock) {
                lock.lock();
                try {
                    highwayDistance += delta;
                } finally {
                    lock.unlock();
                }
            } else {
                highwayDistance += delta;
            }
        }
    }

    private class VehicleWorker implements Runnable {

        private final Vehicle vehicle;
        private final double stepKm = 1.0;     // step size per update
        private final long sleepMs = 80L;      // simulation speed

        VehicleWorker(Vehicle vehicle) {
            this.vehicle = vehicle;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()
                        && highwayState.running
                        && !highwayState.stopped) {

                    if (highwayState.paused) {
                        Thread.sleep(80);
                        continue;
                    }

                    try {
                        vehicle.move(stepKm);
                        highwayState.addDistance(stepKm);

                        // reflect changes in GUI
                        SwingUtilities.invokeLater(() -> {
                            VehicleStatusPanel panel = panelById.get(vehicle.getId());
                            if (panel != null) {
                                panel.refreshFromVehicle("Running");
                            }
                            updateSummary();
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        SwingUtilities.invokeLater(() -> {
                            VehicleStatusPanel panel = panelById.get(vehicle.getId());
                            if (panel != null) {
                                panel.refreshFromVehicle("Error");
                            }
                        });
                        break; // stop this worker on error
                    }

                    Thread.sleep(sleepMs);
                }
            } catch (InterruptedException ie) {
                // exiting thread
            }
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FleetHighwaySimulator sim = new FleetHighwaySimulator();
            sim.setVisible(true);
        });
    }
}
