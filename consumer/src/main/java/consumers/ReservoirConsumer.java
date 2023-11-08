package consumers;

import config.ConsumerConfigFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;

public class ReservoirConsumer implements Runnable {
    private final String topic;
    private final String bootstrapServers;
    private final String groupId;
    private final int reservoirNr;

    public ReservoirConsumer(String topic, String bootstrapServers, String groupId, int reservoirNr) {
        this.topic = topic;
        this.bootstrapServers = bootstrapServers;
        this.groupId = groupId;
        this.reservoirNr = reservoirNr;
    }

    @Override
    public void run() {
        try (Consumer<String, String> consumer = new KafkaConsumer<>(ConsumerConfigFactory.createConsumerConfig(bootstrapServers, groupId))) {
            consumer.subscribe(Collections.singletonList(topic));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                records.forEach(record -> {
                    if (record.key().startsWith("reservoir_" + reservoirNr)) {
                        processRecord(record.key(), record.value());
                    }
                });
            }
        }
    }

    private void processRecord(String key, String value) {
        String sensorPosition = key.substring(key.lastIndexOf("_") + 1);
        String[] parts = value.split(" ");
        boolean isWaterDetected = Boolean.parseBoolean(parts[1]);

        if (sensorPosition.equals("bottom") && !isWaterDetected) {
            System.out.println("Pumps started for reservoir " + reservoirNr);
        } else if (sensorPosition.equals("top") && isWaterDetected) {
            System.out.println("Pumps stopped for reservoir " + reservoirNr);
        }
    }
}