package config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class TestDataCreation {
    private static final String CURRENT_DIR = System.getProperty("user.dir");
    private static final String FILE_PATH = CURRENT_DIR + File.separator + "test_data" + File.separator + "test_data_";
    private static final Random RANDOM = new Random();

    public static void createData(int nrOfLines) {
        System.out.println("data created");

        try {
            Files.createDirectories(Path.of(CURRENT_DIR + File.separator + "test_data"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //create data for the 7 reservoirs
        for (int i=1; i<8; i++) {
            //create data for the bottom sensor (min water level)
            createReservoirData(i,"top", nrOfLines);
            createReservoirData(i,"bottom", nrOfLines);
        }

        for (int i=1; i<4; i++) {
            createParcelData(i, nrOfLines);
        }

        //create data for the parcels
    }

    private static void createReservoirData(int reservoirNr, String sensorPlacement, int nrOfLines) {
        File file = new File(FILE_PATH + "reservoir_" + reservoirNr + "_" + sensorPlacement + ".txt");
        dataCreation(file, nrOfLines, "reservoir");
    }

    private static void createParcelData(int parcelNr, int nrOfLines) {
        File file = new File(FILE_PATH + "parcel_" + parcelNr + ".txt");
        dataCreation(file, nrOfLines, "parcel");
    }

    private static void dataCreation(File file, int nrOfLines, String dataType) {
        try {
            if (file.exists()) {
                file.delete();
                System.out.println("Old file deleted");
            }

            if (file.createNewFile()) {
                FileWriter writer = new FileWriter(file);

                for (int j=0; j<nrOfLines; j++) {
                    if (dataType.equals("reservoir")) {
                        writer.write(String.valueOf(RANDOM.nextBoolean()));
                    } else if (dataType.equals("parcel")) {
                        writer.write(String.valueOf(RANDOM.nextInt(50 - 10) + 10));
                        writer.write("\n");
                        writer.write(String.valueOf(RANDOM.nextInt(25 - 1) + 1));
                    }

                    if (j < nrOfLines-1) {
                        writer.write("\n");
                    }
                }
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
