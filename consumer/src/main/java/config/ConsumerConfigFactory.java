package config;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

public class ConsumerConfigFactory {
    private final Consumer<String, String> consumer;

    public ConsumerConfigFactory(String bootstrapServers) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        this.consumer = new KafkaConsumer<>(props);
    }
}
