package consumers;

import config.ConsumerConfigFactory;
import config.SensorVal;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;

public class ParcelConsumer implements Runnable {
    private final String TOPIC;
    private final String BOOTSTRAP_SERVERS;
    private final String GROUP_ID;
    private final int PARCEL_NR;

    public ParcelConsumer(String topic, String bootstrapServers, String groupId, int parcelNr) {
        this.TOPIC = topic;
        this.BOOTSTRAP_SERVERS = bootstrapServers;
        this.GROUP_ID = groupId;
        this.PARCEL_NR = parcelNr;
    }

    @Override
    public void run() {
        try(Consumer<String, SensorVal> consumer = new KafkaConsumer<>(ConsumerConfigFactory.createConsumerConfig(BOOTSTRAP_SERVERS, GROUP_ID, 2))) {
            consumer.subscribe(Collections.singletonList(TOPIC));

            while (true) {
                ConsumerRecords<String, SensorVal> records = consumer.poll(Duration.ofMillis(100));

                records.forEach(record -> {
                    if (record.key().startsWith("parcel_" + PARCEL_NR)) {
                        System.out.println(record.key());
                        //processRecord(record.value());
                    }
                });
            }
        }
    }

    private void processRecord(SensorVal val) {
        System.out.println(val);
    }
}
