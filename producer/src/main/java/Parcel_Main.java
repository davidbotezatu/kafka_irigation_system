import config.ProducerConfigFactory;
import producers.ParcelProducer;
import utils.ParcelsTestDataCreation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Parcel_Main {
    private static final String BOOTSTRAP_SERVERS = "localhost:19092,localhost:29092,localhost:39092";
    private static final String PARCEL_TOPIC = "parcel-data";
    private static final long SLEEP_DURATION = 1000; // Sleep duration between messages - for testing purposes
    private static final Path PARCEL_DATA_DIR = Paths.get(System.getProperty("user.dir"), "parcels_test_data");

    public static void main(String[] args) {
        // Create test data
        ParcelsTestDataCreation.createData(100);

        //type 1 for reservoir producer, type 2 for the parcel producers
        Properties parcelProps = ProducerConfigFactory.createProducerConfig(BOOTSTRAP_SERVERS, 2);

        //Start one producer for each parcel
        for (int i = 1; i <= 3; i++) {
            Path parcelData = PARCEL_DATA_DIR.resolve("parcel_" + i + ".txt");

            new Thread(new ParcelProducer(PARCEL_TOPIC, parcelData, parcelProps, SLEEP_DURATION)).start();
        }
    }
}
