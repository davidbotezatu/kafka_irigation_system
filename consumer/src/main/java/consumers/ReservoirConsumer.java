package consumers;

import config.ConsumerConfigFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReservoirConsumer implements Runnable {
    private static final String TOPIC = "water-levels";
    private final Consumer<String, String> consumer;
    private final int reservoirNumber;
    private final Pattern keyPattern;

    public ReservoirConsumer(int reservoirNumber, String bootstrapServers, String groupId) {
        this.reservoirNumber = reservoirNumber;
        ConsumerConfigFactory factory = new ConsumerConfigFactory(bootstrapServers, groupId);
        this.consumer = factory.getConsumer();
        this.consumer.subscribe(Collections.singletonList(TOPIC));
        this.keyPattern = Pattern.compile("reservoir_" + reservoirNumber + "_(top|bottom)");
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    processRecord(record);
                }
            }
        } finally {
            consumer.close();
        }
    }

    private void processRecord(ConsumerRecord<String, String> record) {
        Matcher matcher = keyPattern.matcher(record.key());
        if (matcher.matches()) {
            String sensorPosition = matcher.group(1);
            String sensorValue = record.value();

            if ("bottom".equals(sensorPosition) && "false".equals(sensorValue)) {
                System.out.println("Pumps started for Reservoir " + reservoirNumber);
            } else if ("top".equals(sensorPosition) && "true".equals(sensorValue)) {
                System.out.println("Pumps stopped for Reservoir " + reservoirNumber);
            }
        }
    }

}
