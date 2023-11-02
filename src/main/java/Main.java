import producers.ProducerConfigFactory;
import producers.ReservoirProducer;

public class Main {
    private static final String BOOTSTRAP_SERVERS = "kafka1:9092,kafka2:9092,kafka3:9092";
    public static void main(String[] args) {
        //create test data
        TestDataCreation.createData(100);

        ProducerConfigFactory producer = new ProducerConfigFactory(BOOTSTRAP_SERVERS);
        ReservoirProducer.startReservoirProducer(producer);
        producer.closeProducer();
    }
}