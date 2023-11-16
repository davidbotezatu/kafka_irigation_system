package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class ParcelsTestDataCreation {
    private static final int NR_OF_PARCELS = 3;
    private static final Path DATA_DIRECTORY = Path.of(System.getProperty("user.dir"), "parcels_test_data");

    public static void createData(int nrOfDataPoints) {
        System.out.println("Start PARCEL data creation");
        createDirectory();
        generateDataForParcels(nrOfDataPoints);
        System.out.println("Finished PARCEL data creation");
    }

    private static void createDirectory() {
        try {
            Files.createDirectory(DATA_DIRECTORY);
        } catch (IOException e) {
            System.err.println("Failed to create directory: " + e);
        }
    }

    private static void generateDataForParcels(int nrOfDataPoints) {
        for (int i = 1; i <= NR_OF_PARCELS; i++) {
            generateData(nrOfDataPoints, i);
        }
    }

    private static void generateData(int nrOfDataPoints, int parcelNr) {
        Random random = new Random();
        StringBuilder data = new StringBuilder();

        for (int i = 0; i < nrOfDataPoints; i++) {
            data.append(String.format("%s%d%n", "T", random.nextInt(81)));
            data.append(String.format("%s%d%n", "H", random.nextInt(101)));
        }

        //max temp detected by a dht22 is 80 degrees
        writeDataToFile(parcelNr, String.valueOf(data));
        writeDataToFile(parcelNr, String.valueOf(data));
    }

    private static void writeDataToFile(int parcelNr, String data) {
        String filename = DATA_DIRECTORY + File.separator + "parcel_" + parcelNr + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(data);
        } catch (IOException e) {
            System.err.println("Error writing parcel data to file: " + e);
            throw new RuntimeException(e);
        }
    }
}
