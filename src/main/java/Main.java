import producers.ProducerConfigFactory;
import producers.ReservoirProducer;

public class Main {
    private static final String BOOTSTRAP_SERVERS = "localhost:19092,localhost:29092,localhost:39092";
    public static void main(String[] args) {
        ProducerConfigFactory producer = new ProducerConfigFactory(BOOTSTRAP_SERVERS);
        ReservoirProducer.startReservoirProducer(producer);
        producer.closeProducer();
    }
}