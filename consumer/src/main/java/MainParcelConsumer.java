import consumers.ParcelConsumer;

public class MainParcelConsumer {
    public static void main(String[] args) {
        final String BOOTSTRAP_SERVERS = "localhost:19092,localhost:29092,localhost:39092";
        final String TOPIC = "parcel-data";
        final int NR_OF_CONSUMERS = 3;

        for (int i = 1; i <= NR_OF_CONSUMERS; i++) {
            String groupId = "parcel-group-" + i;
            new Thread(new ParcelConsumer(TOPIC, BOOTSTRAP_SERVERS, groupId, i)).start();
        }
    }
}
