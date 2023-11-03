package config;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestDataCreation {
    private static final String CURRENT_DIR = System.getProperty("user.dir");
    private static final String FILE_PATH = CURRENT_DIR + File.separator + "test_data" + File.separator + "test_data_";

    public static void createData(int nrOfLines) {
        System.out.println("Starting data creation.");

        try {
            Files.createDirectories(Path.of(CURRENT_DIR + File.separator + "test_data"));
        } catch (IOException e) {
            e.printStackTrace();
            return; // Exit if directory creation fails
        }

        // Generate data for the bottom sensors of all 7 reservoirs
        for (int i = 1; i <= 7; i++) {
            String bottomSensorFile = FILE_PATH + "reservoir_" + i + "_bottom.txt";
            String topSensorFile = FILE_PATH + "reservoir_" + i + "_top.txt";

            // Simulate the process for the bottom sensor
            writeSensorData(bottomSensorFile, nrOfLines, false);

            // Simulate the process for the top sensor based on the bottom sensor data
            writeSensorData(topSensorFile, nrOfLines, true);
        }

        System.out.println("Data creation completed.");
    }

    private static void writeSensorData(String filePath, int nrOfLines, boolean isTopSensor) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            boolean isBottomSensorDry = true; // Initially, the bottom sensor is dry
            int fillCounter = 0; // Counts the number of times the pump has been on

            for (int j = 0; j < nrOfLines; j++) {
                boolean sensorTrigger;

                if (isBottomSensorDry) {
                    // The bottom sensor triggers false twice before turning true
                    sensorTrigger = (fillCounter >= 2);
                    if (!sensorTrigger) {
                        fillCounter++;
                    }
                } else {
                    // Once the reservoir is full, trigger true twice more before emptying
                    if (fillCounter < 7) {
                        sensorTrigger = true;
                        fillCounter++;
                    } else {
                        // Start emptying the reservoir
                        sensorTrigger = (j % 3 != 0); // Example pattern for depleting
                        isBottomSensorDry = !sensorTrigger; // If bottom is dry, top should also be dry
                    }
                }

                // Write data for top sensor based on bottom sensor state
                if (isTopSensor) {
                    writer.write(String.valueOf(!isBottomSensorDry));
                } else {
                    writer.write(String.valueOf(sensorTrigger));
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}