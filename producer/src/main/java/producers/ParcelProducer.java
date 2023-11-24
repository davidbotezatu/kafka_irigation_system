package producers;

import config.SensorVal;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParcelProducer implements Runnable {
    private final String topic;
    private final Path parcelSensorsFilePath;
    private final Properties producerProps;
    private final long sleepDuration;

    public ParcelProducer(String topic, Path parcelDataFilePath, Properties producerProps, long sleepDuration) {
        this.topic = topic;
        this.parcelSensorsFilePath = parcelDataFilePath;
        this.producerProps = producerProps;
        this.sleepDuration = sleepDuration;
    }

    @Override
    public void run() {
        try (BufferedReader reader = Files.newBufferedReader(parcelSensorsFilePath)) {
            Producer<String, SensorVal> producer = new KafkaProducer<>(producerProps);

            String line;
            while (((line = reader.readLine()) != null)) {
                int temp = extractSensorValue(line);
                int humid = extractSensorValue(reader.readLine());

                sendSensorData(producer, parcelSensorsFilePath.getFileName().toString().replace(".txt", ""), new SensorVal(temp, humid));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Integer extractSensorValue(String line) {
        Pattern pattern = Pattern.compile("[TH](\\d+)");
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        return null;
    }

    private void sendSensorData(Producer<String, SensorVal> producer, String key, SensorVal value) throws InterruptedException {
        ProducerRecord<String, SensorVal> record = new ProducerRecord<>(topic, key, value);
        producer.send(record);
        Thread.sleep(sleepDuration);
    }
}
