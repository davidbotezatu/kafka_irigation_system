import consumers.ReservoirConsumer;

public class Main {
    private static final String BOOTSTRAP_SERVERS = "localhost:19092,localhost:29092,localhost:39092";

    public static void main(String[] args) {
        for (int i = 1; i <= 7; i++) {
            String groupId = "reservoir-group-" + i;
            ReservoirConsumer consumer = new ReservoirConsumer(i, BOOTSTRAP_SERVERS, groupId);
            Thread consumerThread = new Thread(consumer);
            consumerThread.start();
        }
    }
}
