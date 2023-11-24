package producers;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import utils.ReservoirStateService;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ReservoirProducer implements Runnable {
    private final String topic;
    private final Path topSensorFilePath;
    private final Path bottomSensorFilePath;
    private final Properties producerProps;
    private final long sleepDuration;
    private ReservoirStateService reservoirStateService;

    public ReservoirProducer(String topic, Path topSensorFilePath, Path bottomSensorFilePath, Properties producerProps, long sleepDuration) {
        this.topic = topic;
        this.topSensorFilePath = topSensorFilePath;
        this.bottomSensorFilePath = bottomSensorFilePath;
        this.producerProps = producerProps;
        this.sleepDuration = sleepDuration;
    }

    @Override
    public void run() {
        try (Producer<String, String> producer = new KafkaProducer<>(producerProps);
             BufferedReader topReader = Files.newBufferedReader(topSensorFilePath);
             BufferedReader bottomReader = Files.newBufferedReader(bottomSensorFilePath)) {

            String topValue;
            String bottomValue;
            while (((topValue = topReader.readLine()) != null) && ((bottomValue = bottomReader.readLine()) != null)) {
                // Send bottom sensor data
                sendSensorData(producer, bottomSensorFilePath.getFileName().toString().replace(".txt", ""), bottomValue);

                // Send top sensor data
                sendSensorData(producer, topSensorFilePath.getFileName().toString().replace(".txt", ""), topValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendSensorData(Producer<String, String> producer, String key, String value) throws InterruptedException {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
        producer.send(record);
        updateReservoirStateService(key, value);
        Thread.sleep(sleepDuration);
    }

    private void updateReservoirStateService(String key, String value) {
        String reservoirId = removeLocation(key);

        String[] parts = value.split(" ");
        boolean isWaterDetected = Boolean.parseBoolean(parts[1]);

        reservoirStateService.updateReservoirState(reservoirId, isWaterDetected);
    }

    private String removeLocation(String key) {
        if (key.endsWith("_top")) {
            return key.substring(0, key.length() - 4);
        }

        return key.substring(0, key.length() - 7);
    }
}
