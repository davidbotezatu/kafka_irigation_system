package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

/**
 * Utility class for generating simulated sensor data for reservoirs.
 * This data is intended for testing purposes and mimics the behavior of actual sensors
 * by producing readings of water levels and corresponding sensor states.
 * <p>
 * The class is designed to create a directory, if not existent, and populate it with text files
 * containing the test data for a predefined number of reservoirs.
 */
public class TestDataCreation {
    // Directory path constants where test data files will be stored.
    private static final String TEST_DATA_DIRECTORY = "test_data";
    private static final String CURRENT_DIR = System.getProperty("user.dir");
    private static final String FILE_PATH = CURRENT_DIR + File.separator + TEST_DATA_DIRECTORY + File.separator;

    // Constants representing the total number of reservoirs and the index indicating a full reservoir.
    private static final int NUMBER_OF_RESERVOIRS = 7;
    private static final int FULL_RESERVOIR = 4;

    /**
     * Enum representing discrete water levels within a reservoir.
     */
    private enum WaterLevel {
        EMPTY, QUARTER_FULL, HALF_FULL, THREE_QUARTERS_FULL, FULL
    }

    /**
     * Initiates the creation of test data for the specified number of data points.
     *
     * @param nrOfDataPoints the number of sensor data points to generate for each reservoir.
     */
    public static void createData(int nrOfDataPoints) {
        System.out.println("Starting data creation.");
        createDirectory();
        generateDataForAllReservoirs(nrOfDataPoints);
        System.out.println("Data creation completed.");
    }

    /**
     * Creates a directory at the specified file path if it does not already exist.
     *
     * @throws RuntimeException if the directory cannot be created.
     */
    private static void createDirectory() {
        try {
            Files.createDirectories(Path.of(FILE_PATH));
        } catch (IOException e) {
            System.err.println("Failed to create directory: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates sensor data files for each reservoir with the specified number of data points.
     * Each reservoir will have its initial water level set randomly and will either be filling up or depleting.
     * The generated data includes two sensor readings for each data point, simulating bottom and top sensors.
     *
     * @param nrOfDataPoints the number of sensor data points to generate for each reservoir.
     */
    private static void generateDataForAllReservoirs(int nrOfDataPoints) {
        for (int i = 1; i <= NUMBER_OF_RESERVOIRS; i++) {
            int waterLevelIndex = determineInitialWaterLevel();

            // Generate sensor data for the bottom sensor of the reservoir.
            generateAndWriteSensorData(i, waterLevelIndex, nrOfDataPoints, "bottom");

            // Generate sensor data for the top sensor of the reservoir.
            generateAndWriteSensorData(i, waterLevelIndex, nrOfDataPoints, "top");
        }
    }

    /**
     * Determines the initial water level for a reservoir, represented as an index.
     * The water level index is random and can be any value from 0 (empty) to 4 (full).
     *
     * @return an integer representing the initial water level index.
     */
    private static int determineInitialWaterLevel() {
        return new Random().nextInt(FULL_RESERVOIR + 1);
    }

    /**
     * Determines whether the reservoir should start by filling up based on the initial water level index.
     * If the initial water level index is 0 (the reservoir is empty), it should start filling.
     *
     * @param waterLevelIndex the initial water level index of the reservoir.
     * @return true if the reservoir should start filling, false otherwise.
     */
    private static boolean shouldStartFilling(int waterLevelIndex) {
        return waterLevelIndex == 0;
    }

    /**
     * Generates sensor data for a single reservoir and writes it to a file.
     * The data simulates either a filling or depleting process based on the initial water level.
     * The sensor type can be either "bottom" or "top", indicating the sensor's position in the reservoir.
     *
     * @param reservoirNr       the reservoir number for which to generate the data.
     * @param initialWaterLevel the initial water level index of the reservoir.
     * @param nrOfDataPoints    the number of data points to generate for the reservoir.
     * @param sensorPosition    the type of sensor ("bottom" or "top") for which to generate the data.
     */
    private static void generateAndWriteSensorData(int reservoirNr, int initialWaterLevel, int nrOfDataPoints, String sensorPosition) {
        // Re-evaluate the filling status based on the initial water level before starting data generation.
        boolean isFilling = shouldStartFilling(initialWaterLevel);

        // Simulate the sensor data based on the type of sensor and initial conditions.
        String sensorData = simulateSensorData(sensorPosition.equals("bottom"), initialWaterLevel, nrOfDataPoints, isFilling);

        // Write the generated sensor data to a file named according to the reservoir number and sensor type.
        writeSensorDataToFile(reservoirNr, sensorPosition, sensorData);
    }

    /**
     * Simulates the sensor data for a given number of data points, initial water level, and filling status.
     * The simulation accounts for the type of sensor (bottom or top) to determine the sensor's state at each data point.
     *
     * @param isBottomSensor      flag indicating whether the sensor is at the bottom of the reservoir.
     * @param initialWaterLevel   the initial water level index of the reservoir.
     * @param nrOfDataPoints      the number of data points to simulate.
     * @param isFilling           flag indicating whether the reservoir is filling up or not.
     * @return a string representing the simulated sensor data.
     */
    private static String simulateSensorData(boolean isBottomSensor, int initialWaterLevel, int nrOfDataPoints, boolean isFilling) {
        StringBuilder sensorData = new StringBuilder();
        int waterLevelIndex = initialWaterLevel;

        // Loop through the number of data points to simulate the sensor data.
        for (int j = 0; j < nrOfDataPoints; j++) {
            WaterLevel waterLevel = WaterLevel.values()[waterLevelIndex];

            // Determine the state of the sensor based on its position and current water level.
            boolean sensorState = determineSensorState(isBottomSensor, waterLevel);

            // Format the current water level and sensor state into a string and append it to the sensor data.
            sensorData.append(formatSensorData(waterLevel, sensorState));

            // Update the water level index for the next data point.
            waterLevelIndex = updateWaterLevelIndex(waterLevelIndex, isFilling);

            // Update the filling status if the water level reaches the top or bottom.
            isFilling = updateFillingStatus(waterLevelIndex, isFilling);
        }

        return sensorData.toString();
    }

    /**
     * Determines the state of the sensor based on its position and the current water level.
     * For a bottom sensor, the state is true (activated) for any water level except EMPTY.
     * For a top sensor, the state is true only when the water level is FULL.
     *
     * @param isBottomSensor flag indicating if the sensor is at the bottom of the reservoir.
     * @param waterLevel     the current water level of the reservoir.
     * @return boolean indicating the state of the sensor.
     */
    private static boolean determineSensorState(boolean isBottomSensor, WaterLevel waterLevel) {
        return (isBottomSensor && waterLevel != WaterLevel.EMPTY) || (!isBottomSensor && waterLevel == WaterLevel.FULL);
    }

    /**
     * Formats the sensor data into a string representation.
     * This method takes the water level and sensor state and converts them into a human-readable string,
     * with the water level represented as a percentage and the sensor state as a boolean value.
     *
     * @param waterLevel   the water level to format.
     * @param sensorState  the sensor state to format.
     * @return a formatted string representing the sensor data.
     */
    private static String formatSensorData(WaterLevel waterLevel, boolean sensorState) {
        return String.format("%s%% %s%n", waterLevel.ordinal() * 25, sensorState);
    }

    /**
     * Updates the water level index for the next data point.
     * If the reservoir is filling, the water level index is incremented, up to the maximum represented by FULL_RESERVOIR.
     * If the reservoir is depleting, the water level index is decremented, not falling below 0.
     *
     * @param waterLevelIndex the current water level index.
     * @param isFilling       flag indicating whether the reservoir is filling up or not.
     * @return the updated water level index.
     */
    private static int updateWaterLevelIndex(int waterLevelIndex, boolean isFilling) {
        return isFilling ? waterLevelIndex + 1 : waterLevelIndex - 1;
    }

    /**
     * Updates the filling status of the reservoir based on the current water level index.
     * If the reservoir is filling and reaches the FULL mark, it switches to depleting.
     * If the reservoir is depleting and reaches EMPTY, it switches to filling.
     *
     * @param waterLevelIndex the current water level index.
     * @param isFilling       flag indicating whether the reservoir is currently filling up or not.
     * @return true if the reservoir should continue filling, false if it should deplete.
     */
    private static boolean updateFillingStatus(int waterLevelIndex, boolean isFilling) {
        if (isFilling && waterLevelIndex == FULL_RESERVOIR) {
            // Switch to depleting once the reservoir is full.
            return false;
        } else if (!isFilling && waterLevelIndex == 0) {
            // Switch to filling once the reservoir is empty.
            return true;
        }
        return isFilling;
    }

    /**
     * Writes the generated sensor data to a file named according to the reservoir number and sensor type.
     * The file is created in the directory specified by FILE_PATH.
     *
     * @param reservoirNr the reservoir number for which the data is being written.
     * @param sensorPosition  the location of the sensor ("bottom" or "top") for which the data is written.
     * @param sensorData  the string representation of the sensor data to write to the file.
     * @throws RuntimeException if there is an issue writing to the file.
     */
    private static void writeSensorDataToFile(int reservoirNr, String sensorPosition, String sensorData) {
        String filename = FILE_PATH + "reservoir_" + reservoirNr + "_" + sensorPosition + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(sensorData);
        } catch (IOException e) {
            System.err.println("Failed to write data to file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}