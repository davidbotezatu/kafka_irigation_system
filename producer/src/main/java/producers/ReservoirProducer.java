package producers;

import config.ProducerConfigFactory;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;
import java.util.stream.Stream;

/**
 * This class handles the production of sensor data for reservoir water levels.
 * It reads the test data from files and publishes the data to a specified topic.
 * The class uses a configurable producer to send the data, which allows for flexibility
 * in how data is handled and sent to consumers.
 */
public class ReservoirProducer {
    private static final String TOPIC = "water-levels";
    private static final Path DATA_DIRECTORY = Paths.get(System.getProperty("user.dir"), "test_data");
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("reservoir_(\\d+)_(top|bottom)\\.txt");

    /**
     * Starts the process of reading sensor data files and publishing their contents to a topic.
     * The method streams through the data directory, filters for files that match the naming pattern,
     * and processes each file sequentially.
     *
     * @param producer      the ProducerConfigFactory instance used for sending sensor data.
     * @param sleepDuration the time in milliseconds to pause between sending each line of data.
     */
    public static void startReservoirProducer(ProducerConfigFactory producer, long sleepDuration) {
        try (Stream<Path> paths = Files.list(DATA_DIRECTORY)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> FILE_NAME_PATTERN.matcher(path.getFileName().toString()).matches())
                    .forEach(path -> processFile(producer, path, sleepDuration));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes a single file of sensor data, sending each line of the file as a message to the topic.
     * The file's name is parsed to construct a key representing the reservoir number and sensor position.
     * Each line of the file is sent as a separate message, with a pause between each send operation.
     *
     * @param producer      the ProducerConfigFactory instance used for sending sensor data.
     * @param filePath      the Path object representing the file to be processed.
     * @param sleepDuration the time in milliseconds to pause between sending each line of data.
     */
    private static void processFile(ProducerConfigFactory producer, Path filePath, long sleepDuration) {
        Matcher matcher = FILE_NAME_PATTERN.matcher(filePath.getFileName().toString());

        if (matcher.matches()) {
            String reservoirNumber = matcher.group(1);
            String sensorPosition = matcher.group(2);
            String key = "reservoir_" + reservoirNumber + "_" + sensorPosition;

            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String value;
                while ((value = reader.readLine()) != null) {
                    producer.sendSensorData(TOPIC, key, value);
                    Thread.sleep(sleepDuration);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
