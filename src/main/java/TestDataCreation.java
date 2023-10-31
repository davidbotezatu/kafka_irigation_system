import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class TestDataCreation {
    private static String currentDir = System.getProperty("user.dir");
    private static String filePath = currentDir + File.separator + "test_data" + File.separator + "test_data_";
    private static Random random = new Random();

    public static void main(String[] args) {
        //create data for the 7 reservoirs
        for (int i=1; i<8; i++) {
            //create data for the bottom sensor (min water level)
            createReservoirData(i,"top", 100);
            createReservoirData(i,"bottom", 100);
        }

        for (int i=1; i<4; i++) {
            createParcelData(i, 100);
        }

        //create data for the parcels
    }

    private static void createReservoirData(int reservoirNr, String sensorPlacement, int nrOfLines) {
        File file = new File(filePath + "reservoir_" + reservoirNr + "_" + sensorPlacement + ".txt");
        dataCreation(file, nrOfLines, "reservoir");
    }

    private static void createParcelData(int parcelNr, int nrOfLines) {
        File file = new File(filePath + "parcel_" + parcelNr + ".txt");
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
                        writer.write(String.valueOf(random.nextBoolean()));
                    } else if (dataType.equals("parcel")) {
                        writer.write(String.valueOf(random.nextInt(50 - 10) + 10));
                        writer.write("\n");
                        writer.write(String.valueOf(random.nextInt(25 - 1) + 1));
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
