import consumers.ReservoirConsumer;

public class Main {
    public static void main(String[] args) {
        final String BOOTSTRAP_SERVERS = "localhost:19092,localhost:29092,localhost:39092";
        final String TOPIC = "water-levels";
        final int NR_OF_CONSUMERS = 7;

        for (int i = 1; i <= NR_OF_CONSUMERS; i++) {
            String groupId = "reservoir-group-" + i;
            new Thread(new ReservoirConsumer(TOPIC, BOOTSTRAP_SERVERS, groupId, i)).start();
        }
    }
}
