package producers;

import org.apache.kafka.clients.producer.*;
import java.util.Properties;

public class ProducerConfig {
    private Producer<String, String> producer;

    public ProducerConfig() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "kafka1:19092,kafka2:29092,kafka3:39092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        this.producer = new KafkaProducer<>(props);
    }

    public void sendSensorData(String topic, String key, String value) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

        producer.send(record, (metadata, e) -> {
            if (e != null) {
                e.printStackTrace();
            }
        });
    }

    public void close() {
        producer.close();
    }
}
