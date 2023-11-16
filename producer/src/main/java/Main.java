import config.ProducerConfigFactory;
import producers.ReservoirProducer;
import utils.ParcelsTestDataCreation;
import utils.TestDataCreation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Main {
    private static final String BOOTSTRAP_SERVERS = "localhost:19092,localhost:29092,localhost:39092";
    private static final String TOPIC = "water-levels";
    private static final long SLEEP_DURATION = 1000; // Sleep duration between messages - for testing purposes
    private static final Path DATA_DIRECTORY = Paths.get(System.getProperty("user.dir"), "test_data");
    private static final Path PARCEL_DATA_DIR = Paths.get(System.getProperty("user.dir"), "parcels_test_data");

    public static void main(String[] args) {
        // Create test data
        TestDataCreation.createData(100);
        ParcelsTestDataCreation.createData(100);

        Properties producerProps = ProducerConfigFactory.createProducerConfig(BOOTSTRAP_SERVERS, 1);

        // Start one producer for each reservoir
        for (int i = 1; i <= 7; i++) {
            Path topSensorFilePath = DATA_DIRECTORY.resolve("reservoir_" + i + "_top.txt");
            Path bottomSensorFilePath = DATA_DIRECTORY.resolve("reservoir_" + i + "_bottom.txt");

            // Start producer for both sensors
            new Thread(new ReservoirProducer(
                    TOPIC,
                    topSensorFilePath,
                    bottomSensorFilePath,
                    producerProps,
                    SLEEP_DURATION
            )).start();
        }

        //Start one producer for each parcel
        for (int i = 1; i <= 3; i++) {
            Path parcelData = PARCEL_DATA_DIR.resolve("parcel_" + i + ".txt");
        }
    }
}
