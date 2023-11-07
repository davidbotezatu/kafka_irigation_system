import config.ProducerConfigFactory;
import utils.TestDataCreation;
import producers.ReservoirProducer;

public class Main {
    private static final String BOOTSTRAP_SERVERS = "localhost:19092,localhost:29092,localhost:39092";
    public static void main(String[] args) {
        //create test data
        TestDataCreation.createData(100);

        ProducerConfigFactory producer = new ProducerConfigFactory(BOOTSTRAP_SERVERS);
        ReservoirProducer.startReservoirProducer(producer, 1000);
        producer.closeProducer();
    }
}